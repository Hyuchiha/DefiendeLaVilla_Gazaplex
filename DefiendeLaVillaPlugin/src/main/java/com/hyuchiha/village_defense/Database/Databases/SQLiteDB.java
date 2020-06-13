package com.hyuchiha.village_defense.Database.Databases;

import com.hyuchiha.village_defense.Database.Base.Account;
import com.hyuchiha.village_defense.Game.Kit;
import com.hyuchiha.village_defense.Main;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

public class SQLiteDB extends SQLDB {

  private final Plugin plugin;

  public SQLiteDB(Main plugin) {
    super(plugin);

    this.plugin = plugin;
  }

  @Override
  protected Connection getNewConnection() {
    try {
      Class.forName("org.sqlite.JDBC");

      return DriverManager.getConnection("jdbc:sqlite:" + new File(plugin.getDataFolder(), "database.db").getAbsolutePath());
    } catch (Exception e) {
      return null;
    }
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
  protected String getCreateAccountQuery(Account account) {
    return "INSERT OR IGNORE INTO `" + ACCOUNTS_TABLE + "` (`uuid`, `username`, `kills`, "
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
}
