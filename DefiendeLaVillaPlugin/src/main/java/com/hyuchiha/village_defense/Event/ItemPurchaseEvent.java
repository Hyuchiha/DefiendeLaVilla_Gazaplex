package com.hyuchiha.village_defense.Event;

import com.hyuchiha.village_defense.Arena.Arena;
import com.hyuchiha.village_defense.Game.GamePlayer;
import com.hyuchiha.village_defense.Game.ShopItem;

/**
 * @author hyuchiha
 */
public class ItemPurchaseEvent extends BaseEvent {

  private final ShopItem purchasedItem;
  private final Arena arena;
  private final GamePlayer playerWhoBuy;


  public ItemPurchaseEvent(ShopItem item, Arena arena, GamePlayer player) {
    this.purchasedItem = item;
    this.arena = arena;
    this.playerWhoBuy = player;
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
