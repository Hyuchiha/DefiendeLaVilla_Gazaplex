/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Listeners;

import com.hyuchiha.village_defense.Database.PlayerStatsData;
import com.hyuchiha.village_defense.Database.StatsManager;
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
 *
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
        
        if(vdplayer.getState() == PlayerState.SPECTATING){
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

            //Se actualiza la BD
            StatsManager.updateStatsFromPlayer(PlayerStatsData.getPlayerStat(player.getUniqueId(), player.getName()));

            vdplayer.getArena().getGame().getScoreboardManager().removeScoreboard(player.getName());

            vdplayer.getArena().getGame().playerLeaveGame(vdplayer);
        }

        PlayerManager.removePlayer(player);

        //Se eliminan los stats
        PlayerStatsData.removePlayerStat(player.getUniqueId());
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        final Player player = e.getPlayer();
        if (player == null) {
            return;
        }

        GamePlayer vdplayer = PlayerManager.getPlayer(player);
        
        if(vdplayer.getState() == PlayerState.SPECTATING){
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

            //Se actualiza la BD
            StatsManager.updateStatsFromPlayer(PlayerStatsData.getPlayerStat(player.getUniqueId(), player.getName()));

            vdplayer.getArena().getGame().getScoreboardManager().removeScoreboard(player.getName());

            vdplayer.getArena().getGame().playerLeaveGame(vdplayer);
        }

        PlayerManager.removePlayer(player);

        //Se eliminan los stats
        PlayerStatsData.removePlayerStat(player.getUniqueId());
    }
}
