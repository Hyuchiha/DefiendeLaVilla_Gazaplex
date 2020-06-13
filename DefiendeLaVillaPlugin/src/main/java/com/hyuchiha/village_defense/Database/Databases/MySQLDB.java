package com.hyuchiha.village_defense.Database.Databases;

import com.hyuchiha.village_defense.Database.Base.Account;
import com.hyuchiha.village_defense.Game.Kit;
import com.hyuchiha.village_defense.Main;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.DriverManager;

public class MySQLDB extends SQLDB {

  private final Plugin plugin;

  public MySQLDB(Main plugin) {
    super(plugin);
    this.plugin = plugin;
  }

  @Override
  protected Connection getNewConnection() {
    ConfigurationSection config = getConfigSection();

    try {
      Class.forName("com.mysql.jdbc.Driver");

      String url = "jdbc:mysql://" + config.getString("host") + ":" + config.getString("port") + "/" + config.getString("name");

      return DriverManager.getConnection(url, config.getString("user"), config.getString("pass"));
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
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
        + "  PRIMARY KEY (`uuid`), "
        + "  UNIQUE KEY `uuid` (`uuid`)"
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
  protected String getCreateAccountQuery(Account account) {
    return "INSERT IGNORE INTO `" + ACCOUNTS_TABLE + "` (`uuid`, `username`, `kills`, "
        + "`deaths`, `bosses_kills`, `max_wave_reached`, `min_wave_reached`) VALUES "
        + "('"
        + account.getUUID() + "', '"
        + account.getName()
        + "', '0', '0', '0', '0', '0');";
  }

  @Override
  protected String getUpdateAccountQuery(Account account) {
    return "UPDATE `" + ACCOUNTS_TABLE + "` SET "
        + "`username`= '" + account.getName() + "',"
        + "`kills`= '" + account.getKills() + "',"
        + "`deaths`='" + account.getDeaths() + "',"
        + "`bosses_kills`='" + account.getBosses_kills() + "',"
        + "`max_wave_reached`='" + account.getMax_wave_reached() + "',"
        + "`min_wave_reached`='" + account.getMin_wave_reached() + "' "
        + "WHERE `uuid`='" + account.getUUID() + "';";
  }

  private ConfigurationSection getConfigSection() {
    return plugin.getConfig().getConfigurationSection("Database");
  }
}
