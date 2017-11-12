/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hyuchiha
 */
public class ArenaUtils {
    
    public static Location parseStringToLocation(World w, String in) {
        String[] params = in.split(",");
        for (String s : params) {
            s.replace("-0", "0");
        }
        if (params.length == 3 || params.length == 5) {
            double x = Double.parseDouble(params[0]);
            double y = Double.parseDouble(params[1]);
            double z = Double.parseDouble(params[2]);
            Location loc = new Location(w, x, y, z);
            if (params.length == 5) {
                loc.setYaw(Float.parseFloat(params[4]));
                loc.setPitch(Float.parseFloat(params[5]));
            }
            return loc;
        }
        return null;
    }
    
    public static String parseLocationToString(Location loc){
        String value = loc.getBlockX()+","+loc.getBlockY()+","+loc.getBlockZ(); 
        return value;
    }
    
    public static List<String> parseListLocationToListString(List<Location> list){
        List<String> values = new ArrayList<String>();
        
        for(Location loc:list){
            values.add(parseLocationToString(loc));
        }
        
        return values;
    }
    
    public static void giveArenaLobbyObjects(Player player){
        ItemStack kitselector = new ItemStack(Material.DIAMOND);
        ItemMeta itemMetaSelector = kitselector.getItemMeta();
        itemMetaSelector.setDisplayName(ChatColor.BLUE + "Kit");
        kitselector.setItemMeta(itemMetaSelector);
        player.getInventory().setItem(0, kitselector);
        player.updateInventory();
        
        ItemStack kitPurchase = new ItemStack(Material.GOLD_INGOT);
        ItemMeta itemMetaPurchase = kitPurchase.getItemMeta();
        itemMetaPurchase.setDisplayName(ChatColor.GOLD + "Desbloquear Kit");
        kitPurchase.setItemMeta(itemMetaPurchase);
        player.getInventory().setItem(1, kitPurchase);
        player.updateInventory();
        
        ItemStack exitArena = new ItemStack(Material.COMPASS);
        ItemMeta itemMetaExitArena = exitArena.getItemMeta();
        itemMetaExitArena.setDisplayName(ChatColor.GOLD + "Abandonar Arena");
        exitArena.setItemMeta(itemMetaExitArena);
        player.getInventory().setItem(8, exitArena);
        player.updateInventory();
    }
    
    public static void giveShopObjects(Player player){
        ItemStack ShopEquipo = new ItemStack(Material.EMERALD);
        ItemMeta EquipoMeta = ShopEquipo.getItemMeta();
        EquipoMeta.setDisplayName(ChatColor.GREEN + "Tienda de Equipo");
        ShopEquipo.setItemMeta(EquipoMeta);
        player.getInventory().setItem(0, ShopEquipo);
        player.updateInventory();
        
        ItemStack ShopOther = new ItemStack(Material.REDSTONE);
        ItemMeta OtherMeta = ShopOther.getItemMeta();
        OtherMeta.setDisplayName(ChatColor.BLUE + "Tienda de Otros");
        ShopOther.setItemMeta(OtherMeta);
        player.getInventory().setItem(1, ShopOther);
        player.updateInventory();
        
        ItemStack ShopVip = new ItemStack(Material.QUARTZ);
        ItemMeta VipMeta = ShopVip.getItemMeta();
        VipMeta.setDisplayName(ChatColor.GOLD + "Tienda Vip");
        ShopVip.setItemMeta(VipMeta);
        player.getInventory().setItem(2, ShopVip);
        player.updateInventory();
    }
    
    public static void givePrincipalLobbyObjects(Player player){
        
        ItemStack exit = new ItemStack(Material.COMPASS);
        ItemMeta itemMetaExitArena = exit.getItemMeta();
        itemMetaExitArena.setDisplayName(ChatColor.GOLD + "Volver al lobby");
        exit.setItemMeta(itemMetaExitArena);
        player.getInventory().setItem(8, exit);
        player.updateInventory();
    }
    
    public static void giveSpectatorObjects(Player player){
        ItemStack exit = new ItemStack(Material.APPLE);
        ItemMeta itemMetaExitArena = exit.getItemMeta();
        itemMetaExitArena.setDisplayName(ChatColor.GOLD + "Dejar de espectar");
        exit.setItemMeta(itemMetaExitArena);
        player.getInventory().setItem(8, exit);
        player.updateInventory();
    }
}
