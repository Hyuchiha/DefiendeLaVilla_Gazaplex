/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Timers;

import com.hyuchiha.village_defense.Game.Game;
import com.hyuchiha.village_defense.Game.GamePlayer;
import com.hyuchiha.village_defense.Game.GameState;
import com.hyuchiha.village_defense.Game.Wave;
import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Manager.MobManager;
import com.hyuchiha.village_defense.Manager.PlayerManager;
import com.hyuchiha.village_defense.Manager.ShopManager;
import com.hyuchiha.village_defense.Messages.Translator;
import com.hyuchiha.village_defense.MessagesApi.ActionBar;
import com.hyuchiha.village_defense.Scoreboard.ScoreboardType;
import com.hyuchiha.village_defense.Utils.Sound;
import com.hyuchiha.village_defense.Utils.SpecialUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author hyuchiha
 */
public class GameTimer extends BukkitRunnable {

  private final Main plugin;
  private int wave = 1, secondsTillNextWave = -1;
  private final Game game;
  private boolean hasSpawnedFirstWave = false;
  private final int moneyWave;
  private final int bossWave;
  private final int waveChangeNumber;
  private final int waveEventAt;
  private boolean spetialWaveEnabled = false;
  private int gemsPhase;

  public GameTimer(Main plugin, Game game) {
    this.plugin = plugin;
    this.game = game;
    this.moneyWave = plugin.getConfig().getInt("Game.moneyAt");
    this.bossWave = plugin.getConfig().getInt("Game.bossWaveAt");
    this.waveChangeNumber = plugin.getConfig().getInt("Game.shopWaveChange");
    this.gemsPhase = plugin.getConfig().getInt("Game.gems-wave-finish");
    this.waveEventAt = plugin.getConfig().getInt("Game.wave-event-at");
    this.spetialWaveEnabled = plugin.getConfig().getBoolean("Game.enable-wave-event");

    game.setGameState(GameState.INGAME);
    game.getArena().updateState();
    game.sendPlayersToGame();

    playWave();

    this.runTaskTimer(plugin, 20L, 20L);
  }

  @Override
  public void run() {

    if (hasSpawnedFirstWave && canFinish()) {
      game.getWave().cancelWave();

      game.sendMessageToPlayers(Translator.getPrefix() + Translator.getColoredString("GAME.GAME_RESTART"));

      new RestartTimer(plugin, game);

      this.cancel();

      return;
    }

    if (hasSpawnedFirstWave) {
      if (game.getWave().getProgress() < 0.1) {
        if (secondsTillNextWave == -1) {
          secondsTillNextWave = plugin.getConfig().contains("Timers.between-fase") ? plugin.getConfig().getInt("Timers.between-fase") : 15;

          game.getWave().endWave();
          wave++;

          gemsPhase += (3 * wave) * .8;
          for (GamePlayer player : game.getPlayersInGame()) {
            if (player.isKilled()) {
              player.regamePlayer();
            }
            player.updateGems(gemsPhase);
            player.sendMessage(Translator.getPrefix() + Translator.getColoredString("GAME.NEXT_WAVE_START").replace("%TIME%", Integer.toString(secondsTillNextWave)));
            game.getScoreboardManager().updateScoreboard(ScoreboardType.INGAME);
            game.getScoreboardManager().updateScoreboard(ScoreboardType.SPECTATOR);
          }

          return;
        } else {
          if (secondsTillNextWave == 0) {
            secondsTillNextWave = -1;

            for (GamePlayer player : game.getPlayersInGame()) {
              if (player.isKilled()) {
                player.regamePlayer();
              }

              player.sendMessage(Translator.getPrefix() + Translator.getColoredString("GAME.WAVE_START").replace("%WAVE_NUMBER%", Integer.toString(wave)));
            }

            giveWaveSpecials();
            playWave();

            return;
          }
        }

        secondsTillNextWave--;
      }
    }
  }

  public boolean canFinish() {
    return game.getWave() != null && game.getWave().getState() != Wave.WaveState.RESTARTING && (game.getWave().villagersAreDead() || game.allPlayersDeath() || game.getPlayersInGame().size() <= 0);
  }

  public void giveWaveSpecials() {

    if (wave % waveChangeNumber == 0) {
      for (GamePlayer player : game.getPlayersInGame()) {
        Player inGamePlayer = player.getPlayer();
        inGamePlayer.closeInventory();
      }
      ShopManager.getNewRandomShops(game.getArena());
    }

    if (wave % moneyWave == 0) {
      for (GamePlayer player : game.getPlayersInGame()) {
        //Aqui se les pagara con Vault
        int moneyToGive = plugin.getConfig().getInt("Game.money");
        PlayerManager.addMoney(player.getPlayer(), moneyToGive);
      }
      game.getScoreboardManager().updateScoreboard(ScoreboardType.INGAME);
    }

    if (wave % waveEventAt == 0 && spetialWaveEnabled) {
      SpecialUtils.addRandomEffectToArena(game);
    }
  }

  private void playWave() {

    if (wave == 1) {
      Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {

        @Override
        public void run() {
          Wave wave = new Wave(plugin.getConfig().getInt("Game.difficulty"), game);
          wave.setToSpawn(MobManager.getNextEnemyWave(1, plugin.getConfig().getInt("Game.difficulty")));

          game.setWave(wave);

          for (GamePlayer player : game.getPlayersInGame()) {
            player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ENDERDRAGON_GROWL.bukkitSound(), 1.5F, 1.5F);
            player.getKit().getKit().giveSpawnItems(player.getPlayer());

            ActionBar.send(player.getPlayer(),
                Translator.getColoredString("GAME.WAVE_START")
                    .replace("%WAVE_NUMBER%",
                        Integer.toString(
                            GameTimer.this.wave)));
            game.getScoreboardManager().giveScoreboard(player.getPlayer().getName(), ScoreboardType.INGAME);
          }

          Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
              hasSpawnedFirstWave = true;
            }
          }, 100L);

          game.getWave().startWave();
        }
      }, 100L);
    } else {
      game.getWave().setBossWave(wave % bossWave == 0);

      for (GamePlayer player : game.getPlayersInGame()) {
        Player inGamePlayer = player.getPlayer();
        inGamePlayer.playSound(inGamePlayer.getLocation(), Sound.ENDERDRAGON_GROWL.bukkitSound(), 1.5F, 1.5F);
      }

      game.getScoreboardManager().updateScoreboard(ScoreboardType.INGAME);
      game.getScoreboardManager().updateScoreboard(ScoreboardType.SPECTATOR);

      game.getWave().startWave();
    }

  }

}
