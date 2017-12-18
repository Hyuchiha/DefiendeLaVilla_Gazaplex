package com.hyuchiha.village_defense.Database.Base;

import com.hyuchiha.village_defense.Game.Kit;

import java.util.ArrayList;
import java.util.List;

public class Account {
    private String uuid;
    private String name;
    private int kills;
    private int deaths;
    private int bosses_kills;
    private int max_wave_reached;
    private int min_wave_reached;

    private List<Kit> kits = new ArrayList<>();

    public Account(String uuid, String name, int kills, int deaths, int bosses_kills, int max_wave_reached, int min_wave_reached) {
        this.uuid = uuid;
        this.name = name;
        this.kills = kills;
        this.deaths = deaths;
        this.bosses_kills = bosses_kills;
        this.max_wave_reached = max_wave_reached;
        this.min_wave_reached = min_wave_reached;
    }

    public Account(String uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public String getUUID() {
        return uuid;
    }

    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getKills() {
        return kills;
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

    public List<Kit> getKits() {
        return kits;
    }

    public void addUnlockedKit(Kit kit){
        kits.add(kit);
    }

    public void setKits(List<Kit> kits) {
        this.kits = kits;
    }

    public boolean hasKit(Kit kitToVerify) {
        for(Kit kit : kits){
            if(kit == kitToVerify){
                return true;
            }
        }

        return false;
    }
}
