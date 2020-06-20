package com.hyuchiha.village_defense.Kits.Implementations;

import com.hyuchiha.village_defense.Game.GamePlayer;
import com.hyuchiha.village_defense.Game.Kit;
import com.hyuchiha.village_defense.Kits.Base.BaseKit;
import com.hyuchiha.village_defense.Manager.PlayerManager;
import com.hyuchiha.village_defense.Messages.Translator;
import com.hyuchiha.village_defense.Utils.KitUtils;
import com.hyuchiha.village_defense.Utils.Utils;
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
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class Invoker extends BaseKit {
  public Invoker(String name, ItemStack icon, ConfigurationSection section) {
    super(name, icon, section);
  }

  @Override
  protected void setupSpawnItems() {
    ItemStack woodSword = XMaterial.WOODEN_SWORD.parseItem();
    spawnItems.add(woodSword);

    ItemStack invocador = Utils.getVillagerEgg(3);
    ItemMeta meta = invocador.getItemMeta();
    meta.setDisplayName(Translator.getColoredString("KITS.INVOKER_ITEM"));
    invocador.setItemMeta(meta);
    spawnItems.add(invocador);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void RightClickChecks(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    GamePlayer gPlayer = PlayerManager.getPlayer(player);
    Action action = event.getAction();

    if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
      PlayerInventory inventory = player.getInventory();
      ItemStack handItem = inventory.getItemInMainHand();

      if (handItem != null && KitUtils.isItem(handItem, "KITS.INVOKER_ITEM")
          && gPlayer.getKit() == Kit.INVOKER) {
        //Se invoca a un aldeano
        event.setCancelled(true);
        gPlayer.getArena().getGame().getWave().addNewVillagerCreatedByPlayer();

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
