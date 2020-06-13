/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Listeners;

import com.hyuchiha.village_defense.Database.Base.Account;
import com.hyuchiha.village_defense.Database.Base.Database;
import com.hyuchiha.village_defense.Game.GamePlayer;
import com.hyuchiha.village_defense.Game.Kit;
import com.hyuchiha.village_defense.Game.PlayerState;
import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Manager.PlayerManager;
import com.hyuchiha.village_defense.Manager.SpectatorManager;
import com.hyuchiha.village_defense.Utils.KitUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author hyuchiha
 */
public class QuitListener implements Listener {

  private Main plugin;

  public QuitListener(Main main) {
    this.plugin = main;
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent e) {
    final Player player = e.getPlayer();
    if (player == null) {
      return;
    }
    GamePlayer vdplayer = PlayerManager.getPlayer(player);

    if (vdplayer.getState() == PlayerState.SPECTATING) {
      vdplayer.getArena().getGame().removeSpectator(vdplayer);
    }

    e.setQuitMessage("");

    if (SpectatorManager.isSpectator(player)) {
      SpectatorManager.removeSpectator(player);
    }

    if (vdplayer.getState() == PlayerState.INGAME || vdplayer.getState() == PlayerState.LOBBY_GAME) {

      if (vdplayer.getKit() == Kit.HUNTER) {
        KitUtils.removePlayerWolfs(player.getPlayer());
      }

      vdplayer.getArena().getGame().getScoreboardManager().removeScoreboard(player.getName());

      vdplayer.getArena().getGame().playerLeaveGame(vdplayer);
    }

    //Se actualiza la BD
    Database database = plugin.getDatabase();
    Account account = database.getAccount(player.getUniqueId().toString(), player.getName());

    if (account != null) {
      database.saveAccount(account);
      database.removeCachedAccount(account);
    }

    PlayerManager.removePlayer(player);
  }

  @EventHandler
  public void onKick(PlayerKickEvent e) {
    final Player player = e.getPlayer();
    if (player == null) {
      return;
    }

    GamePlayer vdplayer = PlayerManager.getPlayer(player);

    if (vdplayer.getState() == PlayerState.SPECTATING) {
      vdplayer.getArena().getGame().removeSpectator(vdplayer);
    }
    e.setLeaveMessage("");

    if (SpectatorManager.isSpectator(player)) {
      SpectatorManager.removeSpectator(player);
    }

    if (vdplayer.getState() == PlayerState.INGAME || vdplayer.getState() == PlayerState.LOBBY_GAME) {

      if (vdplayer.getKit() == Kit.HUNTER) {
        KitUtils.removePlayerWolfs(player.getPlayer());
      }

      vdplayer.clearData();

      vdplayer.getArena().getGame().getScoreboardManager().removeScoreboard(player.getName());

      vdplayer.getArena().getGame().playerLeaveGame(vdplayer);
    }

    //Se actualiza la BD
    Database database = plugin.getDatabase();
    Account account = database.getAccount(player.getUniqueId().toString(), player.getName());

    if (account != null) {
      database.saveAccount(account);
      database.removeCachedAccount(account);
    }

    PlayerManager.removePlayer(player);
  }
}
