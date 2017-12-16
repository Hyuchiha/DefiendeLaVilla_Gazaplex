/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Utils;

import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Messages.Translator;
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
        return loc.getBlockX()+","+loc.getBlockY()+","+loc.getBlockZ();
    }
    
    public static List<String> parseListLocationToListString(List<Location> list){
        List<String> values = new ArrayList<>();
        
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
        itemMetaPurchase.setDisplayName(Translator.change("UNLOCK_KIT"));
        kitPurchase.setItemMeta(itemMetaPurchase);
        player.getInventory().setItem(1, kitPurchase);
        player.updateInventory();
        
        ItemStack exitArena = new ItemStack(Material.COMPASS);
        ItemMeta itemMetaExitArena = exitArena.getItemMeta();
        itemMetaExitArena.setDisplayName(Translator.change("LEAVE_ARENA"));
        exitArena.setItemMeta(itemMetaExitArena);
        player.getInventory().setItem(8, exitArena);
        player.updateInventory();
    }
    
    public static void giveShopObjects(Player player){
        ItemStack ShopEquipo = new ItemStack(Material.EMERALD);
        ItemMeta EquipoMeta = ShopEquipo.getItemMeta();
        EquipoMeta.setDisplayName(Translator.change("EQUIPMENT_STORE"));
        ShopEquipo.setItemMeta(EquipoMeta);
        player.getInventory().setItem(0, ShopEquipo);
        player.updateInventory();

        ItemStack ShopCombat = new ItemStack(Material.QUARTZ);
        ItemMeta CombatMeta = ShopCombat.getItemMeta();
        CombatMeta.setDisplayName(Translator.change("COMBAT_STORE"));
        ShopCombat.setItemMeta(CombatMeta);
        player.getInventory().setItem(1, ShopCombat);
        player.updateInventory();
        
        ItemStack ShopOther = new ItemStack(Material.REDSTONE);
        ItemMeta OtherMeta = ShopOther.getItemMeta();
        OtherMeta.setDisplayName(Translator.change("OTHER_STORE"));
        ShopOther.setItemMeta(OtherMeta);
        player.getInventory().setItem(2, ShopOther);
        player.updateInventory();
    }
    
    public static void givePrincipalLobbyObjects(Player player){

        if(!Main.getInstance().getConfig().getBoolean("EnableBungeeComunication")){
            return;
        }

        ItemStack exit = new ItemStack(Material.COMPASS);
        ItemMeta itemMetaExitArena = exit.getItemMeta();
        itemMetaExitArena.setDisplayName(Translator.change("RETURN_TO_LOBBY"));
        exit.setItemMeta(itemMetaExitArena);
        player.getInventory().setItem(8, exit);
        player.updateInventory();
    }
    
    public static void giveSpectatorObjects(Player player){
        ItemStack exit = new ItemStack(Material.APPLE);
        ItemMeta itemMetaExitArena = exit.getItemMeta();
        itemMetaExitArena.setDisplayName(Translator.change("LEAVE_SPECTATOR"));
        exit.setItemMeta(itemMetaExitArena);
        player.getInventory().setItem(8, exit);
        player.updateInventory();
    }
}
