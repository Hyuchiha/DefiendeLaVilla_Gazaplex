package com.hyuchiha.village_defense.Database.Databases;

import com.hyuchiha.village_defense.Database.Base.Account;
import com.hyuchiha.village_defense.Database.Base.Database;
import com.hyuchiha.village_defense.Database.StatType;
import com.hyuchiha.village_defense.Game.Kit;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
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

        return getDatabase() != null;

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

        MongoCursor<Document> cursor= result.iterator();
        while (cursor.hasNext()){
            Document document = cursor.next();
            accounts.add(getAccountFromDocument(document));
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

        if(document != null){
            Account account = getAccountFromDocument(document);

            cachedAccounts.put(uuid, account);

            return account;
        }else {
            return null;
        }
    }

    @Override
    public void saveAccount(Account account) {
        MongoDatabase database = getDatabase();

        MongoCollection<Document> collection = database.getCollection(ACCOUNTS_COLLECTION);

        Document dbAccount = collection.find(eq("uuid", account.getUUID())).first();

        if(dbAccount != null){
            collection.replaceOne(
                    eq("_id", dbAccount.get("_id")),
                    getDocument(account)
                    );
        }else {
            collection.insertOne(getDocument(account));
        }


    }

    @Override
    public void addUnlockedKit(String uuid, String kit) {
        MongoDatabase database = getDatabase();

        MongoCollection<Document> collection = database.getCollection(ACCOUNTS_COLLECTION);
        Document document = collection.find(eq("uuid", uuid)).first();

        Account account = getAccountFromDocument(document);

        account.getKits().add(Kit.valueOf(kit.toUpperCase()));

        collection.replaceOne(
                eq("_id", document.get("_id")),
                getDocument(account)
        );

        cachedAccounts.put(uuid, account);

    }

    private Document getDocument(Account account){
        Document document = new Document(
                "uuid", account.getUUID())
                .append("username", account.getName())
                .append("kills", account.getKills())
                .append("deaths", account.getDeaths())
                .append("bosses_kills", account.getBosses_kills())
                .append("max_wave_reached", account.getMax_wave_reached())
                .append("min_wave_reached", account.getMin_wave_reached());

        List<String> kits = new ArrayList<>();

        for(Kit kit : account.getKits()){
            kits.add(kit.name());
        }

        document.append("kits", kits);

        return document;
    }

    private Account getAccountFromDocument(Document document){
        Account account = new Account(
                document.getString("uuid"),
                document.getString("username"),
                document.getInteger("kills"),
                document.getInteger("deaths"),
                document.getInteger("bosses_kills"),
                document.getInteger("max_wave_reached"),
                document.getInteger("min_wave_reached")
        );

        ArrayList<String> kitsDB = (ArrayList<String>) document.get("kits");

        List<Kit> kits = new ArrayList<>();

        for(String kitToFind : kitsDB){
            Kit loadedKit = Kit.valueOf(kitToFind);
            kits.add(loadedKit);
        }

        account.setKits(kits);

        return account;
    }

    private ConfigurationSection getConfigSection() {
        return plugin.getConfig().getConfigurationSection("Database");
    }
}
