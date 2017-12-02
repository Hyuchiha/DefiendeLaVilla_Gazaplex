package com.hyuchiha.village_defense.Database;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

/**
 *
 * @author hyuchiha
 */
public class PlayerStatsData {
    private static final HashMap<UUID, PlayerStatsData> metaTable = new HashMap<>();

    public static PlayerStatsData getPlayerStat(UUID uuid, String username) {
        if (!metaTable.containsKey(uuid)) {
            metaTable.put(uuid, new PlayerStatsData(uuid, username));
        }
        return metaTable.get(uuid);
    }
    
    public static HashMap<UUID, PlayerStatsData> getAllPlayerStats(){
        return metaTable;
    }
    
    public static void removePlayerStat(UUID uuid){
        if (metaTable.containsKey(uuid)) {
            metaTable.remove(uuid);
        }
    }
    
    public static void resetPlayerStats(){
        metaTable.clear();
    }

    private final UUID UUID;
    private String username;
    private int kills;
    private int deaths;
    private int bosses_kills;
    private int max_wave_reached;
    private int min_wave_reached;
    
    public PlayerStatsData(UUID uuid, String username){
        this.UUID = uuid;
        this.username = username;
        this.kills = 0;
        this.deaths = 0;
        this.bosses_kills = 0;
        this.max_wave_reached = 0;
        this.min_wave_reached = 0;
    }

    public int getKills() {
        return kills;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(username);
    }
    
    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getBosses_kills() {
        return bosses_kills;
    }

    public void setBosses_kills(int bosses_kills) {
        this.bosses_kills = bosses_kills;
    }

    public int getMax_wave_reached() {
        return max_wave_reached;
    }

    public void setMax_wave_reached(int max_wave_reached) {
        this.max_wave_reached = max_wave_reached;
    }

    public int getMin_wave_reached() {
        return min_wave_reached;
    }

    public void setMin_wave_reached(int min_wave_reached) {
        this.min_wave_reached = min_wave_reached;
    }

    public String getUsername() {
        return username;
    }

    public UUID getUUID() {
        return UUID;
    }
}
