/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Database;

import com.hyuchiha.village_defense.Main;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 *
 * @author hyuchiha
 */
public class StatsManager {

    private static final String DATABASE_QUERY = "CREATE TABLE IF NOT EXISTS `village_defense` ("
            + "  `uuid` varchar(36) NOT NULL,"
            + "  `username` varchar(16) NOT NULL,"
            + "  `kills` int(16) NOT NULL,"
            + "  `deaths` int(16) NOT NULL,"
            + "  `bosses_kills` int(16) NOT NULL,"
            + "  `max_wave_reached` int(16) NOT NULL,"
            + "  `min_wave_reached` int(16) NOT NULL,"
            + "  UNIQUE KEY `uuid` (`uuid`)"
            + ") ENGINE=InnoDB;";

    private static final String TABLE_NAME = "village_defense";
    private static final Main plugin = Main.getInstance();


    public static void InitDatabase() {
        plugin.getDbConnection().query(DATABASE_QUERY, true);
    }

    public static void InsertPlayer(Player player) {
        String query = "INSERT IGNORE INTO `" + TABLE_NAME + "` (`uuid`, `username`, `kills`, "
                + "`deaths`, `bosses_kills`, `max_wave_reached`, `min_wave_reached`) VALUES "
                + "('"
                + player.getUniqueId().toString() + "', '"
                + player.getName()
                + "', '0', '0', '0', '0', '0');";

        plugin.getDbConnection().query(query, true);
    }

    public static int getStat(StatType s, Player p) {
        String uuid = p.getUniqueId().toString();
        try {
            int stat = 0;
            ResultSet rs = plugin.getDbConnection().query("SELECT * FROM `" + TABLE_NAME + "` WHERE `uuid`='"
                    + uuid + "'", true).getResultSet();

            while (rs.next()) {
                stat = rs.getInt(s.name().toLowerCase());
            }

            return stat;
        } catch (SQLException ex) {
            return 0;
        }
    }

    public static void updateAllStatsOnLogin(Player player){
        String uuid = player.getUniqueId().toString();
        String username = player.getName();
        try{
            ResultSet rs = plugin
                    .getDbConnection()
                    .query("SELECT * FROM `" + TABLE_NAME + "` WHERE `uuid`='"
                            + uuid + "'", true).getResultSet();

            if (rs.next()) {
                PlayerStatsData data = PlayerStatsData.getPlayerStat(UUID.fromString(uuid), username);

                data.setKills(rs.getInt(StatType.KILLS.name().toLowerCase()));
                data.setDeaths(rs.getInt(StatType.DEATHS.name().toLowerCase()));
                data.setBosses_kills(rs.getInt(StatType.BOSSES_KILLS.name().toLowerCase()));
                data.setMax_wave_reached(rs.getInt(StatType.MAX_WAVE_REACHED.name().toLowerCase()));
                data.setMin_wave_reached(rs.getInt(StatType.MIN_WAVE_REACHED.name().toLowerCase()));

            }
        }catch(SQLException ex){
        }
    }

    public static void updateStatsFromPlayer(PlayerStatsData data){
        String query = "UPDATE `"+ TABLE_NAME +"` SET "
                + "`username`= '" + data.getUsername() + "',"
                + "`kills`= '" + data.getKills() + "',"
                + "`deaths`='" + data.getDeaths() + "',"
                + "`bosses_kills`='" + data.getBosses_kills() + "',"
                + "`max_wave_reached`='" + data.getMax_wave_reached() + "',"
                + "`min_wave_reached`='" + data.getMin_wave_reached() + "' "
                + "WHERE `uuid`='" + data.getUUID().toString() + "';";

        plugin.getDbConnection().query(query, true);

    }
}
