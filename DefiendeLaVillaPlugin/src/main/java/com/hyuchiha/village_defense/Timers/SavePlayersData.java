/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Timers;

import com.hyuchiha.village_defense.Database.PlayerStatsData;
import com.hyuchiha.village_defense.Database.StatsManager;
import com.hyuchiha.village_defense.Game.GamePlayer;
import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Output.Output;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kev'
 */
public class SavePlayersData extends BukkitRunnable {

    private final Main plugin;
    private final List<GamePlayer> playersToSave;

    public SavePlayersData(List<GamePlayer> players, Main plugin) {
        this.playersToSave = players;
        this.plugin = plugin;

        this.runTaskLater(plugin, 5 * 20L);
    }

    @Override
    public void run() {
        try {
            for (GamePlayer player : playersToSave) {
                PlayerStatsData data = PlayerStatsData.getPlayerStat(player.getPlayerUUID(), player.getPlayer().getName());

                StatsManager.updateStatsFromPlayer(data);
            }
        } catch (Exception e) {
            Output.logError(e.getLocalizedMessage());
            Output.logError(e.getMessage());
        }
        
        this.cancel();
    }

}
