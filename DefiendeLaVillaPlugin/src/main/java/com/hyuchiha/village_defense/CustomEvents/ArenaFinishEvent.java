/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.CustomEvents;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author hyuchiha
 */
public class ArenaFinishEvent extends Event{
    
    
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }
    private String arena;

    public ArenaFinishEvent(String arena) {
        this.arena = arena;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public String getArena() {
        return arena;
    }
}
