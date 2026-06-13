package com.hyuchiha.village_defense.Manager;

import com.hyuchiha.village_defense.Game.GamePlayer;
import com.hyuchiha.village_defense.Game.PlayerState;
import com.hyuchiha.village_defense.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

/**
 * @author hyuchiha
 */
public class SpectatorManager {

  private static final ArrayList<UUID> spectators = new ArrayList<>();

  public static void addSpectator(Player player) {

    if (isSpectator(player)) {
      return;
    }

    GamePlayer vdplayer = PlayerManager.getPlayer(player);

    for (GamePlayer playerInGame : vdplayer.getArena().getGame().getPlayersInGame()) {
      Player other = playerInGame.getPlayer();
      // Comparacion por UUID y null-guard del jugador objetivo (puede estar offline).
      if (other != null && !playerInGame.getPlayerUUID().equals(vdplayer.getPlayerUUID())) {
        other.hidePlayer(player);
      }
    }

    player.setAllowFlight(true);
    player.setFlying(true);
    spectators.add(player.getUniqueId());

    if (vdplayer.getState() == PlayerState.SPECTATING) {
      vdplayer.getPlayer().teleport(vdplayer.getArena().getSpawnArenaLocation());
    }
  }

  public static boolean isSpectator(Player player) {
    return spectators.contains(player.getUniqueId());
  }

  public static void removeSpectator(Player player) {
    if (player == null) {
      return;
    }

    if (player.isOnline()) {
      GamePlayer vdplayer = PlayerManager.getPlayer(player);
      // Solo des-ocultar a los jugadores de la misma partida (no todo el servidor).
      if (vdplayer != null && vdplayer.getArena() != null) {
        for (GamePlayer playerInGame : vdplayer.getArena().getGame().getPlayersInGame()) {
          Player other = playerInGame.getPlayer();
          if (other != null) {
            other.showPlayer(player);
          }
        }
      }
    }

    player.setAllowFlight(false);
    player.setFlying(false);
    player.setFallDistance(0);
    player.setHealth(player.getMaxHealth());
    player.setFoodLevel(20);
    player.setSaturation(20);

    spectators.remove(player.getUniqueId());

  }

  public static void clearSpectators() {
    // Se itera sobre una copia: removeSpectator() muta la lista 'spectators'
    // (CME si se recorre la lista viva). getPlayerExact puede ser null si el
    // jugador esta offline; removeSpectator ya lo maneja.
    for (UUID uuid : new ArrayList<>(spectators)) {
      removeSpectator(Bukkit.getPlayer(uuid));
    }

    spectators.clear();
  }
}
