/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Database;

import com.hyuchiha.village_defense.Game.Kit;
import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Output.Output;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hyuchiha
 */
public class KitsManager {

    private static final String DATABASE_QUERY = "CREATE TABLE IF NOT EXISTS `Kits` ( "
            + "`clv_kit` int(6) NOT NULL AUTO_INCREMENT,"
            + "`Name` varchar(45) NOT NULL,"
            + "PRIMARY KEY (`clv_kit`), "
            + "UNIQUE KEY `clv_kit` (`clv_kit`) ) "
            + "ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1";

    private static final String TABLE_NAME = "Kits";
    private static final String KIT_NAME = "Name";

    private static final Main plugin = Main.getInstance();

    public static void InitDatabase() {
        plugin.getDbConnection().query(DATABASE_QUERY, true);
        initializeKits();
    }

    public static List<String> getAllTheElements() {
        List<String> result = new ArrayList<String>();

        try {
            ResultSet rs = plugin.getDbConnection().query("SELECT * FROM `" + TABLE_NAME + "`", true).getResultSet();

            while (rs.next()) {
                result.add(rs.getString(KIT_NAME));
            }

            return result;
        } catch (SQLException ex) {
            return null;
        }
    }

    public static int getIdOfElement(String name) {
        try {
            String Query = "SELECT * FROM " + TABLE_NAME + " WHERE `" + KIT_NAME + "` = '" + name + "'";

            ResultSet result = plugin.getDbConnection().query(Query, true).getResultSet();

            if (result.next()) {
                return result.getInt("clv_kit");
            }
        } catch (SQLException ex) {
            Output.logError(ex.getSQLState());
        }
        return -1;
    }

    public static void initializeKits() {
        for (Kit kitToAdd : Kit.values()) {
            if (!isElement(kitToAdd.name())) {
                addElement(kitToAdd.name());
            }
        }
    }

    public static void addElement(String element) {
        String Query = "INSERT INTO `" + TABLE_NAME + "`(`" + KIT_NAME + "`)  VALUES "
                + "('" + element + "');";

        plugin.getDbConnection().query(Query, true);
    }

    private static boolean isElement(String name) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE `" + KIT_NAME + "` = "
                + "'" + name + "'";

        ResultSet result = plugin.getDbConnection().query(query, true).getResultSet();
        try {
            return result.next();
        } catch (SQLException e) {
            Output.logError(e.getSQLState());
        }
        return false;
    }
}
