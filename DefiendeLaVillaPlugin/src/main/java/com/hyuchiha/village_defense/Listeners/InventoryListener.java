package com.hyuchiha.village_defense.Listeners;

import com.google.common.base.Enums;
import com.hyuchiha.village_defense.Game.GamePlayer;
import com.hyuchiha.village_defense.Game.Kit;
import com.hyuchiha.village_defense.Game.PlayerState;
import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Manager.PlayerManager;
import com.hyuchiha.village_defense.Messages.Translator;
import com.hyuchiha.village_defense.Utils.MenuUtils;
import com.hyuchiha.village_defense.Utils.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author hyuchiha
 */
public class InventoryListener implements Listener {

  private final Main plugin;

  public InventoryListener(Main plugin) {
    this.plugin = plugin;
  }

  @EventHandler()
  public void onSelectClass(InventoryClickEvent e) {
    Player player = (Player) e.getWhoClicked();
    GamePlayer vdPlayer = PlayerManager.getPlayer(player);
    InventoryView view = e.getView();

    if (view.getTitle().equals(Translator.getColoredString("INVENTORY.SELECT_CLASS_TITLE"))) {
      ItemStack clickedItem = e.getCurrentItem();

      if (clickedItem == null || clickedItem.getType() == Material.AIR) {
        return;
      }

      e.setCancelled(true);

      if (clickedItem.getType() == XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial()) {
        return;
      }

      player.closeInventory();

      ItemMeta itemMeta = clickedItem.getItemMeta();
      String itemName = itemMeta.getDisplayName();

      if (vdPlayer.getKit() != Kit.CIVILIAN && vdPlayer.getState() == PlayerState.LOBBY_GAME) {
        player.sendMessage(Translator.getPrefix() + Translator.getColoredString("GAME.ALREADY_SELECTED_CLASS"));
        return;
      }

      Kit kitSelected = Enums.getIfPresent(Kit.class, ChatColor.stripColor(itemName).toUpperCase()).orNull();

      if (kitSelected != null) {
        if (!kitSelected.isOwnedBy(player)) {
          player.sendMessage(Translator.getPrefix() + Translator.getColoredString("ERROR.DONT_HAVE_CLASS_UNLOCKED"));
          return;
        }

        vdPlayer.setKit(kitSelected);

        String classSelected = Translator.getColoredString("GAME.CLASS_SELECTED");
        classSelected = classSelected.replace("%CLASS%", ChatColor.stripColor(itemName));
        player.sendMessage(Translator.getPrefix() + classSelected);
      } else {
        player.sendMessage(Translator.getPrefix() + Translator.getColoredString("ERROR.NO_CLASS_FOUND"));
      }

    }
  }

  @EventHandler()
  public void onUnlockClass(InventoryClickEvent e) {
    Player player = (Player) e.getWhoClicked();
    InventoryView view = e.getView();

    if (view.getTitle().equals(Translator.getColoredString("INVENTORY.UNLOCK_CLASS_TITLE"))) {
      ItemStack clickedItem = e.getCurrentItem();

      if (clickedItem == null || clickedItem.getType() == Material.AIR) {
        return;
      }

      e.setCancelled(true);

      if (clickedItem.getType() == XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial()) {
        return;
      }

      player.closeInventory();

      ItemMeta itemMeta = clickedItem.getItemMeta();
      String itemName = itemMeta.getDisplayName();

      Kit kitSelected = Enums.getIfPresent(Kit.class, ChatColor.stripColor(itemName).toUpperCase()).orNull();

      if (kitSelected != null && !kitSelected.isOwnedBy(player)) {
        MenuUtils.showConfirmUnlockClass(player, kitSelected);
      } else {
        player.sendMessage(Translator.getPrefix() + Translator.getColoredString("GAME.ALREADY_OWN_CLASS"));
      }
    }
  }

  @EventHandler
  public void onConfirmUnlock(InventoryClickEvent e) {
    Player player = (Player) e.getWhoClicked();
    InventoryView view = e.getView();
    Inventory inventory = e.getClickedInventory();

    if (view.getTitle().equals(Translator.getColoredString("GAME.CONFIRM_UNLOCK"))) {
      ItemStack clickedItem = e.getCurrentItem();

      if (clickedItem == null || clickedItem.getType() == Material.AIR) {
        return;
      }

      e.setCancelled(true);

      if (e.getCurrentItem().getType() == Material.EMERALD_BLOCK || e.getCurrentItem().getType() == Material.REDSTONE_BLOCK) {

        player.closeInventory();

        String name = inventory.getItem(4).getItemMeta().getDisplayName();

        if (e.getCurrentItem().getType() == Material.REDSTONE_BLOCK) {
          return;
        }

        double money = Main.getInstance().getConfig("kits.yml").getInt("Kits." + name.toUpperCase() + ".price");
        double userMoney = PlayerManager.getMoney(player);

        if (userMoney >= money) {
          Main.getInstance().getMainDatabase().addUnlockedKit(player.getUniqueId().toString(), ChatColor.stripColor(name).toUpperCase());

          PlayerManager.withdrawMoney(player, money);

          String classUnlocked = Translator.getColoredString("GAME.UNLOCK_CLASS");
          player.sendMessage(Translator.getPrefix() + " " + classUnlocked.replace("%CLASS%", name));
        } else {
          player.sendMessage(Translator.getPrefix() + " " + Translator.getColoredString("ERROR.DONT_HAVE_REQUIRED_MONEY"));
        }

      }

    }
  }
}
