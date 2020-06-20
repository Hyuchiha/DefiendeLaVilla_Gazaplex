/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Utils;

import com.hyuchiha.village_defense.Game.GamePlayer;
import com.hyuchiha.village_defense.Game.Kit;
import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Manager.PlayerManager;
import com.hyuchiha.village_defense.Messages.Translator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hyuchiha
 */
public class MenuUtils {

  public static void showKitSelector(Player p) {
    int size = ((46 + 8) / 9) * 9;
    Inventory inv = Bukkit.createInventory(p, size, Translator.getColoredString("INVENTORY.SELECT_CLASS_TITLE"));

    ArrayList<Kit> notUnlocked = new ArrayList<>();
    GamePlayer gPlayer = PlayerManager.getPlayer(p);

    int kitsCount = 0;
    Kit selectedKit = null;
    if (gPlayer.getKit() != Kit.CIVILIAN) {
      selectedKit = gPlayer.getKit();
    }

    //Build the inventory with the kits unlocked
    for (Kit kit : Kit.values()) {
      if (kit.isOwnedBy(p)) {
        ItemStack kitItem = buildItemForSelector(true, kit, selectedKit == kit);
        inv.addItem(kitItem);

        kitsCount++;
      } else {
        //Catch the kits not unlocked from this user
        notUnlocked.add(kit);
      }
    }

    int startPoint = kitsCount;
    kitsCount = (Math.round((kitsCount + 8) / 9) + 1) * 9;

    //Make the remaining Slots from glass
    fillInventoryWithGlass(startPoint, kitsCount, inv);

    //Fill the remaining kits
    for (Kit kit : notUnlocked) {
      ItemStack restantKits = buildItemForSelector(false, kit, false);
      inv.addItem(restantKits);
      kitsCount++;
    }

    //Make the restant Slots from glass
    fillInventoryWithGlass(kitsCount, inv.getSize(), inv);

    p.openInventory(inv);
  }

  public static void showUnlockerSelector(Player p) {
    int size = ((46 + 8) / 9) * 9;
    Inventory inv = Bukkit.createInventory(p, size, Translator.getColoredString("INVENTORY.UNLOCK_CLASS_TITLE"));

    int kitsCount = 0;

    //Build the inventory with the kits unlocked
    for (Kit kit : Kit.values()) {
      if (!kit.isOwnedBy(p)) {
        ItemStack restantKits = buildItemForSelector(false, kit, false);
        inv.addItem(restantKits);
        kitsCount++;
      }
    }

    //Make the restant Slots from glass
    fillInventoryWithGlass(kitsCount, inv.getSize(), inv);

    p.openInventory(inv);
  }

  public static void showConfirmUnlockClass(Player p, Kit kit) {
    Inventory inv = createInventory(p, 9, Translator.getColoredString("GAME.CONFIRM_UNLOCK"));

    ItemStack item = kit.getKit().getIcon().clone();
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(ChatColor.GOLD + kit.getName());
    item.setItemMeta(meta);

    ItemStack yes = new ItemStack(Material.EMERALD_BLOCK);
    ItemMeta yesMeta = yes.getItemMeta();
    yesMeta.setDisplayName(ChatColor.GREEN + Translator.getString("COMMONS.TOTALLY"));
    yes.setItemMeta(yesMeta);

    ItemStack no = new ItemStack(Material.REDSTONE_BLOCK);
    ItemMeta noMeta = no.getItemMeta();
    noMeta.setDisplayName(ChatColor.RED + Translator.getString("COMMONS.NOPE"));
    no.setItemMeta(noMeta);

    inv.setItem(2, yes);
    inv.setItem(4, item);
    inv.setItem(6, no);

    p.openInventory(inv);
  }

  private static Inventory createInventory(Player player, int slots, String name) {
    return Bukkit.createInventory(player, slots, name);
  }

  private static ItemStack buildItemForSelector(boolean isUnlocked, Kit kit, boolean hasKitSelected) {
    ItemStack kitLogo = kit.getKit().getIcon().clone();
    ItemMeta meta = kitLogo.getItemMeta();
    List<String> lore = meta.getLore();

    int price = Main.getInstance()
        .getConfig("kits.yml")
        .getInt("Kits." + kit.name().toUpperCase() + ".price");

    if (isUnlocked) {
      if (!hasKitSelected) {
        lore.add(ChatColor.GRAY + "---------------");
        lore.add(ChatColor.GREEN + Translator.getColoredString("COMMONS.UNLOCKED"));
        lore.add(ChatColor.GRAY + "---------------");
      }
    } else {
      lore.add(ChatColor.GRAY + "---------------");
      lore.add(ChatColor.RED + Translator.getColoredString("COMMONS.LOCKED"));
      lore.add(ChatColor.GRAY + "---------------");
      lore.add(ChatColor.RED + Translator.getColoredString("GAME.UNLOCK_WITH").replace("%POINTS%", Integer.toString(price)));
    }

    meta.setLore(lore);
    kitLogo.setItemMeta(meta);

    return kitLogo;
  }

  private static void fillInventoryWithGlass(int start, int end, Inventory inv) {
    ItemStack glass = Utils.getDyeGlassPane(DyeColor.BLACK);
    ItemMeta metaglass = glass.getItemMeta();
    metaglass.setDisplayName(ChatColor.BLACK + "");
    glass.setItemMeta(metaglass);

    for (int i = start; i < end; i++) {
      inv.setItem(i, glass);
    }
  }

}
