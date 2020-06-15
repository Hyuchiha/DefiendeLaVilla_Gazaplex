/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Listeners;

import com.hyuchiha.village_defense.Event.ArenaJoinEvent;
import com.hyuchiha.village_defense.Game.GamePlayer;
import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Manager.PlayerManager;
import com.hyuchiha.village_defense.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * @author hyuchiha
 */
public class SignListener implements Listener {

  private final Main plugin;

  public SignListener(Main main) {
    this.plugin = main;
  }

  @EventHandler
  public void onSingClick(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    GamePlayer gamePlayer = PlayerManager.getPlayer(player);

    Action a = event.getAction();
    if (a == Action.RIGHT_CLICK_BLOCK) {
      if (event.getClickedBlock() != null) {
        Material clickedType = event.getClickedBlock().getType();
        if (Utils.isWallSign(clickedType)) {
          Sign s = (Sign) event.getClickedBlock().getState();

          if (s.getLine(0).contains(ChatColor.DARK_PURPLE + "Arena")) {
            String arenaName = ChatColor.stripColor(s.getLine(1));

            Bukkit.getServer().getPluginManager().
                callEvent(new ArenaJoinEvent(arenaName, gamePlayer));
          }
        }
      }
    }
  }

  @EventHandler
  public void onSignBreak(BlockBreakEvent event) {
    Material clickedType = event.getBlock().getType();
    if (Utils.isWallSign(clickedType)) {
      Sign s = (Sign) event.getBlock().getState();

      if (s.getLine(0).contains(ChatColor.DARK_PURPLE + "Arena")) {
        event.setCancelled(true);
      }
    }
  }

}
