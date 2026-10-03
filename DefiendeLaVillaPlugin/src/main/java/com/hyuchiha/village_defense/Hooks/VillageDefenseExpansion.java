package com.hyuchiha.village_defense.Hooks;

import com.hyuchiha.village_defense.Arena.Arena;
import com.hyuchiha.village_defense.Database.Base.Account;
import com.hyuchiha.village_defense.Game.Game;
import com.hyuchiha.village_defense.Game.GamePlayer;
import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Manager.PlayerManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

/**
 * Expansion de PlaceholderAPI ({@code %villagedefense_<id>%}).
 *
 * <p>Stats (cache-only, nunca IO en render; "0" si la cuenta aun no esta cacheada):
 * kills, deaths, bosses_kills, max_wave, min_wave, kdr.
 *
 * <p>Partida del jugador (vacio si no esta en arena): gems, kit, arena, state, wave, players_alive.
 */
public class VillageDefenseExpansion extends PlaceholderExpansion {
  private final Main plugin;

  public VillageDefenseExpansion(Main plugin) {
    this.plugin = plugin;
  }

  @Override
  public String getIdentifier() {
    return "villagedefense";
  }

  @Override
  public String getAuthor() {
    return "Hyuchiha";
  }

  @Override
  public String getVersion() {
    return plugin.getDescription().getVersion();
  }

  @Override
  public boolean persist() {
    return true;
  }

  @Override
  public String onRequest(OfflinePlayer offlinePlayer, String identifier) {
    if (offlinePlayer == null || identifier == null) {
      return null;
    }

    String id = identifier.toLowerCase();

    switch (id) {
      case "kills":
      case "deaths":
      case "bosses_kills":
      case "max_wave":
      case "min_wave":
      case "kdr":
        return statValue(offlinePlayer, id);
    }

    // Lookup sin registrar: PlayerManager.getPlayer crearia un GamePlayer por cada render.
    GamePlayer gp = PlayerManager.findPlayer(offlinePlayer.getUniqueId());
    Arena arena = (gp != null) ? gp.getArena() : null;
    Game game = (arena != null) ? arena.getGame() : null;

    switch (id) {
      case "gems":
        return (gp != null) ? Integer.toString(gp.getGems()) : "0";
      case "kit":
        return (gp != null && gp.getKit() != null) ? gp.getKit().name() : "";
      case "arena":
        return (arena != null) ? arena.getName() : "";
      case "state":
        return (game != null && game.getState() != null) ? game.getState().name() : "";
      case "wave":
        return (game != null && game.getWave() != null) ? Integer.toString(game.getWave().getWaveNumber()) : "0";
      case "players_alive":
        return (game != null) ? Integer.toString(game.getNumberOfAlivePlayers()) : "0";
    }

    return null;
  }

  private String statValue(OfflinePlayer player, String id) {
    Account account = (plugin.getMainDatabase() != null)
        ? plugin.getMainDatabase().getCachedAccount(player.getUniqueId().toString())
        : null;
    if (account == null) {
      return "0";
    }

    switch (id) {
      case "kills":
        return Integer.toString(account.getKills());
      case "deaths":
        return Integer.toString(account.getDeaths());
      case "bosses_kills":
        return Integer.toString(account.getBosses_kills());
      case "max_wave":
        return Integer.toString(account.getMax_wave_reached());
      case "min_wave":
        return Integer.toString(account.getMin_wave_reached());
      default:
        int deaths = Math.max(1, account.getDeaths());
        return String.format("%.1f", account.getKills() / (double) deaths);
    }
  }
}
