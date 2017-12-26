package com.hyuchiha.village_defense.Database.Databases;

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
}