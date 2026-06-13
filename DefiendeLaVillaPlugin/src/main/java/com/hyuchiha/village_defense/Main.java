package com.hyuchiha.village_defense;

import com.hyuchiha.village_defense.Arena.Arena;
import com.hyuchiha.village_defense.Chat.ChatListener;
import com.hyuchiha.village_defense.Command.StatsCommand;
import com.hyuchiha.village_defense.Command.TopsCommand;
import com.hyuchiha.village_defense.Command.VillageDefenseCommand;
import com.hyuchiha.village_defense.Config.ConfigManager;
import com.hyuchiha.village_defense.Database.Base.Database;
import com.hyuchiha.village_defense.Database.Databases.MongoDB;
import com.hyuchiha.village_defense.Database.Databases.MySQLDB;
import com.hyuchiha.village_defense.Database.Databases.SQLiteDB;
import com.hyuchiha.village_defense.Game.Game;
import com.hyuchiha.village_defense.Game.GamePlayer;
import com.hyuchiha.village_defense.Game.GameState;
import com.hyuchiha.village_defense.Hooks.VaultHooks;
import com.hyuchiha.village_defense.Listeners.*;
import com.hyuchiha.village_defense.Manager.ArenaManager;
import com.hyuchiha.village_defense.Manager.MobManager;
import com.hyuchiha.village_defense.Manager.PlayerManager;
import com.hyuchiha.village_defense.Manager.ShopManager;
import com.hyuchiha.village_defense.Manager.SpectatorManager;
import com.hyuchiha.village_defense.Messages.Translator;
import com.hyuchiha.village_defense.Output.Output;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

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
      Game game = arena.getGame();
      if (game == null) {
        continue;
      }

      if (game.getState() == GameState.INGAME && game.getWave() != null) {
        game.getWave().cancelWave();
      }

      // En /reload el server NO desconecta a los jugadores: hay que sacarlos de la
      // arena para que no queden con inventario de partida, scoreboard ni en estado
      // INGAME. Se itera una copia porque sendPlayerToLobby muta el estado.
      for (GamePlayer player : new ArrayList<>(game.getPlayersInGame())) {
        Player bukkitPlayer = player.getPlayer();
        if (bukkitPlayer != null) {
          game.getScoreboardManager().removeScoreboard(bukkitPlayer.getName());
        }
        player.sendPlayerToLobby();
      }

      game.removeAllSpectators();
    }

    SpectatorManager.clearSpectators();

    // Flush de las cuentas cacheadas a la DB antes de soltar el pool.
    if (database != null) {
      database.close();
    }

    PlayerManager.clearPlayers();
  }

  public void hookBungeeCord() {
    Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
  }

  private void hookVault() {
    if (getServer().getPluginManager().isPluginEnabled("Vault")) {
      VaultHooks.vault = true;


      if (!VaultHooks.instance().setupPermissions()) {
        VaultHooks.vault = false;
        getLogger().warning("Unable to load Vault: No permission plugin found.");
      }


      if (!VaultHooks.instance().setupChat()) {
        VaultHooks.vault = false;
        getLogger().warning("Unable to load Vault: No chat plugin found.");
      }


      if (!VaultHooks.instance().setupEconomy()) {
        VaultHooks.vault = false;
        getLogger().warning("Unable to load Vault: No economy plugin found.");
      }

      if (VaultHooks.vault) {
        getLogger().info("Vault hook initalized!");
      }
    } else {
      getLogger().warning("Vault not found! Permissions features disabled.");
    }
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

  public Database getMainDatabase() {
    return database;
  }
}
