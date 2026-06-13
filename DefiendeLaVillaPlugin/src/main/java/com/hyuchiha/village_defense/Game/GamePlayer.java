package com.hyuchiha.village_defense.Game;

import com.hyuchiha.village_defense.Arena.Arena;
import com.hyuchiha.village_defense.Manager.ArenaManager;
import com.hyuchiha.village_defense.Manager.SpectatorManager;
import com.hyuchiha.village_defense.Messages.Translator;
import com.hyuchiha.village_defense.Scoreboard.ScoreboardType;
import com.hyuchiha.village_defense.Utils.ArenaUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.UUID;

public class GamePlayer {

  private UUID playerUUID;
  private PlayerState state;
  private String arena;
  private boolean killed;
  private int gems;
  private Kit kit;

  public GamePlayer(UUID uuid) {
    this.playerUUID = uuid;
    this.state = PlayerState.LOBBY;
    this.killed = false;
    this.gems = 0;

    this.kit = Kit.CIVILIAN;
  }

  public PlayerState getState() {
    return state;
  }

  public void setState(PlayerState state) {
    this.state = state;
  }

  public boolean isKilled() {
    return killed;
  }

  public void setKilled(boolean killed) {
    this.killed = killed;
  }

  public int getGems() {
    return gems;
  }

  public void setGems(int gems) {
    this.gems = gems;
  }

  public UUID getPlayerUUID() {
    return playerUUID;
  }

  public void setPlayerUUID(UUID playerUUID) {
    this.playerUUID = playerUUID;
  }

  public Player getPlayer() {
    return Bukkit.getPlayer(playerUUID);
  }

  public void setArena(String arena) {
    this.arena = arena;
  }

  public Arena getArena() {
    return ArenaManager.getArenaConfiguration(arena);
  }

  public void sendMessage(String message) {
    Player player = getPlayer();

    if (player != null) {
      player.sendMessage(message);
    }
  }

  public void initGameSettings() {
    clearData();
    clearArmor();
  }

  public void clearData() {
    Player player = getPlayer();
    if (player == null) {
      return;
    }

    player.setGameMode(GameMode.SURVIVAL);
    player.setExp(0.0F);
    player.setTotalExperience(0);
    player.setLevel(0);
    player.setHealth(20.0);
    player.setExhaustion(0.0F);
    player.setFoodLevel(20);
    player.setFlying(false);

    for (PotionEffect effect : player.getActivePotionEffects()) {
      player.removePotionEffect(effect.getType());
    }

    player.closeInventory();
    player.getInventory().clear();
  }

  private void clearArmor() {
    Player player = getPlayer();
    if (player == null) {
      return;
    }

    player.getInventory().setHelmet(null);
    player.getInventory().setChestplate(null);
    player.getInventory().setLeggings(null);
    player.getInventory().setBoots(null);
  }

  public void sendPlayerToLobbyArena() {
    Player player = getPlayer();
    if (player == null) {
      return;
    }

    clearData();
    ArenaUtils.giveArenaLobbyObjects(player);
    Location loc = getArena().getLobbyGameLocation();

    player.teleport(loc);
    setKilled(false);
    sendMessage(Translator.getPrefix() +
        Translator.getColoredString("GAME.PLAYER_JOIN_ARENA")
            .replace("%ARENA%", getArena().getName()));


    //Se actualizara aqui el scoreboard cuando se entre a la partida
    //y no este iniciada
    Game game = getArena().getGame();
    game.getScoreboardManager()
        .giveScoreboard(player.getName(), ScoreboardType.LOBBY_GAME);

  }

  public void sendPlayerToArena() {
    Player player = getPlayer();
    if (player == null) {
      return;
    }

    initGameSettings();
    ArenaUtils.giveShopObjects(player);
    player.teleport(getArena().getSpawnArenaLocation());
    setKilled(false);
    player.sendMessage(Translator.getPrefix() + Translator.getColoredString("GAME.ARENA_INIT"));

    setState(PlayerState.INGAME);
    //Se reinicia la antigua scoreboard
    player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
  }

  public void regamePlayer() {
    Player player = getPlayer();
    if (player == null) {
      return;
    }

    player.teleport(getArena().getSpawnArenaLocation());
    SpectatorManager.removeSpectator(player);
    initGameSettings();
    ArenaUtils.giveShopObjects(player);
    setKilled(false);
    player.sendMessage(Translator.getPrefix() + Translator.getColoredString("GAME.PLAYER_RESPAWN"));

    getKit().getKit().giveSpawnItems(player);
    //Se actualizara aqui el scoreboard
    getArena().getGame().getScoreboardManager().updateScoreboard(ScoreboardType.INGAME);
  }

  public void sendPlayerToLobby() {
    Player player = getPlayer();
    if (player == null) {
      return;
    }

    initGameSettings();
    player.teleport(ArenaManager.getLobby().getLobbyLocation());
    setState(PlayerState.LOBBY);
    setArena(null);
    setKit(Kit.CIVILIAN);
    setGems(0);
    setKilled(false);
    //Se entregan los objetos del lobby principal
    ArenaUtils.givePrincipalLobbyObjects(player);
    //Se cambiara el scoreboard o se eliminara
    player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
  }

  public void setPlayerSpectator() {
    Player player = getPlayer();
    if (player == null) {
      return;
    }

    player.teleport(getArena().getSpawnArenaLocation());
    setState(PlayerState.SPECTATING);
    SpectatorManager.addSpectator(player);

    //Se entrega el objeto de espectador
    ArenaUtils.giveSpectatorObjects(player);
  }

  public void updateGems(int gemsPhase) {
    this.gems += gemsPhase;
  }

  public Kit getKit() {
    return kit;
  }

  public void setKit(Kit kit) {
    this.kit = kit;
  }
}
