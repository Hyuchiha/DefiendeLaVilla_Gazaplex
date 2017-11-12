package com.hyuchiha.village_defense.Listeners;

import com.hyuchiha.village_defense.Arena.Arena;
import com.hyuchiha.village_defense.Chat.ChatUtil;
import com.hyuchiha.village_defense.CustomEvents.ArenaFinishEvent;
import com.hyuchiha.village_defense.CustomEvents.ArenaJoinEvent;
import com.hyuchiha.village_defense.CustomEvents.ArenaLeaveEvent;
import com.hyuchiha.village_defense.CustomEvents.ArenaStartEvent;
import com.hyuchiha.village_defense.Game.GamePlayer;
import com.hyuchiha.village_defense.Game.Kit;
import com.hyuchiha.village_defense.Game.PlayerState;
import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Manager.ArenaManager;
import com.hyuchiha.village_defense.Messages.Translator;
import com.hyuchiha.village_defense.Game.GameState;
import com.hyuchiha.village_defense.Output.Output;
import com.hyuchiha.village_defense.Manager.SpectatorManager;
import com.hyuchiha.village_defense.Timers.LobbyTimer;
import com.hyuchiha.village_defense.Utils.KitUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;

/**
 *
 * @author hyuchiha
 */
public class ArenaListener implements Listener {

    private Main plugin;

    public ArenaListener(Main main) {
        this.plugin = main;
    }

    @EventHandler
    public void onArenaJoin(ArenaJoinEvent event) {
        String name = event.getArenaName();
        Arena arena = ArenaManager.getArenaConfiguration(name);
        GamePlayer playerEvent = event.getPlayer();

        if (arena == null) {
            playerEvent.getPlayer().sendMessage(Translator.change("CANT_JOIN_ARENA"));
            Output.logError("Se intento ingresa a la arena " + name + " pero retorno null");
            return;
        }

        /**
        //Verificarse que tenga permiso para jugar
        if (!playerEvent.getPlayer().hasPermission("VD.Player.join")) {
            playerEvent.getPlayer().sendMessage(Translator.change("DONT_HAVE_PERMISSION_TO_JOIN"));
            return;
        }*/

        //Se tiene permiso y se empieza la validacion de si
        //estas en otra partida
        if (playerEvent.getState() != PlayerState.LOBBY_GAME && playerEvent.getState() != PlayerState.INGAME) {
            if (arena.getGame().getPlayersInGame().size() == arena.getMaxNumberOfPlayers()) {
                //La arena esta llena y no te puedes unir
                //Se verifica si puedes ingresar como espectador

                if (arena.getGame().getState() == GameState.INGAME) {
                    //Aqui se verifica que tenga permiso

                    if (!playerEvent.getPlayer().hasPermission("VD.Player.spect")) {
                        playerEvent.sendMessage(Translator.change("DONT_HAVE_PERMISSION_TO_SPECT"));
                        return;
                    } else {
                        //Se añade como espectador
                        arena.getGame().addSpectator(playerEvent);
                    }
                } else {
                    //No tienes permito espectar
                    //Se envia mensaje para decirlo
                    playerEvent.sendMessage(Translator.change("DONT_HAVE_PERMISSION_TO_SPECT"));
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
            playerEvent.sendMessage(Translator.change("YOU_ARE_IN_GAME"));
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
        
        for (GamePlayer player : arena.getGame().getPlayersInGame()) {
            player.sendMessage(Translator.change("GAME_HAS_FINISHED"));
            
            if (SpectatorManager.isSpectator(player.getPlayer())) {
                SpectatorManager.removeSpectator(player.getPlayer());
            }

            if (player.getKit() == Kit.CAZADOR) {
                KitUtils.removePlayerWolfs(player.getPlayer());
            }

            player.sendPlayerToLobby();

            /*
            //Se actualiza la BD
            PlayerStatsData data = PlayerStatsData.getPlayerStat(playervd.getName());

            try {
                if (data.getMax_wave_reached() < arena.getGame().getWave().getWaveNumber()) {
                    data.setMax_wave_reached(arena.getGame().getWave().getWaveNumber());
                }

                if (data.getMin_wave_reached() > arena.getGame().getWave().getWaveNumber()) {
                    data.setMin_wave_reached(arena.getGame().getWave().getWaveNumber());
                }
            } catch (Exception e) {
                Output.logError("Error al poner las oleadas maximas y minimas "+e.getLocalizedMessage());
            }*/
            //StatsManager.setMaxWave(player.getPlayer(), arena.getGame().getWave().getWaveNumber());
            //StatsManager.setMinWave(player.getPlayer(), arena.getGame().getWave().getWaveNumber());
        }

        arena.getGame().removeAllSpectators();

        //Se hara un broadcast del maximo de oleada
        String messageFinish = Translator.change("ARENA_MAX_WAVE_BROADCAST");
        messageFinish = messageFinish.replace("%TEAM%", arena.getName());
        messageFinish = messageFinish.replace("%WAVE_NUMBER%", Integer.toString(arena.getGame().getWave().getWaveNumber()));

        ChatUtil.broadcast(plugin.getPrefix() + " " + messageFinish);
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

            if (player.getKit() == Kit.CAZADOR) {
                KitUtils.removePlayerWolfs(player.getPlayer());
            }

            arena.getGame().playerLeaveGame(player);
            arena.getGame().getScoreboardManager().removeScoreboard(player.getPlayer().getName());
        }

    }

}
