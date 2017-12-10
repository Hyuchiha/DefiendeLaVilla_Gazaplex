/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Utils;

import com.hyuchiha.village_defense.Game.Kit;
import com.hyuchiha.village_defense.Main;
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
 *
 * @author hyuchiha
 */
public class ObjectsUtils {
    
    public static void showNewClassSelector(Player p) {
        int size = ((46 + 8) / 9) * 9;
        Inventory inv = Bukkit.createInventory(p, size, Translator.change("CLASS_SELECT_INV_TITLE"));

        ArrayList<Kit> notUnlocked = new ArrayList<>();

        int kitsCount = 0;

        for (Kit kit : Kit.values()) {
            if (kit.isOwnedBy(p)) {
                ItemStack i = kit.getKit().getIcon().clone();
                ItemMeta im = i.getItemMeta();
                List<String> lore = im.getLore();
                lore.add(ChatColor.GRAY + "---------------");
                lore.add(ChatColor.GREEN + Translator.string("UNLOCKED"));
                lore.add(ChatColor.GRAY + "---------------");
                im.setLore(lore);
                i.setItemMeta(im);
                inv.addItem(i);

                kitsCount++;
            } else {
                notUnlocked.add(kit);
            }
        }

        int kitsCopy = kitsCount;
        kitsCount = (Math.round((kitsCount + 8) / 9) + 1) * 9;

        ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.BLACK.getDyeData());
        ItemMeta metaglass = glass.getItemMeta();
        metaglass.setDisplayName(ChatColor.BLACK + "");
        glass.setItemMeta(metaglass);

        for (int i = kitsCopy; i < kitsCount; i++) {
            inv.setItem(i, glass);
        }

        for (Kit kit : notUnlocked) {
            ItemStack i = kit.getKit().getIcon().clone();
            ItemMeta im = i.getItemMeta();
            List<String> lore = im.getLore();
            lore.add(ChatColor.GRAY + "---------------");
            lore.add(ChatColor.RED + Translator.string("LOCKED"));
            lore.add(ChatColor.GRAY + "---------------");
            im.setLore(lore);
            i.setItemMeta(im);
            inv.addItem(i);
            kitsCount++;
        }

        for (int i = kitsCount; i < inv.getSize(); i++) {
            inv.setItem(i, glass);
        }

        p.openInventory(inv);
    }
    
    public static void showUnlockerSelector(Player p) {
        int size = ((Kit.values().length + 8) / 9) * 9;
        Inventory inv = Bukkit.createInventory(p, size, Translator.change("UNLOCK_INV_TITLE"));
        for (Kit kit : Kit.values()) {
            ItemStack i = kit.getKit().getIcon().clone();

            ItemMeta im = i.getItemMeta();
            List<String> lore = new ArrayList<>();

            int price = Main.getInstance()
                    .getConfig("kits.yml")
                    .getInt("Kits." + kit.name().toUpperCase() + ".price");

            lore.add(ChatColor.GRAY + "---------------");
            if (kit.isOwnedBy(p)) {
                lore.add(ChatColor.GREEN + Translator.string("UNLOCKED"));
            } else {
                lore.add(ChatColor.RED + Translator.string("LOCKED"));
                lore.add("");
                lore.add(ChatColor.RED + Translator.string("UNLOCK_WITH").replace("%POINTS%", Integer.toString(price)));
            }
            lore.add(ChatColor.GRAY + "---------------");
            im.setLore(lore);
            i.setItemMeta(im);
            inv.addItem(i);
        }
        p.openInventory(inv);
    }

}
