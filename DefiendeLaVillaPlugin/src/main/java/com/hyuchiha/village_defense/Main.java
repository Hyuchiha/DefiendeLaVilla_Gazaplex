package com.hyuchiha.village_defense;

import com.hyuchiha.village_defense.Arena.Arena;
import com.hyuchiha.village_defense.Chat.ChatListener;
import com.hyuchiha.village_defense.Chat.VaultHooks;
import com.hyuchiha.village_defense.Command.VillageDefenseCommand;
import com.hyuchiha.village_defense.Config.ConfigManager;
import com.hyuchiha.village_defense.Database.BackendConnection.DatabaseConnection;
import com.hyuchiha.village_defense.Database.KitsManager;
import com.hyuchiha.village_defense.Database.KitsUnlockedManager;
import com.hyuchiha.village_defense.Database.PlayerStatsData;
import com.hyuchiha.village_defense.Database.StatsManager;
import com.hyuchiha.village_defense.Game.GamePlayer;
import com.hyuchiha.village_defense.Game.GameState;
import com.hyuchiha.village_defense.Listeners.*;
import com.hyuchiha.village_defense.Manager.*;
import com.hyuchiha.village_defense.Messages.Translator;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class Main extends JavaPlugin {
    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    private ConfigManager config;
    private DatabaseConnection dbConnection;

    @Override
    public void onEnable() {
        instance = this;

        getLogger().log(Level.INFO, "Welcome to Village Defense");
        getLogger().log(Level.INFO, "Developed by Hyuchiha");

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

        initDatabaseConnection();
        createDatabases();
    }

    @Override
    public void onDisable() {
        for(Arena arena: ArenaManager.getArenas()){
            if(arena.getGame() != null && arena.getGame().getState() == GameState.INGAME){
                arena.getGame().getWave().cancelWave();

                for(GamePlayer player : arena.getGame().getPlayersInGame()){
                    StatsManager.updateStatsFromPlayer(PlayerStatsData.getPlayerStat(player.getPlayerUUID(), player.getPlayer().getName()));
                }
            }
        }
    }

    public void hookBungeeCord(){
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
        pm.registerEvents(new KitListener(this), this);
    }

    public void registerCommands() {
        getCommand("villagedefense").setExecutor(new VillageDefenseCommand(this));
    }

    public Configuration getConfig(String configName) {
        return config.getConfig(configName);
    }

    public String getPrefix() {
        return Translator.change("PREFIX");
    }

    public void initDatabaseConnection(){
        Configuration configValues = getConfig("config.yml");

        String hostname = configValues.getString("MySQL.host");
        int port = configValues.getInt("MySQL.port");
        String database = configValues.getString("MySQL.name");
        String userName = configValues.getString("MySQL.user");
        String password = configValues.getString("MySQL.pass");

        this.dbConnection = new DatabaseConnection(hostname, port, database, userName, password, this);
    }

    public void createDatabases(){
        StatsManager.InitDatabase();
        KitsManager.InitDatabase();
        KitsUnlockedManager.InitDatabase();
    }

    public DatabaseConnection getDbConnection() {
        return dbConnection;
    }
}
