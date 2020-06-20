package com.hyuchiha.village_defense.Kits.Implementations;

import com.hyuchiha.village_defense.Game.GamePlayer;
import com.hyuchiha.village_defense.Game.Kit;
import com.hyuchiha.village_defense.Kits.Base.BaseKit;
import com.hyuchiha.village_defense.Manager.PlayerManager;
import com.hyuchiha.village_defense.Messages.Translator;
import com.hyuchiha.village_defense.Utils.KitUtils;
import com.hyuchiha.village_defense.Utils.XMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class Thor extends BaseKit {

  public Thor(String name, ItemStack icon, ConfigurationSection section) {
    super(name, icon, section);
  }

  @Override
  protected void setupSpawnItems() {
    spawnItems.add(new ItemStack(Material.STONE_SWORD));
    ItemStack hammer = XMaterial.GOLDEN_AXE.parseItem();
    ItemMeta meta = hammer.getItemMeta();
    meta.setDisplayName(Translator.getColoredString("KITS.THOR_ITEM"));
    hammer.setItemMeta(meta);
    spawnItems.add(hammer);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void RightClickChecks(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    GamePlayer gPlayer = PlayerManager.getPlayer(player);
    Action action = event.getAction();

    if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
      PlayerInventory inventory = player.getInventory();
      ItemStack handItem = inventory.getItemInMainHand();

      if (handItem != null && KitUtils.isItem(handItem, "KITS.THOR_ITEM")
          && gPlayer.getKit() == Kit.THOR) {
        //Se invoca a la lluvia de rayos
        event.setCancelled(true);
        KitUtils.strikeDamage(player);
      }
    }
  }
}
