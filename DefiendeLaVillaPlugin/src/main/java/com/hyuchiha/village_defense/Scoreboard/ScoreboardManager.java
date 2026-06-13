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
        line(board, SEP_TOP, score--);
        line(board, Translator.getColoredString("SCOREBOARD.LOBBY_PLAYERS")
            + game.getPlayersInGame().size(), score--);
        line(board, Translator.getColoredString("SCOREBOARD.LOBBY_REMAINING")
            + (game.getArena().getMaxNumberOfPlayers() - game.getPlayersInGame().size()), score--);
        line(board, BLANK, score--);
        line(board, Translator.getColoredString("SCOREBOARD.LOBBY_MAP")
            + game.getArena().getName(), score--);
        line(board, SEP_BOTTOM, score--);
        line(board, footer(), score--);
        break;

      case INGAME:
        line(board, SEP_TOP, score--);
        line(board, Translator.getColoredString("SCOREBOARD.INGAME_WAVE_NUMBER")
            + game.getWave().getWaveNumber(), score--);
        line(board, Translator.getColoredString("SCOREBOARD.INGAME_ENEMIES")
            + (game.getWave().getNumberOfEnemiesLeft() == -1 ? 0 : game.getWave().getNumberOfEnemiesLeft()), score--);
        line(board, Translator.getColoredString("SCOREBOARD.INGAME_PLAYERS_ALIVE")
            + game.getNumberOfAlivePlayers(), score--);
        line(board, Translator.getColoredString("SCOREBOARD.INGAME_VILLAGERS_REMAINING")
            + game.getWave().getNumberOfLiveVillagers(), score--);
        line(board, BLANK, score--);
        line(board, Translator.getColoredString("SCOREBOARD.INGAME_PLAYER_MONEY")
            + (int) PlayerManager.getMoney(p), score--);
        line(board, Translator.getColoredString("SCOREBOARD.INGAME_GEMS")
            + gp.getGems(), score--);
        line(board, SEP_BOTTOM, score--);
        line(board, footer(), score--);
        break;

      case SPECTATOR:
        line(board, SEP_TOP, score--);
        line(board, Translator.getColoredString("SCOREBOARD.INGAME_WAVE_NUMBER")
            + game.getWave().getWaveNumber(), score--);
        line(board, Translator.getColoredString("SCOREBOARD.INGAME_ENEMIES")
            + (game.getWave().getNumberOfEnemiesLeft() == -1 ? 0 : game.getWave().getNumberOfEnemiesLeft()), score--);
        line(board, Translator.getColoredString("SCOREBOARD.INGAME_PLAYERS_ALIVE")
            + game.getNumberOfAlivePlayers(), score--);
        line(board, Translator.getColoredString("SCOREBOARD.INGAME_VILLAGERS_REMAINING")
            + game.getWave().getNumberOfLiveVillagers(), score--);
        line(board, SEP_BOTTOM, score--);
        line(board, footer(), score--);
        break;
    }
  }

  /** Lineas decorativas fijas. Cada entry debe ser unico dentro del scoreboard. */
  private static final String SEP_TOP =
      ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "------------";
  private static final String SEP_BOTTOM =
      ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "------------";
  private static final String BLANK = ChatColor.RESET + " ";

  /** IP del servidor como pie de tabla; se resuelve en runtime (Translator ya cargado). */
  private static String footer() {
    return Translator.getColoredString("SERVER_IP");
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
