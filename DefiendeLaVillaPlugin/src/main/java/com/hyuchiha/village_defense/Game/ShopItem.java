/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Game;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;

/**
 *
 * @author hyuchiha
 */
public class ShopItem {

    private final ItemStack item;
    private final int price;

    public ShopItem(Material type, int qty, int price) {
        this.item = new ItemStack(type);
        this.price = price;
        this.item.setAmount(qty);
    }
    
    public ShopItem(ItemStack type, int price){
        this.item = type;
        this.price = price;
    }

    public ShopItem setName(String name) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + name);
        item.setItemMeta(meta);
        return this;
    }

    public void addEnchant(Enchantment ench, int level){
        this.item.addUnsafeEnchantment(ench, level);
    }
    
    public ItemStack getShopStack() {
        ItemStack stack = item.clone();
        String priceStr = ChatColor.GOLD.toString() + price + " Gems";
        ItemMeta meta = stack.getItemMeta();
        if (meta.hasLore()) {
            meta.getLore().add(priceStr);
        } else {
            meta.setLore(Collections.singletonList(priceStr));
        }
        stack.setItemMeta(meta);
        return stack;
    }

    public ItemStack getItemStack() {
        return item;
    }

    public int getPrice() {
        return price;
    }

    public String getName() {
        String name;
        ItemMeta meta = item.getItemMeta();
        if (meta.hasDisplayName()) {
            name = meta.getDisplayName();
        } else {
            name = item.getType().name();
            name = name.replace("_", " ").toLowerCase();
            name = WordUtils.capitalize(name);
            name += ChatColor.WHITE; // In case it's a wand
        }
        if (item.getAmount() > 1) {
            name = item.getAmount() + " " + name;
        }

        return name;
    }
}
