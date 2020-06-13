package com.hyuchiha.village_defense.Game;

import com.hyuchiha.village_defense.Arena.Arena;
import com.hyuchiha.village_defense.Chat.ChatUtil;
import com.hyuchiha.village_defense.Event.ArenaStartEvent;
import com.hyuchiha.village_defense.Manager.ArenaManager;
import com.hyuchiha.village_defense.Manager.SpectatorManager;
import com.hyuchiha.village_defense.Messages.Translator;
import com.hyuchiha.village_defense.Scoreboard.ScoreboardManager;
import com.hyuchiha.village_defense.Scoreboard.ScoreboardType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Game {

  private GameState state;
  private String arena;
  private final List<GamePlayer> gamePlayers;
  private final List<GamePlayer> spectators;
  private Wave wave;

  private final ScoreboardManager scoreboardManager;

  public Game(String arena) {
    this.arena = arena;
    this.state = GameState.WAITING;
    this.gamePlayers = new ArrayList<>();
    this.spectators = new ArrayList<>();

    this.scoreboardManager = new ScoreboardManager();
  }

  public void setGameState(GameState state) {
    this.state = state;
    getArena().updateSign();
  }

  public GameState getState() {
    return this.state;
  }

  public List<GamePlayer> getPlayersInGame() {
    removePlayersNotOnline();
    return gamePlayers;
  }

  private void removePlayersNotOnline() {
    Iterator<GamePlayer> players = gamePlayers.iterator();
    while (players.hasNext()) {

      Player player = players.next().getPlayer();
      if (player == null) {
        players.remove();
      }
    }
  }

  public List<GamePlayer> getSpectators() {
    removePlayersNotOnline();
    return spectators;
  }

  public void playerJoinGame(GamePlayer playerjoined) {
    gamePlayers.add(playerjoined);

    playerjoined.setState(PlayerState.LOBBY_GAME);
    playerjoined.setArena(arena);

    sendPlayerToArenaLobby(playerjoined);

    for (GamePlayer playervd : getPlayersInGame()) {
      Player player = playervd.getPlayer();
      if (player != null) {
        getScoreboardManager().giveScoreboard(player.getName(), ScoreboardType.LOBBY_GAME);
      }
    }
  }

  public void playerLeaveGame(GamePlayer playerleave) {

    gamePlayers.remove(playerleave);
    //Se actualizara el scoreboard
    playerleave.getPlayer().getScoreboard().clearSlot(DisplaySlot.SIDEBAR);

    //Se envia mensaje de que alguien salio
    for (GamePlayer vdplayer : getPlayersInGame()) {
      vdplayer.sendMessage(Translator.getPrefix() +
          Translator.getColoredString("GAME.PLAYER_LEAVE_GAME")
              .replace("%PLAYER%", playerleave.getPlayer().getName())
      );
    }

    getArena().updateSign();

    //Se envia el jugador al lobby
    playerleave.clearData();
    playerleave.sendPlayerToLobby();

    if (state == GameState.WAITING) {
      //Se le avisa al jugador que dejo esta partida
      playerleave.sendMessage(Translator.getPrefix() +
          Translator.getColoredString("GAME.PLAYER_LEAVE_ARENA")
              .replace("%ARENA%", getArena().getName())
      );
    }
  }

  private void sendPlayerToArenaLobby(GamePlayer vdplayer) {
    //Se debe poner un estado para jugadores
    //asi cuando esten en lobby evitar perdida de comida
    vdplayer.sendPlayerToLobbyArena();

    //Y se actualiza el sign para que todos vean que esta perfecto
    ArenaManager.getArenaConfiguration(arena).updateSign();
    //Se debe verificar si se inicia la partida
    if (canInitiateGame()) {
      //Se inicia el juego lanzando un nuevo evento
      Bukkit.getServer()
          .getPluginManager().callEvent(
          new ArenaStartEvent(ArenaManager.getArenaConfiguration(arena)));
    } else {
      //Se enviara mensaje a todos los jugadores acerca de cuantos
      //jugadores faltan

    }
  }

  public void addSpectator(GamePlayer playervd) {
    playervd.setArena(arena);
    playervd.setPlayerSpectator();
    spectators.add(playervd);
    getScoreboardManager().giveScoreboard(playervd.getPlayer().getName(), ScoreboardType.SPECTATOR);
  }

  public void removeAllSpectators() {
    for (GamePlayer spectator : getSpectators()) {
      Player playervd = spectator.getPlayer();

      if (playervd != null) {
        SpectatorManager.removeSpectator(playervd);
        getScoreboardManager().removeScoreboard(playervd.getName());
        spectator.sendMessage(Translator.getPrefix() + Translator.getColoredString("GAME.GAME_HAS_FINISHED"));
        spectator.sendPlayerToLobby();
        spectator.setArena(null);
      }
    }

    this.spectators.clear();
  }

  public void removeSpectator(GamePlayer spectator) {
    Player player = spectator.getPlayer();

    spectator.sendPlayerToLobby();
    spectator.setArena(null);
    spectators.remove(spectator);

    if (player != null) {
      SpectatorManager.removeSpectator(player);
      getScoreboardManager().removeScoreboard(player.getName());
    }
  }

  public boolean allPlayersDeath() {
    return getNumberOfDeathPlayers() == gamePlayers.size();
  }

  public int getNumberOfDeathPlayers() {
    int count = 0;
    for (GamePlayer player : getPlayersInGame()) {
      if (player.isKilled()) {
        count++;
      }
    }
    return count;
  }

  public int getNumberOfAlivePlayers() {
    int count = 0;
    for (GamePlayer player : getPlayersInGame()) {
      if (!player.isKilled()) {
        count++;
      }
    }
    return count;
  }

  public void sendPlayersToGame() {
    for (GamePlayer player : getPlayersInGame()) {
      player.sendPlayerToArena();
    }
  }

  public boolean canInitiateGame() {
    Arena arena = getArena();
    return arena != null && arena.getMaxNumberOfPlayers() == gamePlayers.size();
  }

  public void sendMessageToPlayers(String message) {
    for (GamePlayer gamePlayer : getPlayersInGame()) {
      gamePlayer.sendMessage(message);
    }
  }

  public void playerDeathInfo(GamePlayer player) {
    String deathMessage = ChatUtil.formatDeathMessage(player.getPlayer());

    for (GamePlayer playerInGame : getPlayersInGame()) {
      playerInGame.sendMessage(Translator.getPrefix() + deathMessage);
    }

    for (GamePlayer gameSpectator : getSpectators()) {
      gameSpectator.sendMessage(Translator.getPrefix() + deathMessage);
    }
  }

  public Arena getArena() {
    return ArenaManager.getArenaConfiguration(arena);
  }

  public void removeWaveData() {
    this.wave.endWave();
    this.wave = null;
    this.arena = null;
  }

  public Wave getWave() {
    return wave;
  }

  public void setWave(Wave wave) {
    this.wave = wave;
  }

  public ScoreboardManager getScoreboardManager() {
    return scoreboardManager;
  }
}
