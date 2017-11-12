/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Listeners;

import com.hyuchiha.village_defense.Arena.Arena;
import com.hyuchiha.village_defense.CustomEvents.ItemPurchaseEvent;
import com.hyuchiha.village_defense.Game.GamePlayer;
import com.hyuchiha.village_defense.Game.Kit;
import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Manager.PlayerManager;
import com.hyuchiha.village_defense.Utils.KitUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author hyuchiha
 */
public class KitListener implements Listener {

    private final Main plugin;

    public KitListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void KitDamageHelpers(EntityDamageByEntityEvent event) {
        Entity entityAttacker = event.getDamager();
        Entity entityAttacked = event.getEntity();
        if ((entityAttacker.getType() == EntityType.PLAYER) && (entityAttacked.getType() != EntityType.PLAYER)) {
            Player damager = (Player) entityAttacker;

            GamePlayer player = PlayerManager.getPlayer(damager);

            switch (player.getKit()) {
                case PYRO:
                    entityAttacked.setFireTicks(200);
                    break;
                case CONGELADOR:
                    ((LivingEntity) entityAttacked).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 2));
                    break;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void RightClickChecks(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        GamePlayer eventPlayer = PlayerManager.getPlayer(player);

        ItemStack stack = event.getItem();
        if (stack != null) {
            if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) || (event.getAction() == Action.RIGHT_CLICK_AIR)) {
                switch (eventPlayer.getKit()) {
                    case INVOCADOR:
                        if (stack.getType() == Material.MONSTER_EGG) {
                            if (Kit.isItem(stack, ChatColor.GOLD + "Aldeano")) {
                                //Se invoca a un aldeano
                                event.setCancelled(true);
                                eventPlayer.getArena().getGame().getWave().addNewVillagerCreatedByPlayer();
                                
                                //Se elimina el objeto en mano
                                int amount = player.getItemInHand().getAmount() - 1;
                                
                                if (amount <= 0) {
                                    player.getInventory().removeItem(player.getItemInHand());
                                } else {
                                    player.getItemInHand().setAmount(amount);
                                }
                            }
                        }
                        break;
                    case CAZADOR:
                        if (stack.getType() == Material.BONE) {
                            if (Kit.isItem(stack, ChatColor.GOLD + "Lobo")) {
                                //Se invoca a un lobo
                                event.setCancelled(true);
                                
                                KitUtils.createWolf(player);
                                
                                //Se elimina el objeto en mano
                                int amount = player.getItemInHand().getAmount() - 1;
                                if (amount <= 0) {
                                    player.getInventory().removeItem(player.getItemInHand());
                                } else {
                                    player.getItemInHand().setAmount(amount);
                                }
                            }
                        }
                    case THOR:
                        if (stack.getType() == Material.GOLD_AXE) {
                            if (Kit.isItem(stack, ChatColor.GOLD + "Hammer")) {
                                //Se invoca a la lluvia de rayos
                                event.setCancelled(true);
                                KitUtils.strikeDamage(player);
                            }
                        }
                        break;
                }
            }
        }
    }
    
    @EventHandler
    public void onItemPurchase(ItemPurchaseEvent event){
        Arena arena = event.getArena();
        
        GamePlayer playerWhoPurchased = event.getPlayerWhoBuy();

        //TODO check
        for(GamePlayer player: arena.getGame().getPlayersInGame()){
            if(player.getKit() == Kit.MERCADER){
                if(!player.getPlayer().getName().equals(playerWhoPurchased.getPlayer().getName())){
                    player.setGems((int) (player.getGems()*.3));
                }
            }
        }
    }
    
    @EventHandler
    public void onWolfDamage(EntityDamageByEntityEvent event) {
        Entity damage = event.getDamager();
        Entity wolf = event.getEntity();

        if (wolf instanceof Wolf && damage instanceof Player) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getCause() != DamageCause.PROJECTILE) {
            return;
        }
        Projectile proj = (Projectile) event.getDamager();
        if (!(proj.getShooter() instanceof Player)) {
            return;
        }

        Entity shot = event.getEntity();

        if (shot instanceof Wolf) {
            event.setCancelled(true);
        }
    }
}
