/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Manager;

import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Mobs.BossEnemy;
import com.hyuchiha.village_defense.Mobs.EnemyIA;
import com.hyuchiha.village_defense.Output.Output;
import org.bukkit.configuration.Configuration;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author hyuchiha
 */
public class MobManager {
    
    private static final ArrayList<EnemyIA> enemyObjects = new ArrayList<>();
    private static final ArrayList<BossEnemy> bossEnemyObjects = new ArrayList<>();
    
    public static void registerMobs(){
        Output.log("Registrando mounstruos");
        Configuration config = Main.getInstance().getConfig("config.yml");

        /*
        enemyObjects.add(new ZombieMob(config.getConfigurationSection("Mobs.ZombieMob")));
        enemyObjects.add(new PigmanMob(config.getConfigurationSection("Mobs.PigmanMob")));
        enemyObjects.add(new SkeletonMob(config.getConfigurationSection("Mobs.SkeletonMob")));
        enemyObjects.add(new SpiderMob(config.getConfigurationSection("Mobs.SpiderMob")));
        enemyObjects.add(new SpiderCaveMob(config.getConfigurationSection("Mobs.SpiderCaveMob")));
        enemyObjects.add(new WitchMob(config.getConfigurationSection("Mobs.WitchMob")));
        enemyObjects.add(new WhiterSkullMob(config.getConfigurationSection("Mobs.WhiterSkullMob")));
        enemyObjects.add(new CreeperMob(config.getConfigurationSection("Mobs.CreeperMob")));
        enemyObjects.add(new CreeperChargedMob(config.getConfigurationSection("Mobs.CreeperChargedMob")));
        enemyObjects.add(new Tank(config.getConfigurationSection("Mobs.Tank")));
        
        Output.log("Registrando bosses");
        bossEnemyObjects.add(new MagmaBoss(config.getConfigurationSection("Bosses.MagmaBoss")));
        bossEnemyObjects.add(new SlimeBoss(config.getConfigurationSection("Bosses.SlimeBoss")));*/
    }

    public static ArrayList<EnemyIA> getNextEnemyWave(int currentWave, int difficulty){
        ArrayList<EnemyIA> enemies = new ArrayList();
        Random random = new Random();
        
        for(EnemyIA enemy : enemyObjects){
            if(currentWave >= enemy.getStartingWave()){
                int val = (difficulty /enemy.getDifficulty())+2;
                int toSpawn = random.nextInt(val)+1;
                
                while(toSpawn >0){
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
