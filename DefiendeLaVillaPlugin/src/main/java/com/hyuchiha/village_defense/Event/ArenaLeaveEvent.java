package com.hyuchiha.village_defense.Event;

import com.hyuchiha.village_defense.Arena.Arena;
import com.hyuchiha.village_defense.Game.GamePlayer;

/**
 * @author hyuchiha
 */
public class ArenaLeaveEvent extends BaseEvent {

  private final GamePlayer player;
  private final Arena arena;

  public ArenaLeaveEvent(GamePlayer player, Arena arena) {
    this.player = player;
    this.arena = arena;
  }

  public GamePlayer getPlayer() {
    return player;
  }

  public Arena getArena() {
    return arena;
  }

}
