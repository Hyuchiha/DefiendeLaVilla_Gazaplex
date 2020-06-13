/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Arena;

import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Utils.ArenaUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.Configuration;

/**
 * @author hyuchiha
 */
public class MainLobby {
    private final Location lobbyLocation;
    private final World world;

    public MainLobby() {
        Configuration config = Main.getInstance().getConfig("arenas.yml");

        WorldCreator wc = new WorldCreator(config.getString("Spawn." + "worldName"));
        this.world = Bukkit.getServer().createWorld(wc);
        this.world.setGameRuleValue("doFireTick", "false");
        this.world.setGameRuleValue("doDaylightCycle", "false");

        this.lobbyLocation = ArenaUtils.parseStringToLocation(world, config.getString("Spawn." + "lobbyLocation"));
    }

    public Location getLobbyLocation() {
        return lobbyLocation;
    }

    public World getWorld() {
        return world;
    }

}
