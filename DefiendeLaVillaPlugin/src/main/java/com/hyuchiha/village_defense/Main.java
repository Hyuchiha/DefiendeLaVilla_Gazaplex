package com.hyuchiha.village_defense;

import com.hyuchiha.village_defense.Config.ConfigManager;
import com.hyuchiha.village_defense.Messages.Translator;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class Main extends JavaPlugin {
    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    private ConfigManager config;

    @Override
    public void onEnable() {
        instance = this;

        getLogger().log(Level.INFO, "Welcome to Village Defense");
        getLogger().log(Level.INFO, "Developed by Hyuchiha");

        config = new ConfigManager(this);
        config.loadConfigFiles("config.yml", "arenas.yml", "shops.yml", "kits.yml", "messages.yml");

        Translator.InitMessages();
    }

    @Override
    public void onDisable() {

    }

    public ConfigManager getConfigManager() {
        return config;
    }
}
