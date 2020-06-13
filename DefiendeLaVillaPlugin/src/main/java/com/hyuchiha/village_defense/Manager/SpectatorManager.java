package com.hyuchiha.village_defense.Manager;

import com.hyuchiha.village_defense.Game.GamePlayer;
import com.hyuchiha.village_defense.Game.PlayerState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * @author hyuchiha
 */
public class SpectatorManager {

  private static final ArrayList<String> spectators = new ArrayList<>();

  public static void addSpectator(Player player) {

    if (isSpectator(player)) {
      return;
    }

    GamePlayer vdplayer = PlayerManager.getPlayer(player);

    for (GamePlayer playerInGame : vdplayer.getArena().getGame().getPlayersInGame()) {
      if (!vdplayer.getPlayer().getName().equals(playerInGame.getPlayer().getName())) {
        playerInGame.getPlayer().hidePlayer(player);
      }
    }

    player.setAllowFlight(true);
    player.setFlying(true);
    spectators.add(player.getName());

    if (vdplayer.getState() == PlayerState.SPECTATING) {
      vdplayer.getPlayer().teleport(vdplayer.getArena().getSpawnArenaLocation());
    }
  }

  public static boolean isSpectator(Player player) {
    return spectators.contains(player.getName());
  }

  public static void removeSpectator(Player player) {
    if (player.isOnline()) {
      for (Player pl : Bukkit.getOnlinePlayers()) {
        pl.showPlayer(player);
      }
    }

    player.setAllowFlight(false);
    player.setFlying(false);
    player.setFallDistance(0);
    player.setHealth(player.getMaxHealth());
    player.setFoodLevel(20);
    player.setSaturation(20);

    spectators.remove(player.getName());

  }

  public static void clearSpectators() {
    try {
      for (String name : spectators) {
        removeSpectator(Bukkit.getPlayerExact(name));
      }

      spectators.clear();
    } catch (Exception e) {

    }

  }
}
