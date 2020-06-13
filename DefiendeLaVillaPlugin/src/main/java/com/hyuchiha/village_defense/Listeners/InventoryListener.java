package com.hyuchiha.village_defense.Listeners;

import com.google.common.base.Enums;
import com.hyuchiha.village_defense.Game.GamePlayer;
import com.hyuchiha.village_defense.Game.Kit;
import com.hyuchiha.village_defense.Game.PlayerState;
import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Manager.PlayerManager;
import com.hyuchiha.village_defense.Messages.Translator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

/**
 * @author hyuchiha
 */
public class InventoryListener implements Listener {

  private final Main plugin;

  public InventoryListener(Main plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent e) {
    Inventory inv = e.getInventory();
    Player player = (Player) e.getWhoClicked();

    if (inv.getTitle().startsWith(Translator.getColoredString("INVENTORY.SELECT_CLASS_TITLE"))) {

      if (e.getCurrentItem().getType() == Material.AIR || e.getCurrentItem().getType() == null) {
        return;
      }

      player.closeInventory();
      e.setCancelled(true);
      String name = e.getCurrentItem().getItemMeta().getDisplayName();
      GamePlayer meta = PlayerManager.getPlayer(player);

      if (meta.getKit() != Kit.CIVILIAN && meta.getState() == PlayerState.LOBBY_GAME) {
        player.sendMessage(Translator.getPrefix() + " " + Translator.getColoredString("GAME.ALREADY_SELECTED_CLASS"));
        return;
      }

      Kit toChoose = Enums.getIfPresent(Kit.class, ChatColor.stripColor(name).toUpperCase()).orNull();

      if (toChoose != null) {
        if (!toChoose.isOwnedBy(player)) {
          player.sendMessage(Translator.getPrefix() + " " + Translator.getColoredString("ERROR.DONT_HAS_CLASS_UNLOCKED"));
          return;
        }

        meta.setKit(toChoose);

        String classSelected = Translator.getColoredString("GAME.ALREADY_SELECTED_CLASS");
        classSelected = classSelected.replace("%CLASS%", ChatColor.stripColor(name));
        player.sendMessage(Translator.getPrefix() + " " + classSelected);
      } else {
        player.sendMessage(Translator.getPrefix() + " " + Translator.getColoredString("ERROR.NO_CLASS_FOUND"));
      }

      return;
    }

    if (inv.getTitle().startsWith(Translator.getColoredString("INVENTORY.UNLOCK_INV_TITLE"))) {

      if (e.getCurrentItem().getType() == Material.AIR || e.getCurrentItem().getType() == null) {
        return;
      }

      String name = e.getCurrentItem().getItemMeta().getDisplayName();

      Kit toChoose = Enums.getIfPresent(Kit.class, ChatColor.stripColor(name).toUpperCase()).orNull();

      if (toChoose != null && !toChoose.isOwnedBy(player)) {

        double money = Main.getInstance().
            getConfig("kits.yml").
            getInt("Kits." + name.toUpperCase() + ".price");
        double userMoney = PlayerManager.getMoney(player);

        if (userMoney >= money) {
          plugin.getDatabase().addUnlockedKit(player.getUniqueId().toString(), name);

          PlayerManager.withdrawMoney(player, money);

          String classUnlocked = Translator.getColoredString("GAME.UNLOCK_CLASS");
          player.sendMessage(Translator.getPrefix() + " " + classUnlocked.replace("%CLASS%", name));
        } else {
          player.sendMessage(Translator.getPrefix() + " " + Translator.getColoredString("ERROR.DONT_HAVE_REQUIRED_MONEY"));
        }

        player.closeInventory();
        e.setCancelled(true);
      } else {
        player.closeInventory();
        e.setCancelled(true);
        player.sendMessage(Translator.getPrefix() + " " + Translator.getColoredString("GAME.ALREADY_OWN_CLASS"));
      }

    }

  }
}
