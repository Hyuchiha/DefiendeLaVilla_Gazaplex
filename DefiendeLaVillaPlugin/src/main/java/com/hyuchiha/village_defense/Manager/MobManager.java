/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Manager;

import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.MobCreator_v1_15_R1;
import com.hyuchiha.village_defense.Mobs.BossEnemy;
import com.hyuchiha.village_defense.Mobs.EnemyIA;
import com.hyuchiha.village_defense.Mobs.MobCreator;
import com.hyuchiha.village_defense.Mobs.v1_10_R1.MobCreator_v1_10_R1;
import com.hyuchiha.village_defense.Mobs.v1_11_R1.MobCreator_v1_11_R1;
import com.hyuchiha.village_defense.Mobs.v1_12_R1.MobCreator_v1_12_R1;
import com.hyuchiha.village_defense.Mobs.v1_13_R1.MobCreator_v1_13_R1;
import com.hyuchiha.village_defense.Mobs.v1_13_R2.MobCreator_v1_13_R2;
import com.hyuchiha.village_defense.Mobs.v1_14_R1.MobCreator_v1_14_R1;
import com.hyuchiha.village_defense.Mobs.v1_9_R1.MobCreator_v1_9_R1;
import com.hyuchiha.village_defense.Mobs.v1_9_R2.MobCreator_v1_9_R2;
import com.hyuchiha.village_defense.Output.Output;
import org.bukkit.configuration.Configuration;
import org.inventivetalent.reflection.minecraft.Minecraft;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author hyuchiha
 */
public class MobManager {

  private static final ArrayList<EnemyIA> enemyObjects = new ArrayList<>();
  private static final ArrayList<BossEnemy> bossEnemyObjects = new ArrayList<>();

  public static void registerMobs() {
    Output.log("Registrando mounstruos");
    Configuration config = Main.getInstance().getConfig("config.yml");

    MobCreator creator = null;

    switch (Minecraft.Version.getVersion()) {
      case v1_9_R1:
        creator = new MobCreator_v1_9_R1();
        break;
      case v1_9_R2:
        creator = new MobCreator_v1_9_R2();
        break;
      case v1_10_R1:
        creator = new MobCreator_v1_10_R1();
        break;
      case v1_11_R1:
        creator = new MobCreator_v1_11_R1();
        break;
      case v1_12_R1:
        creator = new MobCreator_v1_12_R1();
        break;
      case v1_13_R1:
        creator = new MobCreator_v1_13_R1();
        break;
      case v1_13_R2:
        creator = new MobCreator_v1_13_R2();
        break;
      case v1_14_R1:
        creator = new MobCreator_v1_14_R1();
        break;
      case v1_15_R1:
        creator = new MobCreator_v1_15_R1();
        break;
      default:
        Output.log("Version not supported");
        break;
    }

    if (creator != null) {
      enemyObjects.addAll(creator.createWaveMobs(config.getConfigurationSection("Mobs")));
      bossEnemyObjects.addAll(creator.createBossMobs(config.getConfigurationSection("Bosses")));
    } else {
      Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
    }
  }

  public static ArrayList<EnemyIA> getNextEnemyWave(int currentWave, int difficulty) {
    ArrayList<EnemyIA> enemies = new ArrayList<>();
    Random random = new Random();

    for (EnemyIA enemy : enemyObjects) {
      if (currentWave >= enemy.getStartingWave()) {
        int val = (difficulty / enemy.getDifficulty()) + 2;
        int toSpawn = random.nextInt(val) + 1;

        while (toSpawn > 0) {
          enemies.add(enemy);
          toSpawn--;
        }
      }
    }

    return enemies;
  }


  public static ArrayList<EnemyIA> getEnemyObjects() {
    return enemyObjects;
  }

  public static ArrayList<BossEnemy> getBossEnemyObjects() {
    return bossEnemyObjects;
  }
}
