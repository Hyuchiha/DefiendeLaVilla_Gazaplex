package com.hyuchiha.village_defense.Kits.Implementations;

import com.hyuchiha.village_defense.Game.GamePlayer;
import com.hyuchiha.village_defense.Game.Kit;
import com.hyuchiha.village_defense.Kits.Base.BaseKit;
import com.hyuchiha.village_defense.Manager.PlayerManager;
import com.hyuchiha.village_defense.Utils.KitUtils;
import com.hyuchiha.village_defense.Utils.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
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
    meta.setDisplayName(ChatColor.GOLD + "Hammer");
    hammer.setItemMeta(meta);
    spawnItems.add(hammer);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void RightClickChecks(PlayerInteractEvent event) {
    final Player player = event.getPlayer();
    GamePlayer eventPlayer = PlayerManager.getPlayer(player);

    ItemStack stack = event.getItem();
    if (stack != null) {
      if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) || (event.getAction() == Action.RIGHT_CLICK_AIR)) {
        if (eventPlayer.getKit() == Kit.THOR) {
          if (stack.getType() == XMaterial.GOLDEN_AXE.parseMaterial()) {
            if (KitUtils.isItem(stack, ChatColor.GOLD + "Hammer")) {
              //Se invoca a la lluvia de rayos
              event.setCancelled(true);
              KitUtils.strikeDamage(player);
            }
          }
        }
      }
    }
  }
}
