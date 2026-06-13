package com.hyuchiha.village_defense.Kits.Implementations;

import com.hyuchiha.village_defense.Arena.Arena;
import com.hyuchiha.village_defense.Event.ItemPurchaseEvent;
import com.hyuchiha.village_defense.Game.GamePlayer;
import com.hyuchiha.village_defense.Game.Kit;
import com.hyuchiha.village_defense.Kits.Base.BaseKit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class Merchant extends BaseKit {

  public Merchant(String name, ItemStack icon, ConfigurationSection section) {
    super(name, icon, section);
  }

  @Override
  protected void setupSpawnItems() {
    spawnItems.add(new ItemStack(Material.STONE_SWORD));
  }

  @EventHandler
  public void onItemPurchase(ItemPurchaseEvent event) {
    Arena arena = event.getArena();

    GamePlayer playerWhoPurchased = event.getPlayerWhoBuy();

    for (GamePlayer player : arena.getGame().getPlayersInGame()) {
      // Comparacion por UUID (no por getPlayer().getName(), que NPE si esta offline).
      if (player.getKit() == Kit.MERCHANT
          && !player.getPlayerUUID().equals(playerWhoPurchased.getPlayerUUID())) {
        player.setGems((int) (player.getGems() * .3));
      }
    }
  }
}
