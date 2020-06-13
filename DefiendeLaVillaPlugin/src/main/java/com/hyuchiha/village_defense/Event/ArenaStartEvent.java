package com.hyuchiha.village_defense.Event;

import com.hyuchiha.village_defense.Arena.Arena;

/**
 * @author hyuchiha
 */
public class ArenaStartEvent extends BaseEvent {

  private Arena arena;

  public ArenaStartEvent(Arena arena) {
    this.arena = arena;
  }

  public Arena getArena() {
    return arena;
  }
}
