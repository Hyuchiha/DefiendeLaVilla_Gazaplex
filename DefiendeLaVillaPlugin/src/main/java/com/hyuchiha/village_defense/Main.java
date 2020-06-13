package com.hyuchiha.village_defense;

import com.hyuchiha.village_defense.Arena.Arena;
import com.hyuchiha.village_defense.Chat.ChatListener;
import com.hyuchiha.village_defense.Chat.VaultHooks;
import com.hyuchiha.village_defense.Command.StatsCommand;
import com.hyuchiha.village_defense.Command.TopsCommand;
import com.hyuchiha.village_defense.Command.VillageDefenseCommand;
import com.hyuchiha.village_defense.Config.ConfigManager;
import com.hyuchiha.village_defense.Database.Base.Database;
import com.hyuchiha.village_defense.Database.Databases.MongoDB;
import com.hyuchiha.village_defense.Database.Databases.MySQLDB;
import com.hyuchiha.village_defense.Database.Databases.SQLiteDB;
import com.hyuchiha.village_defense.Game.GameState;
import com.hyuchiha.village_defense.Listeners.*;
import com.hyuchiha.village_defense.Manager.ArenaManager;
import com.hyuchiha.village_defense.Manager.MobManager;
import com.hyuchiha.village_defense.Manager.PlayerManager;
import com.hyuchiha.village_defense.Manager.ShopManager;
import com.hyuchiha.village_defense.Messages.Translator;
import com.hyuchiha.village_defense.Output.Output;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    private ConfigManager config;
    private Database database;

    @Override
    public void onEnable() {
        instance = this;

        Output.log("Welcome to Village Defense");
        Output.log("Developed by Hyuchiha");

        config = new ConfigManager(this);
        config.loadConfigFiles("config.yml", "arenas.yml", "shops.yml", "kits.yml", "messages.yml");

        Translator.initMessages();
        ShopManager.initShops();
        ArenaManager.initArenas();

        ArenaManager.initShopForArenas();

        MobManager.registerMobs();
        PlayerManager.fetchRespawner();

        registerListeners();
        registerCommands();

        hookVault();
        hookBungeeCord();

        initDatabase();
    }

    @Override
    public void onDisable() {
        for (Arena arena : ArenaManager.getArenas()) {
            if (arena.getGame() != null && arena.getGame().getState() == GameState.INGAME) {
                arena.getGame().getWave().cancelWave();
            }
        }

        database.close();
    }

    public void hookBungeeCord() {
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    public void hookVault() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

            @Override
            public void run() {
                if (getServer().getPluginManager().isPluginEnabled("Vault")) {
                    VaultHooks.vault = true;

                    if (!VaultHooks.instance().setupPermissions()) {
                        VaultHooks.vault = false;
                        getLogger().warning("Unable to load Vault: No permission plugin found.");
                    } else {
                        if (!VaultHooks.instance().setupChat()) {
                            VaultHooks.vault = false;
                            getLogger().warning("Unable to load Vault: No chat plugin found.");
                        } else {
                            if (!VaultHooks.instance().setupEconomy()) {
                                VaultHooks.vault = false;
                                getLogger().warning("Unable to load Vault: No economy plugin found.");
                            } else {
                                getLogger().info("Vault hook initalized!");
                            }
                        }
                    }
                }
            }
        }, 40L);
    }

    public void registerListeners() {
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new SignListener(this), this);
        pm.registerEvents(new ArenaListener(this), this);
        pm.registerEvents(new JoinListener(this), this);
        pm.registerEvents(new QuitListener(this), this);
        pm.registerEvents(new MobListener(this), this);
        pm.registerEvents(new SpectatorListener(this), this);
        pm.registerEvents(new PlayerListener(this), this);
        pm.registerEvents(new BossListener(this), this);
        pm.registerEvents(new ChatListener(this), this);
        pm.registerEvents(new InventoryListener(this), this);
    }

    public void registerCommands() {
        getCommand("villagedefense").setExecutor(new VillageDefenseCommand(this));
        getCommand("stats").setExecutor(new StatsCommand(this));
        getCommand("top").setExecutor(new TopsCommand(this));
    }

    public Configuration getConfig(String configName) {
        return config.getConfig(configName);
    }

    public void initDatabase() {
        Configuration configValues = getConfig("config.yml");

        switch (configValues.getString("Database.type")) {
            case "MySQL":
                database = new MySQLDB(this);
                break;
            case "SQLite":
                database = new SQLiteDB(this);
                break;
            case "MongoDB":
                database = new MongoDB(this);
                break;
        }

        if (database != null) {
            if (!database.init()) {
                Output.logError("Database init error");

                setEnabled(false);
            }
        }
    }

    public Database getDatabase() {
        return database;
    }
}
