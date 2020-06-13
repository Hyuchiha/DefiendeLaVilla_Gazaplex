/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Utils;

import com.hyuchiha.village_defense.Arena.Arena;
import com.hyuchiha.village_defense.Game.Game;
import com.hyuchiha.village_defense.Game.GamePlayer;
import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Manager.PlayerManager;
import com.hyuchiha.village_defense.Messages.Translator;
import com.hyuchiha.village_defense.MessagesApi.TitleAPI;
import com.hyuchiha.village_defense.Output.Output;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

/**
 * @author hyuchiha
 */
public class SpecialUtils {

  public static void addRandomEffectToArena(Game game) {
    Random ran = new Random();
    switch (ran.nextInt(7)) {
      case 1:
        Output.log("Random objects");
        spawnRandomsObjectsInGame(game.getArena());
        break;
      case 2:
        Output.log("Random potion effect");
        addRandomPotionEffectToEveryone(game);
        break;
      case 3:
        Output.log("All on fire");
        applyFireToEveryone(game);
        break;
      case 4:
        Output.log("Money for everyone");
        giveRandomMoneyToEveryone(game);
        break;
      case 5:
        Output.log("Health increased");
        incrementMaxHealthToEveryone(game);
        break;
      case 6:
        Output.log("Health reduced");
        reduceMaxHealthToEveryone(game);
        break;
      case 7:
        Output.log("Random mob");
        spawnRandomNewMob(game.getArena());
        break;
      default:
        Output.log("Error");
        break;
    }

    for (GamePlayer player : game.getPlayersInGame()) {
      Output.log("Sending message to: " + player.getPlayer().getName());
      TitleAPI.send(
          player.getPlayer(),
          Translator.getColoredString("SPECIAL_EVENT_TITLE"),
          Translator.getColoredString("SPECIAL_EVENT_SUBTITLE"),
          10,
          40,
          10);
    }
  }

  private static void giveRandomMoneyToEveryone(Game game) {
    for (GamePlayer player : game.getPlayersInGame()) {
      PlayerManager.addMoney(player.getPlayer(), 200);
    }
  }

  private static void applyFireToEveryone(Game game) {
    for (GamePlayer player : game.getPlayersInGame()) {
      Player inGamePlayer = player.getPlayer();
      inGamePlayer.setFireTicks(15);
    }
  }

  private static void addRandomPotionEffectToEveryone(Game game) {
    PotionEffect e;
    Random ran = new Random();
    switch (ran.nextInt()) {
      case 1:
        e = new PotionEffect(PotionEffectType.SLOW, 200, 2);
        break;
      case 2:
        e = new PotionEffect(PotionEffectType.WITHER, 200, 1);
        break;
      case 3:
        e = new PotionEffect(PotionEffectType.ABSORPTION, 200, 2);
        break;
      case 4:
        e = new PotionEffect(PotionEffectType.REGENERATION, 200, 2);
        break;
      case 5:
        e = new PotionEffect(PotionEffectType.JUMP, 200, 50);
        break;
      case 6:
        e = new PotionEffect(PotionEffectType.SPEED, 200, 3);
        break;
      case 7:
        e = new PotionEffect(PotionEffectType.POISON, 200, 2);
        break;
      case 8:
        e = new PotionEffect(PotionEffectType.FAST_DIGGING, 200, 100);
        break;
      case 9:
        e = new PotionEffect(PotionEffectType.CONFUSION, 200, 2);
        break;
      case 10:
        e = new PotionEffect(PotionEffectType.BLINDNESS, 200, 2);
        break;
      case 11:
        e = new PotionEffect(PotionEffectType.HEALTH_BOOST, 200, 5);
        break;
      case 12:
        e = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 50);
        break;
      default:
        e = new PotionEffect(PotionEffectType.SATURATION, 200, 2);
        break;
    }

    for (GamePlayer player : game.getPlayersInGame()) {
      Player inGamePlayer = player.getPlayer();
      inGamePlayer.addPotionEffect(e);
    }
  }

  private static void spawnRandomNewMob(Arena arena) {
    Location loc = arena.getMobspawns().get(0);

  }

  private static void incrementMaxHealthToEveryone(final Game game) {
    for (GamePlayer player : game.getPlayersInGame()) {
      Player inGamePlayer = player.getPlayer();
      inGamePlayer.setMaxHealth(inGamePlayer.getMaxHealth() + 20.0);
    }

    Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), new Runnable() {

      @Override
      public void run() {
        for (GamePlayer player : game.getPlayersInGame()) {
          Player inGamePlayer = player.getPlayer();
          inGamePlayer.setMaxHealth(20.0);
        }
      }
    }, 30 * 20L);
  }

  private static void spawnRandomsObjectsInGame(Arena arena) {
    Random ran = new Random();
    Location loc = arena.getMobspawns().get(ran.nextInt(arena.getMobspawns().size()));
    switch (ran.nextInt(3)) {
      case 1:
        loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.GOLDEN_APPLE));
        break;
      case 2:
        loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.DIAMOND_CHESTPLATE));
        break;
      case 3:
        loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.DIAMOND_SWORD));
        break;
      default:

        break;
    }
  }

  private static void reduceMaxHealthToEveryone(final Game game) {

    for (GamePlayer player : game.getPlayersInGame()) {
      Player inGamePlayer = player.getPlayer();
      inGamePlayer.setMaxHealth(inGamePlayer.getMaxHealth() - 4.0);
    }

    Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), new Runnable() {

      @Override
      public void run() {
        for (GamePlayer player : game.getPlayersInGame()) {
          Player inGamePlayer = player.getPlayer();
          inGamePlayer.setMaxHealth(20.0);
        }
      }
    }, 30 * 20L);
  }
}
