package com.hyuchiha.village_defense.Game;

import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Manager.MobManager;
import com.hyuchiha.village_defense.Messages.Translator;
import com.hyuchiha.village_defense.Mobs.BossEnemy;
import com.hyuchiha.village_defense.Mobs.EnemyIA;
import com.hyuchiha.village_defense.Scoreboard.ScoreboardType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author hyuchiha
 */
public class Wave {

    private int wave, difficulty;
    private boolean bossWave;
    private Game game;
    private ArrayList<LivingEntity> enemies = new ArrayList<>();
    private ArrayList<LivingEntity> villagers = new ArrayList<>();
    private WaveState state;
    private ArrayList<EnemyIA> toSpawn = new ArrayList<>();

    public Wave(int difficulty, Game game) {
        this.wave = 0;
        this.difficulty = difficulty;
        this.bossWave = false;
        this.game = game;
        this.state = WaveState.STARTING;
    }

    public int getWaveNumber() {
        return wave;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public boolean isBossWave() {
        return bossWave;
    }

    public void setBossWave(boolean bossWave) {
        this.bossWave = bossWave;
    }

    public float getProgress() {

        int livingZombieCount = getNumberOfEnemiesLeft();

        if(livingZombieCount == -1){
            return 100f;
        }
        
        if (livingZombieCount == 0) {
            state = WaveState.RESTARTING;
            return 0f;
        }

        return enemies.size() / livingZombieCount;
    }

    public ArrayList<LivingEntity> getEnemies() {
        return enemies;
    }

    public void setEnemies(ArrayList<LivingEntity> enemies) {
        this.enemies = enemies;
    }

    private void killVillagers() {
        for (Entity e : villagers) {
            e.remove();
        }
        villagers.clear();
    }

    public void cancelWave() {
        this.state = WaveState.ENDING;
        
        killVillagers();

        for (LivingEntity e : enemies) {
            e.remove();
        }
    }

    public void addNewVillagerCreatedByPlayer() {
        villagers.add((LivingEntity) createNewVillager());
        game.getScoreboardManager().updateScoreboard(ScoreboardType.INGAME);
        game.getScoreboardManager().updateScoreboard(ScoreboardType.SPECTATOR);
    }

    public void startWave() {
        this.wave++;
        
        final Random r = new Random();

        this.state = WaveState.PROGRESS;

        spawnEnemies();
        spawnVillagers(wave);

        if (MobManager.getBossEnemyObjects().size() > 0 && isBossWave()) {

            BossEnemy e = MobManager.getBossEnemyObjects().get(r.nextInt(MobManager.getBossEnemyObjects().size()));
            //Se actualiza la scoreboard
            enemies.add(e.spawnEntity(game.getArena().getMobspawns().get(r.nextInt(game.getArena().getMobspawns().size())), Main.getInstance(), getGame().getWave().getWaveNumber()));

            for (GamePlayer player : game.getPlayersInGame()) {
                player.sendMessage(Translator.getPrefix() + ChatColor.BLUE + ChatColor.stripColor(e.getCustomName()) + ChatColor.YELLOW + " ha aparecido!");
                game.getScoreboardManager().updateScoreboard(ScoreboardType.INGAME);
                game.getScoreboardManager().updateScoreboard(ScoreboardType.SPECTATOR);
            }
        }
        
        prepareNextWave();
    }
    
    private void spawnEnemies(){
        this.enemies.clear();
        
        long value = 5L;
        int timeSpawn = 1;
        final Random r = new Random();
        
        for (final EnemyIA e : toSpawn) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {

                @Override
                public void run() {
                    enemies.add(e.spawnEntity(
                            getGame().getArena().getMobspawns().get(r.nextInt(getGame().getArena().getMobspawns().size())),
                            Main.getInstance(),
                            getGame().getWave().getWaveNumber())
                    );
                    game.getScoreboardManager().updateScoreboard(ScoreboardType.INGAME);
                    game.getScoreboardManager().updateScoreboard(ScoreboardType.SPECTATOR);
                }
            }, value * (timeSpawn++) );
        }
    }
    
    private void spawnVillagers(int cant) {
        this.villagers.clear();
        
        long value = 5L;
        int timeSpawn = 1;
        for (int i = 0; i < cant * 2; i++) {

            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {

                @Override
                public void run() {
                    villagers.add((LivingEntity) createNewVillager());
                    game.getScoreboardManager().updateScoreboard(ScoreboardType.INGAME);
                    game.getScoreboardManager().updateScoreboard(ScoreboardType.INGAME);
                }
            }, value * (timeSpawn++));
        }
    }

    private Entity createNewVillager() {
        Random r = new Random();
        Location spawnLocation
                = getGame().getArena().getMobspawns().get(r.nextInt(getGame().getArena().getMobspawns().size()));
        Entity e = spawnLocation.getWorld().spawnEntity(spawnLocation,
                EntityType.VILLAGER);
        LivingEntity entity = (LivingEntity) e;
        entity.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
        return entity;
    }

    public Game getGame() {
        return game;
    }

    public boolean villagersAreDead() {
        int livingVillagerCount = getNumberOfLiveVillagers();

        return livingVillagerCount == 0;
    }

    public int getNumberOfLiveVillagers() {
        int livingVillagerCount = 0;

        for (LivingEntity e : villagers) {
            if (!e.isDead()) {
                livingVillagerCount++;
            }
        }
        return livingVillagerCount;
    }

    public int getNumberOfEnemiesLeft() {
        int livingZombieCount = 0;

        if(enemies.isEmpty()){
            return -1;
        }
        
        for (LivingEntity le : enemies) {
            if (le != null && !le.isDead()) {
                livingZombieCount++;
            }
        }
        
        return livingZombieCount;
    }

    public WaveState getState() {
        return state;
    }

    public ArrayList<EnemyIA> getToSpawn() {
        return toSpawn;
    }

    public void setToSpawn(ArrayList<EnemyIA> toSpawn) {
        this.toSpawn = toSpawn;
    }
    
    public void endWave(){
        this.killVillagers();
    }
    
    private void prepareNextWave(){
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                difficulty = (((difficulty * (wave+1))+((wave+1)*3))/(((wave+1)%50)+1))+10;
                ArrayList<EnemyIA> toSpawnInNextWave = MobManager.getNextEnemyWave(wave+1, difficulty);
                setToSpawn(toSpawnInNextWave);
            }
        });
    }
    
    public enum WaveState{
        STARTING,PROGRESS,RESTARTING,ENDING
    }
    
}
