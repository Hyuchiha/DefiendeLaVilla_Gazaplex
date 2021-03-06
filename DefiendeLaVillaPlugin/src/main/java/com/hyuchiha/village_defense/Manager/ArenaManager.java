package com.hyuchiha.village_defense.Manager;

import com.hyuchiha.village_defense.Arena.Arena;
import com.hyuchiha.village_defense.Arena.MainLobby;
import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Output.Output;
import org.bukkit.configuration.Configuration;

import java.util.Collection;
import java.util.HashMap;

public class ArenaManager {

  private static final HashMap<String, Arena> arenas = new HashMap<>();
  private static MainLobby lobby = null;

  public static void initArenas() {
    Output.log("Cargando configuraciones de arena");

    Configuration arenasConfig = Main.getInstance().getConfig("arenas.yml");

    for (String arena : arenasConfig.getKeys(false)) {
      if (!arena.equals("Spawn")) {
        arenas.put(arena, loadArena(arena));
      } else {
        lobby = getLobby();
      }
    }
  }

  public static MainLobby getLobby() {
    if (lobby == null) {
      return new MainLobby();
    } else {
      return lobby;
    }
  }

  public static Arena getArenaConfiguration(String arena) {
    return arenas.get(arena);
  }

  private static Arena loadArena(String arena) {
    Output.log("Loading arena: " + arena);
    return new Arena(arena);
  }

  public static void initShopForArenas() {
    Output.log("Iniciando los shops para cada arena");
    for (Arena arena : arenas.values()) {
      ShopManager.InitArenaShop(arena);
    }
  }

  public static Collection<Arena> getArenas() {
    return arenas.values();
  }
}
