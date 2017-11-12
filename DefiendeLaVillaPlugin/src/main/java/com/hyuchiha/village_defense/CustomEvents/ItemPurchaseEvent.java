/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.CustomEvents;

import com.hyuchiha.village_defense.Arena.Arena;
import com.hyuchiha.village_defense.Game.ShopItem;
import com.hyuchiha.village_defense.Game.GamePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author hyuchiha
 */
public class ItemPurchaseEvent extends Event{
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    private final ShopItem purchasedItem;
    private final Arena arena;
    private final GamePlayer playerWhoBuy;
    
    
    public ItemPurchaseEvent(ShopItem item, Arena arena, GamePlayer player){
        this.purchasedItem = item;
        this.arena = arena;
        this.playerWhoBuy = player;
    }
    

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public ShopItem getPurchasedItem() {
        return purchasedItem;
    }

    public Arena getArena() {
        return arena;
    }

    public GamePlayer getPlayerWhoBuy() {
        return playerWhoBuy;
    }

}
