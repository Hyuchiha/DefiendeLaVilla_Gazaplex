package com.hyuchiha.village_defense.Database.Databases;

import com.hyuchiha.village_defense.Database.Base.Account;
import com.hyuchiha.village_defense.Database.Base.Database;
import com.hyuchiha.village_defense.Database.StatType;
import com.hyuchiha.village_defense.Game.Kit;
import com.hyuchiha.village_defense.Output.Output;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class MongoDB extends Database {
  private static final String ACCOUNTS_COLLECTION = "accounts";

  private final Plugin plugin;
  private MongoClient mongoClient;

  public MongoDB(Plugin plugin) {
    super(plugin);

    this.plugin = plugin;
  }

  @Override
  public boolean init() {
    super.init();

    ConfigurationSection section = getConfigSection();

    MongoCredential credential = MongoCredential.createScramSha1Credential(
        section.getString("user"),
        section.getString("name"),
        section.getString("pass").toCharArray()
    );

    MongoClientOptions.Builder options = new MongoClientOptions.Builder();
    //options.sslEnabled(true);
    //options.sslInvalidHostNameAllowed(true);
    options.connectTimeout(10000);

    mongoClient = new MongoClient(
        new ServerAddress(
            getConfigSection().getString("host"),
            getConfigSection().getInt("port")
        ), credential, options.build()
    );

    if (getDatabase() == null) {
      return false;
    }

    createIndexes();
    return true;
  }

  /**
   * Indices de lookup por uuid y de leaderboards. {@code createIndex} es idempotente,
   * seguro en cada arranque.
   */
  private void createIndexes() {
    MongoCollection<Document> collection = getDatabase().getCollection(ACCOUNTS_COLLECTION);

    // loadAccount / saveAccount / addUnlockedKit consultan por uuid: sin indice es full scan.
    try {
      collection.createIndex(Indexes.ascending("uuid"), new IndexOptions().unique(true));
    } catch (MongoException e) {
      // Colecciones legacy pueden tener uuid duplicados: se sigue sin el indice unico.
      Output.logError("No se pudo crear el indice unico de uuid en MongoDB: " + e.getMessage());
    }

    // /top: find().sort(descending(<stat>)).limit(n).
    for (StatType type : StatType.values()) {
      collection.createIndex(Indexes.descending(type.name().toLowerCase()));
    }
  }

  public MongoDatabase getDatabase() {
    return mongoClient.getDatabase(getConfigSection().getString("name"));
  }

  @Override
  protected List<Account> loadTopAccountsByStatType(StatType type, int size) {
    MongoDatabase database = getDatabase();

    MongoCollection<Document> collection = database.getCollection(ACCOUNTS_COLLECTION);

    MongoIterable<Document> result = collection.find().sort(Sorts.descending(type.name().toLowerCase())).limit(size);

    List<Account> accounts = new ArrayList<>();

    try (MongoCursor<Document> cursor = result.iterator()) {
      while (cursor.hasNext()) {
        accounts.add(getAccountFromDocument(cursor.next()));
      }
    }

    return accounts;
  }

  @Override
  protected void createAccountAndAddToDatabase(Account account) {
    MongoDatabase database = getDatabase();

    MongoCollection<Document> collection = database.getCollection(ACCOUNTS_COLLECTION);

    collection.insertOne(getDocument(account));

    cachedAccounts.put(account.getUUID(), account);
  }

  @Override
  protected Account loadAccount(String uuid) {
    MongoDatabase database = getDatabase();

    Document document = database.getCollection(ACCOUNTS_COLLECTION).find(eq("uuid", uuid)).first();

    if (document != null) {
      Account account = getAccountFromDocument(document);

      cachedAccounts.put(uuid, account);

      return account;
    } else {
      return null;
    }
  }

  @Override
  public void saveAccount(Account account) {
    MongoDatabase database = getDatabase();

    MongoCollection<Document> collection = database.getCollection(ACCOUNTS_COLLECTION);

    Document dbAccount = collection.find(eq("uuid", account.getUUID())).first();

    if (dbAccount != null) {
      collection.replaceOne(
          eq("_id", dbAccount.get("_id")),
          getDocument(account)
      );
    } else {
      collection.insertOne(getDocument(account));
    }


  }

  @Override
  public void addUnlockedKit(String uuid, String kit) {
    MongoDatabase database = getDatabase();

    MongoCollection<Document> collection = database.getCollection(ACCOUNTS_COLLECTION);
    Document document = collection.find(eq("uuid", uuid)).first();
    if (document == null) {
      return;
    }

    Account account = getAccountFromDocument(document);

    account.getKits().add(Kit.valueOf(kit.toUpperCase()));

    collection.replaceOne(
        eq("_id", document.get("_id")),
        getDocument(account)
    );

    cachedAccounts.put(uuid, account);

  }

  private Document getDocument(Account account) {
    Document document = new Document(
        "uuid", account.getUUID())
        .append("username", account.getName())
        .append("kills", account.getKills())
        .append("deaths", account.getDeaths())
        .append("bosses_kills", account.getBosses_kills())
        .append("max_wave_reached", account.getMax_wave_reached())
        .append("min_wave_reached", account.getMin_wave_reached());

    List<String> kits = new ArrayList<>();

    for (Kit kit : account.getKits()) {
      kits.add(kit.name());
    }

    document.append("kits", kits);

    return document;
  }

  private Account getAccountFromDocument(Document document) {
    Account account = new Account(
        document.getString("uuid"),
        document.getString("username"),
        // Default 0: documentos legacy pueden no tener todos los stats.
        document.getInteger("kills", 0),
        document.getInteger("deaths", 0),
        document.getInteger("bosses_kills", 0),
        document.getInteger("max_wave_reached", 0),
        document.getInteger("min_wave_reached", 0)
    );

    List<Kit> kits = new ArrayList<>();

    Object kitsDB = document.get("kits");
    if (kitsDB instanceof List) {
      for (Object kitToFind : (List<?>) kitsDB) {
        // Un kit borrado/renombrado en kits.yml no debe tumbar la carga de la cuenta.
        try {
          kits.add(Kit.valueOf(String.valueOf(kitToFind).toUpperCase()));
        } catch (IllegalArgumentException ignored) {
        }
      }
    }

    account.setKits(kits);

    return account;
  }

  private ConfigurationSection getConfigSection() {
    return plugin.getConfig().getConfigurationSection("Database");
  }
}
