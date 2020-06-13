package com.hyuchiha.village_defense.Scoreboard;

import com.hyuchiha.village_defense.Game.Game;
import com.hyuchiha.village_defense.Game.GamePlayer;
import com.hyuchiha.village_defense.Manager.PlayerManager;
import com.hyuchiha.village_defense.Messages.Translator;
import com.hyuchiha.village_defense.Output.Output;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;

/**
 * @author hyuchiha
 */
public class ScoreboardManager {

  private final HashMap<String, ScoreboardType> players = new HashMap<>();

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
      Game game = gp.getArena().getGame();

      obj.setDisplayName(ChatColor.BOLD + "" + Translator.getColoredString("SCOREBOARD.TITLE"));

      switch (st) {
        case LOBBY_GAME:

          obj.getScore(ChatColor.AQUA + "").setScore(score--);
          obj.getScore(Translator.getColoredString("SCOREBOARD.LOBBY_PLAYERS")).setScore(score--);
          obj.getScore(ChatColor.WHITE + "" + game.getPlayersInGame().size()).setScore(score--);
          obj.getScore(ChatColor.BLUE + "").setScore(score--);
          obj.getScore(Translator.getColoredString("SCOREBOARD.LOBBY_REMAINING")).setScore(score--);
          obj.getScore(ChatColor.WHITE + "" + (game.getArena().getMaxNumberOfPlayers() - game.getPlayersInGame().size())).setScore(score--);
          obj.getScore(ChatColor.BOLD + "").setScore(score--);
          obj.getScore(Translator.getColoredString("SCOREBOARD.LOBBY_MAP")).setScore(score--);
          obj.getScore(ChatColor.WHITE + game.getArena().getName()).setScore(score--);

          break;

        case INGAME:

          obj.getScore(Translator.getColoredString("SCOREBOARD.INGAME_PLAYER_MONEY")).setScore((int) PlayerManager.getMoney(p));
          obj.getScore(Translator.getColoredString("SCOREBOARD.INGAME_GEMS")).setScore(gp.getGems());
          obj.getScore(Translator.getColoredString("SCOREBOARD.INGAME_WAVE_NUMBER")).setScore(game.getWave().getWaveNumber());
          obj.getScore(Translator.getColoredString("SCOREBOARD.INGAME_ENEMIES")).setScore(
              game.getWave().getNumberOfEnemiesLeft() == -1 ? 0 : game.getWave().getNumberOfEnemiesLeft());
          obj.getScore(Translator.getColoredString("SCOREBOARD.INGAME_PLAYERS_ALIVE")).setScore(game.getNumberOfAlivePlayers());
          obj.getScore(Translator.getColoredString("SCOREBOARD.INGAME_VILLAGERS_REMAINING")).setScore(game.getWave().getNumberOfLiveVillagers());

          break;

        case SPECTATOR:

          obj.getScore(Translator.getColoredString("SCOREBOARD.INGAME_WAVE_NUMBER")).setScore(game.getWave().getWaveNumber());
          obj.getScore(Translator.getColoredString("SCOREBOARD.INGAME_ENEMIES")).setScore(game.getWave().getNumberOfEnemiesLeft() == -1 ? 0 : game.getWave().getNumberOfEnemiesLeft());
          obj.getScore(Translator.getColoredString("SCOREBOARD.INGAME_PLAYERS_ALIVE")).setScore(game.getNumberOfAlivePlayers());
          obj.getScore(Translator.getColoredString("SCOREBOARD.INGAME_VILLAGERS_REMAINING")).setScore(game.getWave().getNumberOfLiveVillagers());

          break;

      }

      obj.setDisplaySlot(DisplaySlot.SIDEBAR);
      p.setScoreboard(board);

      players.put(player, st);
    } catch (Exception e) {
      e.printStackTrace();
      Output.logError("Problema al asignar la scoreboard a " + player + " " + e.getLocalizedMessage());
      players.remove(player);
    }
  }

  public void removeScoreboard(String player) {
    players.remove(player);
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
    if (players.containsKey(player)) {
      this.giveScoreboard(player, sts);
    }
  }
}
