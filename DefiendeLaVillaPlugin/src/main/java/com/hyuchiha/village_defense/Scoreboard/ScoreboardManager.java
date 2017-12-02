/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Scoreboard;

import com.hyuchiha.village_defense.Manager.PlayerManager;
import com.hyuchiha.village_defense.Game.Game;
import com.hyuchiha.village_defense.Game.GamePlayer;
import com.hyuchiha.village_defense.Output.Output;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;

/**
 *
 * @author hyuchiha
 */
public class ScoreboardManager {

    private final HashMap<String, ScoreboardType> players = new HashMap<>();

    public ScoreboardManager() {

    }

    public void giveScoreboard(String player, ScoreboardType st) {
        try {

            Player p = Bukkit.getPlayer(player);

            if (p == null) {
                players.remove(player);
                return;
            }

            Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
            Objective obj = board.registerNewObjective(p.getName(), "dummy");

            int score = 15;
            GamePlayer gp = PlayerManager.getPlayer(p);

            obj.setDisplayName(ChatColor.DARK_GREEN +""+ ChatColor.BOLD + "Defiende la Villa ");

            switch (st) {
                case LOBBY_GAME:
                    Game game = gp.getArena().getGame();

                    obj.getScore(ChatColor.AQUA + "").setScore(score--);
                    obj.getScore(ChatColor.AQUA + "Jugadores:").setScore(score--);
                    obj.getScore(ChatColor.WHITE + "" + game.getPlayersInGame().size()).setScore(score--);
                    obj.getScore(ChatColor.BLUE + "").setScore(score--);
                    obj.getScore(ChatColor.GREEN + "Restantes:").setScore(score--);
                    obj.getScore(ChatColor.WHITE + "" + (game.getArena().getMaxNumberOfPlayers() - game.getPlayersInGame().size())).setScore(score--);
                    obj.getScore(ChatColor.BOLD + "").setScore(score--);
                    obj.getScore(ChatColor.RED + "Mapa:").setScore(score--);
                    obj.getScore(ChatColor.WHITE + game.getArena().getName()).setScore(score--);

                    break;

                case INGAME:
                    Game game2 = gp.getArena().getGame();

                    obj.getScore(ChatColor.AQUA + "Fe:").setScore((int) PlayerManager.getMoney(p));
                    obj.getScore(ChatColor.LIGHT_PURPLE + "Gemas:").setScore(gp.getGems());
                    obj.getScore(ChatColor.GREEN + "Oleada:").setScore(game2.getWave().getWaveNumber());
                    obj.getScore(ChatColor.YELLOW + "Enemigos:").setScore(
                            game2.getWave().getNumberOfEnemiesLeft()== -1 ? 0 : game2.getWave().getNumberOfEnemiesLeft());
                    obj.getScore(ChatColor.DARK_GREEN + "Jugadores vivos:").setScore(game2.getNumberOfAlivePlayers());
                    obj.getScore(ChatColor.RED + "Aldeanos:").setScore(game2.getWave().getNumberOfLiveVillagers());
                    break;
                case SPECTATOR:
                    Game game3 = gp.getArena().getGame();

                    obj.getScore(ChatColor.GREEN + "Oleada:").setScore(game3.getWave().getWaveNumber());
                    obj.getScore(ChatColor.YELLOW + "Enemigos:").setScore(game3.getWave().getNumberOfEnemiesLeft()== -1 ? 0 : game3.getWave().getNumberOfEnemiesLeft());
                    obj.getScore(ChatColor.DARK_GREEN + "Jugadores vivos:").setScore(game3.getNumberOfAlivePlayers());
                    obj.getScore(ChatColor.RED + "Aldeanos:").setScore(game3.getWave().getNumberOfLiveVillagers());
                    break;

            }

            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
            p.setScoreboard(board);

            players.put(player, st);
        } catch (Exception e) {
            Output.logError("Problema al asignar la scoreboard a " + player + " " + e.getLocalizedMessage());
            players.remove(player);
        }
    }

    public void removeScoreboard(String player) {
        try {
            players.remove(player);
        } catch (Exception e) {
            Output.logError(e.getMessage());
            Output.logError(e.getLocalizedMessage());
        }

    }

    public void updateScoreboard(ScoreboardType... sts) {
        for (ScoreboardType st : sts) {
            for (String p : players.keySet()) {
                Player player = Bukkit.getPlayer(p);
                if (player != null) {
                    if (players.get(p) == st) {
                        this.giveScoreboard(p, st);
                    }
                }
            }
        }
    }

    public void updateScoreboard(ScoreboardType sts, String player) {
        try {
            if (players.containsKey(player)) {
                this.giveScoreboard(player, sts);
            }
        } catch (Exception e) {
            Output.logError(e.getMessage());
            Output.logError(e.getLocalizedMessage());
        }
    }
}
