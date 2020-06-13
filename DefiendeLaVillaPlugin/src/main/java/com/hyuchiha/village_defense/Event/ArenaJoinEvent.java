package com.hyuchiha.village_defense.Event;

import com.hyuchiha.village_defense.Game.GamePlayer;

/**
 * @author hyuchiha
 */
public class ArenaJoinEvent extends BaseEvent {

  private final String arenaName;
  private final GamePlayer player;

  public ArenaJoinEvent(String arenaName, GamePlayer playerjoin) {
    this.arenaName = arenaName;
    this.player = playerjoin;
  }

  public String getArenaName() {
    return arenaName;
  }

  public GamePlayer getPlayer() {
    return player;
  }
}
