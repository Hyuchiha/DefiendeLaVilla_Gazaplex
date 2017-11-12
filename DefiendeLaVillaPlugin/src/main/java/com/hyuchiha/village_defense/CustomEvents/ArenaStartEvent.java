/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.CustomEvents;

import com.hyuchiha.village_defense.Arena.Arena;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author hyuchiha
 */
public class ArenaStartEvent extends Event{
    
    
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }
    private Arena arena;

    public ArenaStartEvent(Arena arena) {
        this.arena = arena;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Arena getArena() {
        return arena;
    }
}
