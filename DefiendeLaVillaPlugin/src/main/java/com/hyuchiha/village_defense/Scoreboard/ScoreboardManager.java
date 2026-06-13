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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hyuchiha
 */
public class ScoreboardManager {

  /** Scoreboard cacheado por jugador: se crea una sola vez y se reusa entre updates. */
  private static final class PlayerBoard {
    final Scoreboard scoreboard;
    final Objective objective;
    ScoreboardType type;
    final List<String> entries = new ArrayList<>();

    PlayerBoard(Scoreboard scoreboard, Objective objective, ScoreboardType type) {
      this.scoreboard = scoreboard;
      this.objective = objective;
      this.type = type;
    }
  }

  private final Map<String, PlayerBoard> boards = new HashMap<>();

  public void giveScoreboard(String playerName, ScoreboardType st) {
    try {
      Player p = Bukkit.getPlayer(playerName);

      if (p == null) {
        boards.remove(playerName);
        return;
      }

      PlayerBoard board = boards.get(playerName);

      // Se crea el Scoreboard/Objective UNA sola vez por jugador (o cuando cambia de
      // tipo). Antes se hacia getNewScoreboard()+registerNewObjective()+setScoreboard()
      // en CADA update, disparado por cada spawn/muerte de mob -> cientos por oleada.
      if (board == null || board.type != st) {
        Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = sb.registerNewObjective("vd", "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName(ChatColor.BOLD + "" + Translator.getColoredString("SCOREBOARD.TITLE"));

        board = new PlayerBoard(sb, obj, st);
        boards.put(playerName, board);
        p.setScoreboard(sb);
      }

      populate(board, p, st);
    } catch (Exception e) {
      e.printStackTrace();
      Output.logError("Problema al asignar la scoreboard a " + playerName + " " + e.getLocalizedMessage());
      boards.remove(playerName);
    }
  }

  /**
   * Reescribe las lineas sobre el MISMO scoreboard (sin recrearlo): se resetean las
   * entradas previas y se vuelven a poner. El cliente recibe solo paquetes incrementales.
   */
  private void populate(PlayerBoard board, Player p, ScoreboardType st) {
    for (String entry : board.entries) {
      board.scoreboard.resetScores(entry);
    }
    board.entries.clear();

    GamePlayer gp = PlayerManager.getPlayer(p);
    Game game = gp.getArena().getGame();
    int score = 15;

    switch (st) {
      case LOBBY_GAME:
        line(board, ChatColor.AQUA + "", score--);
        line(board, Translator.getColoredString("SCOREBOARD.LOBBY_PLAYERS"), score--);
        line(board, ChatColor.WHITE + "" + game.getPlayersInGame().size(), score--);
        line(board, ChatColor.BLUE + "", score--);
        line(board, Translator.getColoredString("SCOREBOARD.LOBBY_REMAINING"), score--);
        line(board, ChatColor.WHITE + "" + (game.getArena().getMaxNumberOfPlayers() - game.getPlayersInGame().size()), score--);
        line(board, ChatColor.BOLD + "", score--);
        line(board, Translator.getColoredString("SCOREBOARD.LOBBY_MAP"), score--);
        line(board, ChatColor.WHITE + game.getArena().getName(), score--);
        break;

      case INGAME:
        line(board, Translator.getColoredString("SCOREBOARD.INGAME_PLAYER_MONEY"), (int) PlayerManager.getMoney(p));
        line(board, Translator.getColoredString("SCOREBOARD.INGAME_GEMS"), gp.getGems());
        line(board, Translator.getColoredString("SCOREBOARD.INGAME_WAVE_NUMBER"), game.getWave().getWaveNumber());
        line(board, Translator.getColoredString("SCOREBOARD.INGAME_ENEMIES"),
            game.getWave().getNumberOfEnemiesLeft() == -1 ? 0 : game.getWave().getNumberOfEnemiesLeft());
        line(board, Translator.getColoredString("SCOREBOARD.INGAME_PLAYERS_ALIVE"), game.getNumberOfAlivePlayers());
        line(board, Translator.getColoredString("SCOREBOARD.INGAME_VILLAGERS_REMAINING"), game.getWave().getNumberOfLiveVillagers());
        break;

      case SPECTATOR:
        line(board, Translator.getColoredString("SCOREBOARD.INGAME_WAVE_NUMBER"), game.getWave().getWaveNumber());
        line(board, Translator.getColoredString("SCOREBOARD.INGAME_ENEMIES"),
            game.getWave().getNumberOfEnemiesLeft() == -1 ? 0 : game.getWave().getNumberOfEnemiesLeft());
        line(board, Translator.getColoredString("SCOREBOARD.INGAME_PLAYERS_ALIVE"), game.getNumberOfAlivePlayers());
        line(board, Translator.getColoredString("SCOREBOARD.INGAME_VILLAGERS_REMAINING"), game.getWave().getNumberOfLiveVillagers());
        break;
    }
  }

  private void line(PlayerBoard board, String entry, int value) {
    board.objective.getScore(entry).setScore(value);
    board.entries.add(entry);
  }

  public void removeScoreboard(String player) {
    boards.remove(player);
  }

  public void updateScoreboard(ScoreboardType... sts) {
    for (ScoreboardType st : sts) {
      // Copia de las keys: giveScoreboard puede remover una entrada (jugador offline)
      // y mutar el mapa durante la iteracion.
      for (String playerName : new ArrayList<>(boards.keySet())) {
        PlayerBoard board = boards.get(playerName);
        if (board != null && board.type == st) {
          giveScoreboard(playerName, st);
        }
      }
    }
  }

  public void updateScoreboard(ScoreboardType st, String player) {
    if (boards.containsKey(player)) {
      giveScoreboard(player, st);
    }
  }
}
