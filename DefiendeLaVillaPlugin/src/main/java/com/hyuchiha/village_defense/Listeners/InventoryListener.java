/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Listeners;

import com.hyuchiha.village_defense.Game.GamePlayer;
import com.hyuchiha.village_defense.Game.Kit;
import com.hyuchiha.village_defense.Game.PlayerState;
import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Manager.PlayerManager;
import com.hyuchiha.village_defense.Messages.Translator;
import com.hyuchiha.village_defense.Output.Output;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

/**
 *
 * @author hyuchiha
 */
public class InventoryListener implements Listener{
    
    private final Main plugin;
    
    public InventoryListener(Main plugin){
        this.plugin= plugin;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Inventory inv = e.getInventory();
        Player player = (Player) e.getWhoClicked();
        try {
            if (inv.getTitle().startsWith(Translator.change("CLASS_SELECT_INV_TITLE"))) {
                
                if (e.getCurrentItem().getType() == Material.AIR || e.getCurrentItem().getType() == null) {
                    return;
                }
                player.closeInventory();
                e.setCancelled(true);
                String name = e.getCurrentItem().getItemMeta().getDisplayName();
                GamePlayer meta = PlayerManager.getPlayer(player);

                if(meta.getKit() != Kit.CIVIL && meta.getState() == PlayerState.LOBBY_GAME){
                    player.sendMessage(Translator.change("PLAYER_HAS_CLASS_SELECTED"));
                    return;
                }

                Kit toChoose = Kit.valueOf(ChatColor.stripColor(name).toUpperCase());
                
                if (!toChoose.isOwnedBy(player)) {
                    player.sendMessage(Translator.change("PLAYER_DONT_HAS_CLASS_UNLOCKED"));
                    return;
                }
                
                meta.setKit(Kit.getKit(ChatColor.stripColor(name)));
                
                String classSelected = Translator.change("PLAYER_HAS_SELECTED_CLASS");
                classSelected = classSelected.replace("%CLASS%", ChatColor.stripColor(name));
                player.sendMessage(plugin.getPrefix() +" "+ classSelected);

                return;
            }
            
            if (inv.getTitle().startsWith(Translator.change("UNLOCK_INV_TITLE"))) {
                try {
                    if (e.getCurrentItem().getType() == Material.AIR || e.getCurrentItem().getType() == null) {
                        return;
                    }

                    String name = e.getCurrentItem().getItemMeta().getDisplayName();

                    if (!Kit.valueOf(ChatColor.stripColor(name).toUpperCase())
                            .isOwnedBy(player)) {
                        
                        double money = Main.getInstance().
                                getConfig("kits.yml").
                                getInt("Class_Point." + name.toUpperCase());
                        double userMoney = PlayerManager.getMoney(player);

                        if (userMoney >= money) {
                            PlayerManager.withdrawMoney(player, money);
                            //TODO add unlocker
                            //KitsUnlockedManager.addUnlockedKit(player.getName(), name);

                            String classUnlocked = Translator.change("PLAYER_UNLOCK_CLASS");
                            player.sendMessage(classUnlocked.replace("%CLASS%", name));
                        } else {
                            player.sendMessage(Translator.change("PLAYER_DONT_HAVE_REQUIRED_MONEY"));
                        }

                        player.closeInventory();
                        e.setCancelled(true);
                    } else {
                        player.closeInventory();
                        e.setCancelled(true);
                        player.sendMessage(Translator.change("PLAYER_ALREADY_HAVE_CLASS"));
                    }
                } catch (Exception ex) {
                    Output.logError("Error desbloqueando un kit");
                    Output.logError(ex.getLocalizedMessage());
                }

            }
        }catch(Exception ex){
            Output.logError("Error en los inventarios");
            Output.logError(ex.getLocalizedMessage());
        }
    }
}
