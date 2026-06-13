package com.hyuchiha.village_defense.Database.Databases;

import com.hyuchiha.village_defense.Game.Kit;
import com.hyuchiha.village_defense.Main;
import com.zaxxer.hikari.HikariConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;

public class MySQLDB extends SQLDB {

  private final Plugin plugin;

  public MySQLDB(Main plugin) {
    super(plugin);
    this.plugin = plugin;
  }

  @Override
  protected HikariConfig buildHikariConfig() {
    ConfigurationSection config = getConfigSection();

    HikariConfig hc = new HikariConfig();
    String url = "jdbc:mysql://" + config.getString("host") + ":" + config.getString("port") + "/"
        + config.getString("name")
        + "?useSSL=false&autoReconnect=true&allowPublicKeyRetrieval=true";

    hc.setJdbcUrl(url);
    hc.setUsername(config.getString("user"));
    hc.setPassword(config.getString("pass"));
    hc.setDriverClassName("com.mysql.cj.jdbc.Driver");
    hc.setPoolName("VillageDefense-MySQL");
    // Pool acotado para un server Spigot: el pico real es un puñado de guardados
    // concurrentes al terminar una partida; ajustable via config.
    hc.setMaximumPoolSize(config.getInt("pool-size", 10));
    hc.setMinimumIdle(2);
    hc.setConnectionTimeout(10_000);
    hc.setIdleTimeout(600_000);
    hc.setMaxLifetime(1_800_000);
    return hc;
  }

  @Override
  protected String getDatabaseQuery() {
    return "CREATE TABLE IF NOT EXISTS `" + ACCOUNTS_TABLE + "` ("
        + "  `uuid` varchar(36) NOT NULL,"
        + "  `username` varchar(16) NOT NULL,"
        + "  `kills` int(16) NOT NULL,"
        + "  `deaths` int(16) NOT NULL,"
        + "  `bosses_kills` int(16) NOT NULL,"
        + "  `max_wave_reached` int(16) NOT NULL,"
        + "  `min_wave_reached` int(16) NOT NULL,"
        + "  PRIMARY KEY (`uuid`)"
        + ") ENGINE=InnoDB;";
  }

  @Override
  protected String getDatabaseKitsQuery() {
    return "CREATE TABLE IF NOT EXISTS `" + KITS_TABLE + "` ( "
        + "`clv_kit` int(6) NOT NULL AUTO_INCREMENT,"
        + "`name` varchar(45) NOT NULL,"
        + "PRIMARY KEY (`clv_kit`), "
        + "UNIQUE KEY `clv_kit` (`clv_kit`) ) "
        + "ENGINE=InnoDB AUTO_INCREMENT=1";
  }

  @Override
  protected String getDatabaseKitsUnlockedQuery() {
    return "CREATE TABLE IF NOT EXISTS `" + KITS_UNLOCKED_TABLE + "` "
        + "(`clv_kit` int(6) NOT NULL,"
        + "`player` varchar(36) NOT NULL, "
        + "PRIMARY KEY (`clv_kit` , `player` ) ,"
        + "FOREIGN KEY (`clv_kit`) REFERENCES " + KITS_TABLE + "(`clv_kit`), "
        + "FOREIGN KEY (`player`) REFERENCES " + ACCOUNTS_TABLE + "(`uuid`) )  "
        + "ENGINE=InnoDB;";
  }

  @Override
  protected String getInsertKitQuery(Kit kit) {
    return "INSERT IGNORE INTO `" + KITS_TABLE + "`(`name`)  VALUES "
        + "('" + kit.name().toUpperCase() + "');";
  }

  @Override
  protected List<String> getIndexQueries() {
    // MySQL no tiene CREATE INDEX IF NOT EXISTS; SQLDB.init() traga el error de
    // nombre-de-indice-duplicado (1061) cuando el indice ya existe.
    return Arrays.asList(
        // Ownership de kit: getKitsFromAccount filtra WHERE player = ?
        // (la PK compuesta (clv_kit, player) no sirve para un filtro solo-player).
        "CREATE INDEX `idx_ku_player` ON `" + KITS_UNLOCKED_TABLE + "` (`player`)",
        // getIdOfElement filtra WHERE name = ?; ademas fuerza unicidad del nombre.
        "CREATE UNIQUE INDEX `idx_kits_name` ON `" + KITS_TABLE + "` (`name`)",
        // Leaderboards: ORDER BY <stat> DESC LIMIT n.
        "CREATE INDEX `idx_acc_kills` ON `" + ACCOUNTS_TABLE + "` (`kills`)",
        "CREATE INDEX `idx_acc_deaths` ON `" + ACCOUNTS_TABLE + "` (`deaths`)",
        "CREATE INDEX `idx_acc_bosses_kills` ON `" + ACCOUNTS_TABLE + "` (`bosses_kills`)",
        "CREATE INDEX `idx_acc_max_wave` ON `" + ACCOUNTS_TABLE + "` (`max_wave_reached`)",
        "CREATE INDEX `idx_acc_min_wave` ON `" + ACCOUNTS_TABLE + "` (`min_wave_reached`)"
    );
  }

  @Override
  protected String getCreateAccountQuery() {
    return "INSERT IGNORE INTO `" + ACCOUNTS_TABLE + "` (`uuid`, `username`, `kills`, "
        + "`deaths`, `bosses_kills`, `max_wave_reached`, `min_wave_reached`) VALUES (?, ?, 0, 0, 0, 0, 0);";
  }

  @Override
  protected String getUpdateAccountQuery() {
    return "UPDATE `" + ACCOUNTS_TABLE + "` SET "
        + "`username`=?, "
        + "`kills`=?, "
        + "`deaths`=?, "
        + "`bosses_kills`=?, "
        + "`max_wave_reached`=?, "
        + "`min_wave_reached`=? "
        + "WHERE `uuid`=?;";
  }

  private ConfigurationSection getConfigSection() {
    return plugin.getConfig().getConfigurationSection("Database");
  }
}
