/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Database;

import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Output.Output;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author hyuchiha
 */
public class KitsUnlockedManager {
    private static final String DATABASE_QUERY = "CREATE TABLE IF NOT EXISTS `KitsUnlocked` "
            + "(`clv_kit` int(6) NOT NULL,"
            + "`player` varchar(36) NOT NULL, "
            + "PRIMARY KEY (`clv_kit` , `player` ) ,"
            + "FOREIGN KEY (`clv_kit`) REFERENCES Kits(`clv_kit`), "
            + "FOREIGN KEY (`player`) REFERENCES village_defense(`uuid`) )  "
            + "ENGINE=InnoDB;";

    private static final String TABLE_NAME = "KitsUnlocked";
    private static final String UUID = "player";
    private static final Main plugin = Main.getInstance();
    
    public static void InitDatabase() {
        plugin.getDbConnection().query(DATABASE_QUERY, true);
    }
    
    public static void addUnlockedKit(String uuid, String kit){
        int idKit = KitsManager.getIdOfElement(kit);
        
        if(idKit == -1){
            return;
        }
        
        String Query = "INSERT INTO `" + TABLE_NAME + "`(`clv_kit`,`" + UUID + "`)  VALUES "
                + "('" + idKit + "', '"+ uuid +"');";
        
        plugin.getDbConnection().query(Query, true);
    }
    
    public static boolean hasKit(String uuid, String Kit){
        try {
            String Query = "SELECT * FROM "+ TABLE_NAME + " WHERE `" + UUID +"` = '"
                    + uuid +"'";
            
            ResultSet result = plugin.getDbConnection().query(Query, true).getResultSet();
            
            int idKit = KitsManager.getIdOfElement(Kit);
            
            while(result.next()){
                int kitOwned = result.getInt("clv_kit");
                
                if(kitOwned == idKit){
                   return true; 
                }
            }
        } catch (SQLException ex) {
            Output.logError(ex.getSQLState());
        }
        return false;
    }
}
