/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.CustomEvents;

import com.hyuchiha.village_defense.Arena.Arena;
import com.hyuchiha.village_defense.Game.GamePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author hyuchiha
 */
public class ArenaLeaveEvent extends Event{
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    private GamePlayer player;
    private Arena arena;
    
    public ArenaLeaveEvent(GamePlayer player, Arena arena){
        this.player = player;
        this.arena=arena;
    }
    

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public GamePlayer getPlayer() {
        return player;
    }

    public Arena getArena() {
        return arena;
    }

}
