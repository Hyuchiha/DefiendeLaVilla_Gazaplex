/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.CustomEvents;

import com.hyuchiha.village_defense.Game.GamePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author hyuchiha
 */
public class ArenaJoinEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private String arenaName;
    private GamePlayer player;
    
    public ArenaJoinEvent(String arenaName, GamePlayer playerjoin){
        this.arenaName = arenaName;
        this.player = playerjoin;
    }
    

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public String getArenaName() {
        return arenaName;
    }

    public GamePlayer getPlayer() {
        return player;
    }


}
