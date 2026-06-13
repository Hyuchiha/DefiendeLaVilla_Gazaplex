package com.hyuchiha.village_defense.Database.Databases;

import com.hyuchiha.village_defense.Database.Base.Account;
import com.hyuchiha.village_defense.Database.Base.Database;
import com.hyuchiha.village_defense.Database.StatType;
import com.hyuchiha.village_defense.Game.Kit;
import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Output.Output;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Backend SQL respaldado por un pool de conexiones HikariCP.
 *
 * <p>Las subclases (MySQLDB / SQLiteDB) aportan un {@link HikariConfig} vía
 * {@link #buildHikariConfig()} mas las queries DDL/DML especificas del dialecto.
 *
 * <p>El pool reemplaza el modelo anterior de una unica {@link Connection}
 * compartida: cada operacion toma una conexion del pool, la usa dentro de un
 * try-with-resources y la devuelve. HikariCP maneja keep-alive, validacion y
 * concurrencia, asi que ya no se sincronizan metodos ni se corre un ping manual.
 */
public abstract class SQLDB extends Database {
  protected static final String ACCOUNTS_TABLE = "village_defense_accounts";
  protected static final String KITS_TABLE = "village_defense_kits";
  protected static final String KITS_UNLOCKED_TABLE = "village_defense_kits_unlocked";

  private HikariDataSource dataSource;

  public SQLDB(Main plugin) {
    super(plugin);
  }

  @Override
  public boolean init() {
    try {
      this.dataSource = new HikariDataSource(buildHikariConfig());
    } catch (RuntimeException e) {
      Output.logError("Failed to initialize Hikari pool: " + e.getMessage());
      return false;
    }

    try (Connection conn = dataSource.getConnection();
         Statement stmt = conn.createStatement()) {
      // Setup del esquema: se crean las tablas y luego los indices secundarios que
      // respaldan los hot paths (carga de cuenta, ownership de kit, id de kit y tops).
      stmt.execute(getDatabaseQuery());
      stmt.execute(getDatabaseKitsQuery());
      stmt.execute(getDatabaseKitsUnlockedQuery());

      // SQLite no permite declarar indices secundarios dentro de CREATE TABLE, y
      // CREATE TABLE IF NOT EXISTS no toca tablas ya existentes en servers en uso,
      // asi que los indices se emiten como CREATE INDEX aparte. Cada uno corre
      // aislado para que un fallo "ya existe" no aborte el setup.
      for (String ddl : getIndexQueries()) {
        try {
          stmt.execute(ddl);
        } catch (SQLException e) {
          // MySQL no tiene CREATE INDEX IF NOT EXISTS; 1061 = nombre de indice
          // duplicado, es decir el indice ya existe. Caso esperado y silencioso.
          if (e.getErrorCode() != 1061) {
            Output.logError("Failed to create index: " + e.getMessage());
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }

    insertMissingKits();
    return true;
  }

  /** Las subclases arman un config Hikari segun su driver JDBC. */
  protected abstract HikariConfig buildHikariConfig();

  @Override
  public void close() {
    super.close(); // hace flush de las cuentas cacheadas via saveAccount
    if (this.dataSource != null && !this.dataSource.isClosed()) {
      this.dataSource.close();
    }
  }

  private void insertMissingKits() {
    for (Kit kit : Kit.values()) {
      try (Connection conn = dataSource.getConnection()) {
        int id = getIdOfElement(conn, kit.name());

        if (id < 0) {
          try (Statement stmt = conn.createStatement()) {
            stmt.execute(getInsertKitQuery(kit));
          }
        }
      } catch (SQLException e) {
        Output.logError(e.getMessage());
      }
    }
  }

  @Override
  protected List<Account> loadTopAccountsByStatType(StatType type, int size) {
    // type es un enum acotado (no entrada de usuario), pero el LIMIT si se parametriza.
    String sql = "SELECT * FROM " + ACCOUNTS_TABLE + " ORDER BY " + type.name().toLowerCase() + " DESC LIMIT ?";

    List<Account> topAccounts = new ArrayList<>();

    try (Connection conn = dataSource.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, size);
      try (ResultSet set = ps.executeQuery()) {
        while (set.next()) {
          topAccounts.add(new Account(
              set.getString("uuid"),
              set.getString("username"),
              set.getInt("kills"),
              set.getInt("deaths"),
              set.getInt("bosses_kills"),
              set.getInt("max_wave_reached"),
              set.getInt("min_wave_reached")));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return topAccounts;
  }

  @Override
  protected void createAccountAndAddToDatabase(Account account) {
    try (Connection conn = dataSource.getConnection();
         PreparedStatement ps = conn.prepareStatement(getCreateAccountQuery())) {
      ps.setString(1, account.getUUID());
      ps.setString(2, account.getName());
      ps.execute();
      cachedAccounts.put(account.getUUID(), account);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected Account loadAccount(String uuid) {
    // Los UUID de Bukkit son siempre lowercase canonico, asi que un match exacto
    // pega directo al PRIMARY KEY. La forma vieja JOIN ... WHERE UPPER(uuid) LIKE
    // UPPER(?) ademas no tenia ON y descartaba cuentas sin kits desbloqueados.
    String query = "SELECT * FROM " + ACCOUNTS_TABLE + " WHERE uuid = ?";

    try (Connection conn = dataSource.getConnection();
         PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, uuid);

      Account account = null;
      try (ResultSet set = ps.executeQuery()) {
        if (set.next()) {
          account = new Account(
              set.getString("uuid"),
              set.getString("username"),
              set.getInt("kills"),
              set.getInt("deaths"),
              set.getInt("bosses_kills"),
              set.getInt("max_wave_reached"),
              set.getInt("min_wave_reached"));
        }
      }

      if (account != null) {
        account.setKits(getKitsFromAccount(conn, uuid));
        cachedAccounts.put(uuid, account);
      }
      return account;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public void saveAccount(Account account) {
    try (Connection conn = dataSource.getConnection();
         PreparedStatement ps = conn.prepareStatement(getUpdateAccountQuery())) {
      ps.setString(1, account.getName());
      ps.setInt(2, account.getKills());
      ps.setInt(3, account.getDeaths());
      ps.setInt(4, account.getBosses_kills());
      ps.setInt(5, account.getMax_wave_reached());
      ps.setInt(6, account.getMin_wave_reached());
      ps.setString(7, account.getUUID());
      ps.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void addUnlockedKit(String uuid, String kit) {
    try (Connection conn = dataSource.getConnection()) {
      int idKit = getIdOfElement(conn, kit);

      if (idKit < 0) {
        // Kit no registrado en la tabla kits: insertMissingKits() los siembra al
        // arrancar, asi que esto no deberia pasar. Se aborta para no insertar una
        // fila colgante (SQLite no fuerza la FK) ni divergir el cache de la DB.
        Output.logError("addUnlockedKit: kit desconocido '" + kit + "'");
        return;
      }

      String query = "INSERT INTO `" + KITS_UNLOCKED_TABLE + "`(`clv_kit`,`player`) VALUES (?, ?);";
      try (PreparedStatement ps = conn.prepareStatement(query)) {
        ps.setInt(1, idKit);
        ps.setString(2, uuid);
        ps.execute();
      }

      Account cached = cachedAccounts.get(uuid);
      if (cached != null) {
        cached.getKits().add(Kit.valueOf(kit.toUpperCase()));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Resuelve el id entero de un kit por nombre. Reusa la conexion del caller para
   * que el insert/update pueda quedarse en la misma conexion fisica si hace falta.
   */
  private int getIdOfElement(Connection conn, String name) throws SQLException {
    String query = "SELECT clv_kit FROM " + KITS_TABLE + " WHERE `name` = ?";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, name);
      try (ResultSet set = ps.executeQuery()) {
        if (set.next()) {
          return set.getInt("clv_kit");
        }
      }
    }
    return -1;
  }

  private List<Kit> getKitsFromAccount(Connection conn, String uuid) throws SQLException {
    List<Kit> kits = new ArrayList<>();

    String query = "SELECT " + KITS_TABLE + ".name FROM " + KITS_UNLOCKED_TABLE
        + " JOIN " + KITS_TABLE + " ON " + KITS_TABLE + ".clv_kit = " + KITS_UNLOCKED_TABLE + ".clv_kit "
        + "WHERE player = ?;";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, uuid);
      try (ResultSet set = ps.executeQuery()) {
        while (set.next()) {
          kits.add(Kit.valueOf(set.getString("name")));
        }
      }
    }
    return kits;
  }

  protected abstract String getDatabaseQuery();

  protected abstract String getDatabaseKitsQuery();

  protected abstract String getDatabaseKitsUnlockedQuery();

  protected abstract String getInsertKitQuery(Kit kit);

  /**
   * Sentencias {@code CREATE INDEX} de los indices secundarios, segun el dialecto.
   * Se ejecutan en {@link #init()} justo despues de crear las tablas; cada una
   * corre aislada para que un fallo "ya existe" no sea fatal.
   */
  protected abstract List<String> getIndexQueries();

  /**
   * Plantilla INSERT parametrizada para una cuenta nueva. Placeholders, en orden:
   * {@code 1 = uuid}, {@code 2 = username}. Las columnas de stats arrancan en 0.
   */
  protected abstract String getCreateAccountQuery();

  /**
   * Plantilla UPDATE parametrizada para una cuenta existente. Placeholders, en orden:
   * {@code 1 = username, 2 = kills, 3 = deaths, 4 = bosses_kills,
   * 5 = max_wave_reached, 6 = min_wave_reached, 7 = uuid (WHERE)}.
   */
  protected abstract String getUpdateAccountQuery();
}
