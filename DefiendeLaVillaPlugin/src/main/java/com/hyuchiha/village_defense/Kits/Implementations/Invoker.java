package com.hyuchiha.village_defense.Kits.Implementations;

import com.hyuchiha.village_defense.Game.GamePlayer;
import com.hyuchiha.village_defense.Game.Kit;
import com.hyuchiha.village_defense.Kits.Base.BaseKit;
import com.hyuchiha.village_defense.Manager.PlayerManager;
import com.hyuchiha.village_defense.Utils.KitUtils;
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

public class Invoker extends BaseKit {
  public Invoker(String name, ItemStack icon, ConfigurationSection section) {
    super(name, icon, section);
  }

  @Override
  protected void setupSpawnItems() {
    spawnItems.add(new ItemStack(Material.WOOD_SWORD));

    ItemStack invocador = new ItemStack(Material.MONSTER_EGG, 3, (short) 12);
    ItemMeta meta = invocador.getItemMeta();
    meta.setDisplayName(ChatColor.GOLD + "Villager");
    invocador.setItemMeta(meta);
    spawnItems.add(invocador);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void RightClickChecks(PlayerInteractEvent event) {
    final Player player = event.getPlayer();
    GamePlayer eventPlayer = PlayerManager.getPlayer(player);

    ItemStack stack = event.getItem();
    if (stack != null) {
      if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) || (event.getAction() == Action.RIGHT_CLICK_AIR)) {

        if (eventPlayer.getKit() == Kit.INVOKER) {
          if (stack.getType() == Material.MONSTER_EGG) {
            if (KitUtils.isItem(stack, ChatColor.GOLD + "Villager")) {
              //Se invoca a un aldeano
              event.setCancelled(true);
              eventPlayer.getArena().getGame().getWave().addNewVillagerCreatedByPlayer();

              //Se elimina el objeto en mano
              int amount = player.getInventory().getItemInMainHand().getAmount() - 1;

              if (amount <= 0) {
                player.getInventory().removeItem(player.getInventory().getItemInMainHand());
              } else {
                player.getInventory().getItemInMainHand().setAmount(amount);
              }
            }
          }
        }
      }
    }
  }
}
