package com.hyuchiha.village_defense.Database.Databases;

import com.hyuchiha.village_defense.Database.Base.Account;
import com.hyuchiha.village_defense.Database.Base.Database;
import com.hyuchiha.village_defense.Database.StatType;
import com.hyuchiha.village_defense.Game.Kit;
import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Output.Output;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class SQLDB extends Database {

  private static final String accountsTable = "village_defense_accounts";
  private static final String kitsTable = "village_defense_kits";
  private static final String unlockedKitsTable = "village_defense_kits_unlocked";

  private Main plugin;
  private Connection connection;

  public SQLDB(Main plugin) {
    super(plugin);

    this.plugin = plugin;

    plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
      @Override
      public void run() {
        try {
          if (connection != null && !connection.isClosed()) {
            connection.createStatement().execute("/* ping */ SELECT 1");
          }
        } catch (SQLException e) {
          connection = getNewConnection();
        }
      }
    }, 60 * 20, 60 * 20);
  }

  @Override
  public boolean init() {
    super.init();

    return checkConnection();
  }

  public boolean checkConnection() {
    try {
      if (connection == null || connection.isClosed()) {
        connection = getNewConnection();

        if (connection == null || connection.isClosed()) {
          return false;
        }

        query(ACCOUNTS_QUERY);

        query(KITS_QUERY);

        query(UNLOCKEDS_QUERY);

        for (Kit kitToAdd : Kit.values()) {
          String query = "INSERT IGNORE INTO `" + kitsTable + "`(`name`)  VALUES "
              + "('" + kitToAdd.name().toUpperCase() + "');";

          query(query);
        }

      }
    } catch (SQLException e) {
      e.printStackTrace();

      return false;
    }

    return true;
  }

  protected abstract Connection getNewConnection();

  public boolean query(String sql) throws SQLException {
    return connection.createStatement().execute(sql);
  }

  public void close() {
    super.close();

    try {
      if (connection != null)
        connection.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected List<Account> loadTopAccountsByStatType(StatType type, int size) {
    checkConnection();

    String sql = "SELECT * FROM " + accountsTable + " ORDER BY " + type.name().toLowerCase() + " DESC limit " + size;

    List<Account> topAccounts = new ArrayList<>();

    try {
      ResultSet set = connection.createStatement().executeQuery(sql);

      while (set.next()) {
        Account account = new Account(
            set.getString("uuid"),
            set.getString("username"),
            set.getInt("kills"),
            set.getInt("deaths"),
            set.getInt("bosses_kills"),
            set.getInt("max_wave_reached"),
            set.getInt("min_wave_reached"));

        topAccounts.add(account);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return topAccounts;
  }

  @Override
  protected void createAccountAndAddToDatabase(Account account) {
    checkConnection();

    try {
      String query = "INSERT IGNORE INTO `" + accountsTable + "` (`uuid`, `username`, `kills`, "
          + "`deaths`, `bosses_kills`, `max_wave_reached`, `min_wave_reached`) VALUES "
          + "('"
          + account.getUUID() + "', '"
          + account.getName()
          + "', '0', '0', '0', '0', '0');";

      PreparedStatement statement = connection.prepareStatement(query);

      if (statement.execute()) {
        statement.close();
      }

      cachedAccounts.put(account.getUUID(), account);

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected Account loadAccount(String uuid) {
    checkConnection();

    Output.log("Loading account with uuid: " + uuid);

    try {
      String query = "SELECT * FROM " + accountsTable +
          " JOIN " + unlockedKitsTable + " WHERE UPPER(uuid) LIKE UPPER(?)";

      PreparedStatement statement = connection.prepareStatement(query);

      statement.setString(1, uuid);

      ResultSet set = statement.executeQuery();

      Account account = null;

      while (set.next()) {
        account = new Account(
            set.getString("uuid"),
            set.getString("username"),
            set.getInt("kills"),
            set.getInt("deaths"),
            set.getInt("bosses_kills"),
            set.getInt("max_wave_reached"),
            set.getInt("min_wave_reached")
        );
      }

      set.close();

      if (account != null) {
        List<Kit> kits = getKitsFromAccount(uuid);
        account.setKits(kits);
      }

      cachedAccounts.put(uuid, account);

      return account;
    } catch (SQLException e) {
      e.printStackTrace();

      return null;
    }
  }

  @Override
  public void saveAccount(Account account) {
    checkConnection();

    try {
      String query = "UPDATE `" + accountsTable + "` SET "
          + "`username`= '" + account.getName() + "',"
          + "`kills`= '" + account.getKills() + "',"
          + "`deaths`='" + account.getDeaths() + "',"
          + "`bosses_kills`='" + account.getBosses_kills() + "',"
          + "`max_wave_reached`='" + account.getMax_wave_reached() + "',"
          + "`min_wave_reached`='" + account.getMin_wave_reached() + "' "
          + "WHERE `uuid`='" + account.getUUID() + "';";

      PreparedStatement statement = connection.prepareStatement(query);

      if (statement.execute()) {
        statement.close();
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void addUnlockedKit(String uuid, String kit) {
    checkConnection();

    try {
      int idKit = getIdOfElement(kit);

      String query = "INSERT INTO `" + unlockedKitsTable + "`(`clv_kit`,`player`)  VALUES "
          + "('" + idKit + "', '" + uuid + "');";

      PreparedStatement statement = connection.prepareStatement(query);

      if (statement.execute()) {
        statement.close();
      }

      Account account = null;

      for (Account cache : cachedAccounts.values()) {
        if (cache.getUUID().equalsIgnoreCase(uuid)) {
          account = cache;
        }
      }

      if (account != null) {
        account.getKits().add(Kit.valueOf(kit.toUpperCase()));

        cachedAccounts.put(uuid, account);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private int getIdOfElement(String name) {
    checkConnection();

    try {
      String query = "SELECT * FROM " + kitsTable + " WHERE `name` = '" + name + "'";

      PreparedStatement statement = connection.prepareStatement(query);

      ResultSet set = statement.executeQuery();

      int id = -1;

      while (set.next()) {
        id = set.getInt("clv_kit");
      }

      return id;

    } catch (SQLException ex) {
      ex.printStackTrace();
    }
    return -1;
  }

  private List<Kit> getKitsFromAccount(String uuid) {
    checkConnection();

    List<Kit> kits = new ArrayList<>();

    try {
      String query = "SELECT " + kitsTable + ".name from " + unlockedKitsTable
          + " JOIN " + kitsTable + " where " + kitsTable + ".clv_kit = " + unlockedKitsTable + ".clv_kit " +
          "AND player = '" + uuid + "';";

      ResultSet set = connection.createStatement().executeQuery(query);

      while (set.next()) {
        String kitName = set.getString("name");

        kits.add(Kit.valueOf(kitName));
      }

    } catch (SQLException ex) {
      ex.printStackTrace();
    }

    return kits;
  }

  private static final String ACCOUNTS_QUERY = "CREATE TABLE IF NOT EXISTS `" + accountsTable + "` ("
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

  private static final String KITS_QUERY = "CREATE TABLE IF NOT EXISTS `" + kitsTable + "` ( "
      + "`clv_kit` int(6) NOT NULL AUTO_INCREMENT,"
      + "`name` varchar(45) NOT NULL,"
      + "PRIMARY KEY (`clv_kit`), "
      + "UNIQUE KEY `clv_kit` (`clv_kit`) ) "
      + "ENGINE=InnoDB AUTO_INCREMENT=1";

  private static final String UNLOCKEDS_QUERY = "CREATE TABLE IF NOT EXISTS `" + unlockedKitsTable + "` "
      + "(`clv_kit` int(6) NOT NULL,"
      + "`player` varchar(36) NOT NULL, "
      + "PRIMARY KEY (`clv_kit` , `player` ) ,"
      + "FOREIGN KEY (`clv_kit`) REFERENCES " + kitsTable + "(`clv_kit`), "
      + "FOREIGN KEY (`player`) REFERENCES " + accountsTable + "(`uuid`) )  "
      + "ENGINE=InnoDB;";
}
