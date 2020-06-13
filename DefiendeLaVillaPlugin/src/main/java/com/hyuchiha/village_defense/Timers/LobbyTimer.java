/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Timers;

import com.hyuchiha.village_defense.Game.Game;
import com.hyuchiha.village_defense.Game.GamePlayer;
import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Messages.Translator;
import com.hyuchiha.village_defense.Scoreboard.ScoreboardType;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author hyuchiha
 */
public class LobbyTimer extends BukkitRunnable {

    private final Main plugin;
    private int timeLeft;
    private final Game game;

    public LobbyTimer(Main plugin, Game game) {
        this.plugin = plugin;
        this.game = game;

        timeLeft = plugin.getConfig().contains("Timers.lobby") ? plugin.getConfig().getInt("Timers.lobby") : 30;

        for (GamePlayer player : game.getPlayersInGame()) {
            Player inGamePlayer = player.getPlayer();
            if(inGamePlayer != null){
                inGamePlayer.playSound(
                        inGamePlayer.getPlayer().getLocation(),
                        Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
            }
        }

        this.runTaskTimer(plugin, 20L, 20L);
    }

    @Override
    public void run() {

        if (game.getPlayersInGame().size() < game.getArena().getMaxNumberOfPlayers()) {

            for (GamePlayer player : game.getPlayersInGame()) {
                player.sendMessage(Translator.getPrefix() + " " + Translator.getColoredString("GAME_CANCELED"));
                game.getScoreboardManager().updateScoreboard(ScoreboardType.LOBBY_GAME);
            }

            this.cancel();
        }

        if (timeLeft == 0) {
            cancel();

            new GameTimer(plugin, game);

            return;
        }

        if (timeLeft == 20 || timeLeft == 10 || timeLeft <= 5) {
            for (GamePlayer player : game.getPlayersInGame()) {
                player.sendMessage(
                        Translator.getPrefix() + " " +
                                Translator.getColoredString("GAME_STARTING")
                                        .replace("%TIME%",
                                                Integer.toString(timeLeft)) + ((timeLeft == 1) ? "." : "s."));
            }
        }

        timeLeft--;

    }

}
