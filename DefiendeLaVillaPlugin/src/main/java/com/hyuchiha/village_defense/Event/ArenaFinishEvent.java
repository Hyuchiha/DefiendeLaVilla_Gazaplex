package com.hyuchiha.village_defense.Event;

/**
 * @author hyuchiha
 */
public class ArenaFinishEvent extends BaseEvent {

  private final String arena;

  public ArenaFinishEvent(String arena) {
    this.arena = arena;
  }

  public String getArena() {
    return arena;
  }
}
