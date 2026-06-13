package com.hyuchiha.village_defense.Database.Databases;

import com.hyuchiha.village_defense.Game.Kit;
import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Output.Output;
import com.zaxxer.hikari.HikariConfig;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SQLiteDB extends SQLDB {

  private final Plugin plugin;

  public SQLiteDB(Main plugin) {
    super(plugin);
    this.plugin = plugin;
  }

  @Override
  protected HikariConfig buildHikariConfig() {
    File dataFolder = new File(plugin.getDataFolder(), "database.db");

    if (!dataFolder.exists()) {
      try {
        dataFolder.createNewFile();
      } catch (IOException e) {
        Output.logError("File write error: database.db");
      }
    }

    HikariConfig hc = new HikariConfig();
    hc.setJdbcUrl("jdbc:sqlite:" + dataFolder.getAbsolutePath());
    hc.setDriverClassName("org.sqlite.JDBC");
    hc.setPoolName("VillageDefense-SQLite");
    // SQLite serializa escrituras a nivel de archivo. Pool de tamaño 1 mantiene
    // todo simple y evita errores "database is locked" bajo contencion.
    hc.setMaximumPoolSize(1);
    hc.setMinimumIdle(1);
    hc.setConnectionTimeout(10_000);
    return hc;
  }

  @Override
  protected String getDatabaseQuery() {
    return "CREATE TABLE IF NOT EXISTS `" + ACCOUNTS_TABLE + "` (" +
        "  `uuid` varchar(36) NOT NULL PRIMARY KEY," +
        "  `username` varchar(16) NOT NULL," +
        "  `kills` int(16) NOT NULL," +
        "  `deaths` int(16) NOT NULL," +
        "  `bosses_kills` int(16) NOT NULL," +
        "  `max_wave_reached` int(16) NOT NULL," +
        "  `min_wave_reached` int(16) NOT NULL" +
        ");";
  }

  @Override
  protected String getDatabaseKitsQuery() {
    return "CREATE TABLE IF NOT EXISTS `" + KITS_TABLE + "` (" +
        "  `clv_kit` INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE," +
        "  `name` varchar(45) NOT NULL" +
        ")";
  }

  @Override
  protected String getDatabaseKitsUnlockedQuery() {
    return "CREATE TABLE IF NOT EXISTS `" + KITS_UNLOCKED_TABLE + "` "
        + "(`clv_kit` int(6) NOT NULL,"
        + "`player` varchar(36) NOT NULL, "
        + "PRIMARY KEY (`clv_kit` , `player` ) ,"
        + "FOREIGN KEY (`clv_kit`) REFERENCES " + KITS_TABLE + "(`clv_kit`), "
        + "FOREIGN KEY (`player`) REFERENCES " + ACCOUNTS_TABLE + "(`uuid`) );";
  }

  @Override
  protected String getInsertKitQuery(Kit kit) {
    return "INSERT OR IGNORE INTO `" + KITS_TABLE + "`(`name`)  VALUES "
        + "('" + kit.name().toUpperCase() + "');";
  }

  @Override
  protected List<String> getIndexQueries() {
    // SQLite soporta CREATE INDEX IF NOT EXISTS, asi que son idempotentes de por si.
    return Arrays.asList(
        // Ownership de kit: getKitsFromAccount filtra WHERE player = ?
        // (la PK compuesta (clv_kit, player) no sirve para un filtro solo-player).
        "CREATE INDEX IF NOT EXISTS `idx_ku_player` ON `" + KITS_UNLOCKED_TABLE + "` (`player`)",
        // getIdOfElement filtra WHERE name = ?; ademas fuerza unicidad del nombre.
        "CREATE UNIQUE INDEX IF NOT EXISTS `idx_kits_name` ON `" + KITS_TABLE + "` (`name`)",
        // Leaderboards: ORDER BY <stat> DESC LIMIT n.
        "CREATE INDEX IF NOT EXISTS `idx_acc_kills` ON `" + ACCOUNTS_TABLE + "` (`kills`)",
        "CREATE INDEX IF NOT EXISTS `idx_acc_deaths` ON `" + ACCOUNTS_TABLE + "` (`deaths`)",
        "CREATE INDEX IF NOT EXISTS `idx_acc_bosses_kills` ON `" + ACCOUNTS_TABLE + "` (`bosses_kills`)",
        "CREATE INDEX IF NOT EXISTS `idx_acc_max_wave` ON `" + ACCOUNTS_TABLE + "` (`max_wave_reached`)",
        "CREATE INDEX IF NOT EXISTS `idx_acc_min_wave` ON `" + ACCOUNTS_TABLE + "` (`min_wave_reached`)"
    );
  }

  @Override
  protected String getCreateAccountQuery() {
    return "INSERT OR IGNORE INTO `" + ACCOUNTS_TABLE + "` (`uuid`, `username`, `kills`, "
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
}
