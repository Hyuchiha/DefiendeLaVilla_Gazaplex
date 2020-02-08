/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Arena;

import com.avaje.ebeaninternal.server.lib.util.NotFoundException;
import com.hyuchiha.village_defense.Game.Game;
import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Manager.ArenaManager;
import com.hyuchiha.village_defense.Messages.Translator;
import com.hyuchiha.village_defense.Output.Output;
import com.hyuchiha.village_defense.Utils.ArenaUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.Configuration;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hyuchiha
 */
public class Arena {

    private final String Name;
    private Location signLocation;
    private Location lobbyGameLocation;
    private Location spawnArenaLocation;
    private List<Location> mobspawns;
    private int MaxNumberOfPlayers;

    private Game game;

    public Arena(String name) {
        this.Name = name;

        configArena();

        this.game = new Game(Name);

        initSign();
    }

    public void configArena() {
        Configuration arenaConf = Main.getInstance().getConfig("arenas.yml");

        //Se pide el mundo del spawn por que alli es donde estara 
        //el sign es probable que esto de problema
        WorldCreator wc = new WorldCreator(Name);
        World world = Bukkit.getServer().createWorld(wc);
        
        this.signLocation = ArenaUtils.parseStringToLocation(ArenaManager.getLobby().getWorld(), arenaConf.getString(Name + ".sign"));
        this.lobbyGameLocation = ArenaUtils.parseStringToLocation(world, arenaConf.getString(Name + ".lobby"));
        this.spawnArenaLocation = ArenaUtils.parseStringToLocation(world, arenaConf.getString(Name + ".spawn"));

        this.mobspawns = new ArrayList<>();
        for (String loc : arenaConf.getStringList(Name + ".mobspawns")) {
            Location mobspawn = ArenaUtils.parseStringToLocation(world, loc);
            mobspawns.add(mobspawn);
        }

        this.MaxNumberOfPlayers = arenaConf.getInt(Name + ".maxPlayers");
        
        initSettingForArena(world);
    }
    
    public void initSettingForArena(World world){
        world.setGameRuleValue("doFireTick", "false");
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setGameRuleValue("doMobSpawning", "false");
    }

    public void saveConfigArena() {
        Configuration arenaConf = Main.getInstance().getConfig("arenas.yml");
    
        arenaConf.set(Name + ".sign", ArenaUtils.parseLocationToString(signLocation));
        arenaConf.set(Name + ".lobby", ArenaUtils.parseLocationToString(lobbyGameLocation));
        arenaConf.set(Name + ".spawn", ArenaUtils.parseLocationToString(spawnArenaLocation));
        arenaConf.set(Name + ".mobspawns", ArenaUtils.parseListLocationToListString(mobspawns));
        arenaConf.set(Name + ".maxPlayers", MaxNumberOfPlayers);
    }

    public void initSign() {
        try {
            Block b = signLocation.getBlock();
            if (b == null) {
                return;
            }

            Material m = b.getType();
            if (m == Material.SIGN_POST || m == Material.WALL_SIGN) {
                updateSign();
            }
        } catch (Exception e) {
            Output.logError(Color.RED + "Error en la creacion de los signs \n" + e.getMessage());
        }
    }

    public void updateSign() {
        Location l = signLocation;
        Block b = l.getBlock();
        if (b == null) {
            return;
        }

        try {
            Material m = b.getType();
            if (m == Material.SIGN_POST || m == Material.WALL_SIGN) {
                Sign s = (Sign) b.getState();


                s.setLine(0, ChatColor.DARK_PURPLE + "Arena");
                s.setLine(1, ChatColor.BOLD + Name);
                s.setLine(2, ChatColor.UNDERLINE.toString() + game.getPlayersInGame().size()
                        + " " + Translator.change("PLAYER")
                        + (game.getPlayersInGame().size() > 1 ? "" : "s"));

                s.setLine(3, ChatColor.BOLD.toString() + game.getState().name());

                s.update(true);
            }
            
        } catch (NullPointerException e) {
            Output.logError(Color.RED + "Error en la creacion de los signs \n" + e.getMessage());
        }
    }

    public void updateState() {
        try {
            BlockState blockState = signLocation.getBlock().getState();

            if (blockState instanceof Sign) {
                Sign sign = (Sign) blockState;

                if (!sign.getChunk().isLoaded()) {
                    sign.getChunk().load();
                }

                sign.setLine(3, ChatColor.BOLD.toString() + game.getState().name());
                sign.update();

            }
        } catch (NotFoundException ignored) {
            Output.logError(Color.RED + "Error en la creacion de los signs \n" + ignored.getMessage());
        }
    }

    public void removeSign() {
        try {
            Location signs = signLocation;
            BlockState blockState = signs.getBlock().getState();

            if ((blockState instanceof Sign)) {
                Sign sign = (Sign) blockState;

                if (!sign.getChunk().isLoaded()) {
                    sign.getChunk().load();
                }

                sign.removeMetadata("Arena", Main.getInstance());

                sign.setLine(1, Name);
                sign.setLine(2, 0 + "/" + MaxNumberOfPlayers);
                sign.setLine(3, "Desabilitada");
                sign.update();
            }
        } catch (NotFoundException e) {
            Output.logError("No se puede remover el sign");
        }
    }

    public Location getSignLocation() {
        return signLocation;
    }

    public void setSignLocation(Location signLocation) {
        this.signLocation = signLocation;
    }

    public Location getLobbyGameLocation() {
        return lobbyGameLocation;
    }

    public void setLobbyGameLocation(Location lobbyGameLocation) {
        this.lobbyGameLocation = lobbyGameLocation;
    }

    public Location getSpawnArenaLocation() {
        return spawnArenaLocation;
    }

    public void setSpawnArenaLocation(Location spawnArenaLocation) {
        this.spawnArenaLocation = spawnArenaLocation;
    }

    public List<Location> getMobspawns() {
        return mobspawns;
    }

    public void setMobspawns(List<Location> mobspawns) {
        this.mobspawns = mobspawns;
    }

    public int getMaxNumberOfPlayers() {
        return MaxNumberOfPlayers;
    }

    public void setMaxNumberOfPlayers(int MaxNumberOfPlayers) {
        this.MaxNumberOfPlayers = MaxNumberOfPlayers;
    }

    public String getName() {
        return Name;
    }

    public Game getGame() {
        return game;
    }

    public void createNewGame() {
        game.removeWaveData();
        this.game = new Game(Name);
        updateSign();
    }
}
