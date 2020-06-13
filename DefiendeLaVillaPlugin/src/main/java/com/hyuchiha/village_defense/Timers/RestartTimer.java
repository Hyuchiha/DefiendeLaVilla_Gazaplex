package com.hyuchiha.village_defense.Timers;

import com.hyuchiha.village_defense.Event.ArenaFinishEvent;
import com.hyuchiha.village_defense.Game.Game;
import com.hyuchiha.village_defense.Main;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author hyuchiha
 */
public class RestartTimer extends BukkitRunnable {

  private final Main plugin;
  private final Game game;
  private int timeLeft = 20;

  public RestartTimer(Main plugin, Game game) {
    this.plugin = plugin;
    this.game = game;

    this.runTaskTimer(plugin, 20L, 20L);
  }

  @Override
  public void run() {

    if (timeLeft == 0) {
      Bukkit.getServer().getPluginManager().
          callEvent(new ArenaFinishEvent(game.getArena().getName()));
      cancel();
    }

    timeLeft--;
  }
}
