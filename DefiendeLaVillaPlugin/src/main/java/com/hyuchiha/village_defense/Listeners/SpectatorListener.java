/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Listeners;

import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Manager.SpectatorManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;

/**
 * @author hyuchiha
 */
public class SpectatorListener implements Listener {

  private final Main plugin;

  public SpectatorListener(Main plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBlockPlace(BlockPlaceEvent event) {
    Player player = event.getPlayer();
    if (SpectatorManager.isSpectator(player)) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBlockDamage(BlockDamageEvent event) {
    Player player = event.getPlayer();
    if (SpectatorManager.isSpectator(player)) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBlockBreak(BlockBreakEvent event) {
    Player player = event.getPlayer();
    if (SpectatorManager.isSpectator(player)) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerClickEvent(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    try {
      if (SpectatorManager.isSpectator(player) && player.isSneaking() && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_AIR)
          || SpectatorManager.isSpectator(player) && player.isSneaking() && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_AIR)) {
        event.setCancelled(true);
      } else if (SpectatorManager.isSpectator(player)) {
        event.setCancelled(true);
      }
    } catch (Exception e) {

    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onSignChange(PlayerPickupItemEvent event) {
    Player player = event.getPlayer();
    if (SpectatorManager.isSpectator(player)) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onEntityDamage(EntityDamageByEntityEvent event) {
    Player player = null;
    if (event.getDamager() instanceof Player) {
      player = (Player) event.getDamager();
    } else {
      return;
    }
    if (SpectatorManager.isSpectator(player)) {
      event.setCancelled(true);
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onFoodChange(FoodLevelChangeEvent event) {
    if (event.getEntity() instanceof Player) {
      final Player player = (Player) event.getEntity();
      if (SpectatorManager.isSpectator(player)) {
        event.setCancelled(true);
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onEntityDamage(EntityDamageEvent event) {
    Player player = null;
    if (event.getEntity() instanceof Player) {
      player = (Player) event.getEntity();
    } else {
      return;
    }
    if (SpectatorManager.isSpectator(player)) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onItemDrop(PlayerDropItemEvent event) {
    Player player = event.getPlayer();
    if (SpectatorManager.isSpectator(player)) {
      event.setCancelled(true);
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onEntityTarget(EntityTargetEvent event) {
    if ((event.getTarget() instanceof Player) && SpectatorManager.isSpectator((Player) event.getTarget())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onShear(PlayerShearEntityEvent event) {
    if (SpectatorManager.isSpectator(event.getPlayer())) {
      event.setCancelled(true);
    }
  }
}
