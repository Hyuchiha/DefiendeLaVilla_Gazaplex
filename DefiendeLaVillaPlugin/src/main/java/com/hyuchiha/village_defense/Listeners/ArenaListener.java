package com.hyuchiha.village_defense.Listeners;

import com.hyuchiha.village_defense.Arena.Arena;
import com.hyuchiha.village_defense.Chat.ChatUtil;
import com.hyuchiha.village_defense.Database.Base.Account;
import com.hyuchiha.village_defense.Event.ArenaFinishEvent;
import com.hyuchiha.village_defense.Event.ArenaJoinEvent;
import com.hyuchiha.village_defense.Event.ArenaLeaveEvent;
import com.hyuchiha.village_defense.Event.ArenaStartEvent;
import com.hyuchiha.village_defense.Game.GamePlayer;
import com.hyuchiha.village_defense.Game.GameState;
import com.hyuchiha.village_defense.Game.Kit;
import com.hyuchiha.village_defense.Game.PlayerState;
import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Manager.ArenaManager;
import com.hyuchiha.village_defense.Manager.SpectatorManager;
import com.hyuchiha.village_defense.Messages.Translator;
import com.hyuchiha.village_defense.Output.Output;
import com.hyuchiha.village_defense.Timers.LobbyTimer;
import com.hyuchiha.village_defense.Timers.SavePlayersData;
import com.hyuchiha.village_defense.Utils.KitUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

/**
 * @author hyuchiha
 */
public class ArenaListener implements Listener {

  private final Main plugin;

  public ArenaListener(Main main) {
    this.plugin = main;
  }

  @EventHandler
  public void onArenaJoin(ArenaJoinEvent event) {
    String name = event.getArenaName();
    Arena arena = ArenaManager.getArenaConfiguration(name);
    GamePlayer playerEvent = event.getPlayer();

    if (arena == null) {
      playerEvent.getPlayer().sendMessage(Translator.getPrefix() + Translator.getColoredString("GAME.CANT_JOIN"));
      Output.logError("Se intento ingresa a la arena " + name + " pero retorno null");
      return;
    }

    //Se tiene permiso y se empieza la validacion de si
    //estas en otra partida
    if (playerEvent.getState() != PlayerState.LOBBY_GAME && playerEvent.getState() != PlayerState.INGAME) {
      if (arena.getGame().getPlayersInGame().size() == arena.getMaxNumberOfPlayers()) {
        //La arena esta llena y no te puedes unir
        //Se verifica si puedes ingresar como espectador

        if (arena.getGame().getState() == GameState.INGAME) {
          //Aqui se verifica que tenga permiso

          if (!playerEvent.getPlayer().hasPermission("VD.Player.spect")) {
            playerEvent.sendMessage(Translator.getPrefix() + Translator.getColoredString("ERROR.DONT_HAVE_PERMISSION_TO_SPECT"));
          } else {
            //Se añade como espectador
            arena.getGame().addSpectator(playerEvent);
          }
        } else {
          //No tienes permito espectar
          //Se envia mensaje para decirlo
          playerEvent.sendMessage(Translator.getPrefix() + Translator.getColoredString("ERROR.DONT_HAVE_PERMISSION_TO_SPECT"));
        }
      } else {
        //No estas en una arena y la arena no esta llena
        //Por lo que puedes unirte normalmente
        if (arena.getGame().getState() != GameState.INGAME) {
          arena.getGame().playerJoinGame(playerEvent);
        } else {
          arena.getGame().addSpectator(playerEvent);
        }
      }
    } else {
      //Se enviara mensaje para decirle que se encuentra en partida
      playerEvent.sendMessage(Translator.getPrefix() + Translator.getColoredString("GAME.PLAYER_IN_GAME"));
    }
  }

  @EventHandler
  public void onArenaStart(ArenaStartEvent event) {
    //Se pueden agregar mas cosas
    Arena arena = event.getArena();

    new LobbyTimer(plugin, arena.getGame());
  }

  @EventHandler
  public void onArenaFinish(ArenaFinishEvent event) {
    Arena arena = ArenaManager.getArenaConfiguration(event.getArena());

    List<GamePlayer> dataCloned = arena.getGame().getPlayersInGame();
    new SavePlayersData(dataCloned, plugin);

    for (GamePlayer player : arena.getGame().getPlayersInGame()) {
      player.sendMessage(Translator.getPrefix() + Translator.getColoredString("GAME.GAME_HAS_FINISHED"));

      if (SpectatorManager.isSpectator(player.getPlayer())) {
        SpectatorManager.removeSpectator(player.getPlayer());
      }

      if (player.getKit() == Kit.HUNTER) {
        KitUtils.removePlayerWolfs(player.getPlayer());
      }

      player.sendPlayerToLobby();

      //Se actualiza la BD
      Account data = plugin.getMainDatabase().getAccount(player.getPlayerUUID().toString(), player.getPlayer().getName());

      try {
        if (data.getMax_wave_reached() < arena.getGame().getWave().getWaveNumber()) {
          data.setMax_wave_reached(arena.getGame().getWave().getWaveNumber());
        }

        if (data.getMin_wave_reached() > arena.getGame().getWave().getWaveNumber()) {
          data.setMin_wave_reached(arena.getGame().getWave().getWaveNumber());
        }
      } catch (Exception e) {
        Output.logError("Error al poner las oleadas maximas y minimas " + e.getLocalizedMessage());
      }
    }

    arena.getGame().removeAllSpectators();

    //Se hara un broadcast del maximo de oleada
    String messageFinish = Translator.getColoredString("GAME.ARENA_MAX_WAVE_BROADCAST");
    messageFinish = messageFinish.replace("%TEAM%", arena.getName());
    messageFinish = messageFinish.replace("%WAVE_NUMBER%", Integer.toString(arena.getGame().getWave().getWaveNumber()));

    ChatUtil.broadcast(Translator.getPrefix() + messageFinish);
    // Se reinician los valores de la arena
    arena.createNewGame();

    for (Entity entity : Bukkit.getWorld(arena.getName()).getEntities()) {
      entity.remove();
    }
  }

  @EventHandler
  public void onArenaLeave(ArenaLeaveEvent event) {
    Arena arena = event.getArena();

    GamePlayer player = event.getPlayer();

    if (SpectatorManager.isSpectator(player.getPlayer())) {
      SpectatorManager.removeSpectator(player.getPlayer());
    }

    if (player.getState() == PlayerState.INGAME || player.getState() == PlayerState.LOBBY_GAME) {

      if (player.getKit() == Kit.HUNTER) {
        KitUtils.removePlayerWolfs(player.getPlayer());
      }

      arena.getGame().playerLeaveGame(player);
      arena.getGame().getScoreboardManager().removeScoreboard(player.getPlayer().getName());
    }

  }

}
