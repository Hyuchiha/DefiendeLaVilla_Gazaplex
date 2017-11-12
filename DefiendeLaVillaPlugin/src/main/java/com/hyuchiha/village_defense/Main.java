package com.hyuchiha.village_defense;

import com.hyuchiha.village_defense.Chat.ChatListener;
import com.hyuchiha.village_defense.Chat.VaultHooks;
import com.hyuchiha.village_defense.Command.VillageDefenseCommand;
import com.hyuchiha.village_defense.Config.ConfigManager;
import com.hyuchiha.village_defense.Listeners.*;
import com.hyuchiha.village_defense.Manager.ArenaManager;
import com.hyuchiha.village_defense.Manager.MobManager;
import com.hyuchiha.village_defense.Manager.ShopManager;
import com.hyuchiha.village_defense.Messages.Translator;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class Main extends JavaPlugin {
    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    private ConfigManager config;
    private Economy economy;

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

        registerListeners();
        registerCommands();

        hookChat();
        hookBungeeCord();
        registerEconomy();
    }

    @Override
    public void onDisable() {

    }

    public void hookBungeeCord(){
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    public void hookChat() {
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
                            getLogger().info("Vault hook initalized!");
                        }
                    }
                } else {
                    getLogger().warning("Vault not found! Permissions features disabled.");
                }
            }
        }, 40L);
    }

    public void registerEconomy() {
        if ((Bukkit.getPluginManager().getPlugin("Vault") instanceof Vault)) {
            RegisteredServiceProvider<Economy> service = Bukkit.getServicesManager().getRegistration(Economy.class);
            if (service != null) {
                this.economy = service.getProvider();
            }
        } else {
            getLogger().warning("Vault not found! Rewards features disabled.");
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
        pm.registerEvents(new KitListener(this), this);
    }

    public void registerCommands() {
        getCommand("villagedefense").setExecutor(new VillageDefenseCommand(this));
    }

    public Configuration getConfig(String configName) {
        return config.getConfig(configName);
    }

    public Economy getEconomy() {
        return economy;
    }

    public String getPrefix() {
        return Translator.change("PREFIX");
    }
}
