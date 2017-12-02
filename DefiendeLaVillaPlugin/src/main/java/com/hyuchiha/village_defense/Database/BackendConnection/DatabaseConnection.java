/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Database.BackendConnection;

import com.hyuchiha.village_defense.Output.Output;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author hyuchiha
 */
public class DatabaseConnection {

    private final String driver;
    private final String connectionString;
    private final Plugin plugin;
    public Connection connection = null;

    public DatabaseConnection(String hostname, int port, String database, String username, String password, Plugin plugin) {
        driver = "com.mysql.jdbc.Driver";
        connectionString = "jdbc:mysql://" + hostname + ":" + port + "/" + database + "?user=" + username + "&password=" + password;
        this.plugin = plugin;
        open();
    }
    
    public Connection open() {
        try {
            Class.forName(driver);

            this.connection = DriverManager.getConnection(connectionString);
            return connection;
        } catch (SQLException | ClassNotFoundException e) {
            Output.logError(e.getLocalizedMessage());
        }
        return this.connection;
    }
    
    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            Output.logError(ex.getLocalizedMessage());
        }
        connection = null;
    }
    
    public boolean isConnected() {
        try {
            return ((connection == null || connection.isClosed()) ? false : true);
        } catch (SQLException e) {
            return false;
        }
    }
    
    public Result query(final String query, boolean retry) {
        if (!isConnected()) {
            open();
        }
        try {
            PreparedStatement statement = null;
            try {
                if (!isConnected()) {
                    open();
                }
                statement = connection.prepareStatement(query);
                if (statement.execute()) {
                    return new Result(statement, statement.getResultSet());
                }
            } catch (final SQLException e) {
                final String msg = e.getMessage();
                Output.logError("Database query error: " + msg);

                if (retry && msg.contains("_BUSY")) {
                    plugin.getLogger().severe("Retrying query...");
                    plugin.getServer().getScheduler()
                            .scheduleSyncDelayedTask(plugin, new Runnable() {
                                @Override
                                public void run() {
                                    query(query, false);
                                }
                            }, 20);
                }
            }
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException ex) {
            Output.logError(ex.getLocalizedMessage());
        }
        return null;
    }
}
