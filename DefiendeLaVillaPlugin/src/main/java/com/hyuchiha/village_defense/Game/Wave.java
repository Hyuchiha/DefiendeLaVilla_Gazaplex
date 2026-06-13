package com.hyuchiha.village_defense.Game;

import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Manager.MobManager;
import com.hyuchiha.village_defense.Messages.Translator;
import com.hyuchiha.village_defense.Mobs.BossEnemy;
import com.hyuchiha.village_defense.Mobs.EnemyIA;
import com.hyuchiha.village_defense.Output.Output;
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
 * @author hyuchiha
 */
public class Wave {

  private int wave, difficulty;
  private boolean bossWave;
  private final Game game;
  private ArrayList<LivingEntity> enemies = new ArrayList<>();
  private final ArrayList<LivingEntity> villagers = new ArrayList<>();
  private WaveState state;
  private ArrayList<EnemyIA> toSpawn = new ArrayList<>();
  private int pendingSpawns = 0;

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

    if (livingZombieCount == -1) {
      return 100f;
    }

    if (livingZombieCount == 0) {
      state = WaveState.RESTARTING;
      return 0f;
    }

    // Fracción de enemigos de la oleada que sigue viva (0.0 = todos muertos, 1.0 = ninguno muerto).
    // División float explícita: con ints la expresión siempre daba 0 o 1 y la oleada nunca avanzaba.
    // El total incluye los mobs aun pendientes de spawnear para no dar progreso falso en la ventana de spawn.
    return (float) livingZombieCount / (enemies.size() + pendingSpawns);
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

  public void addNewVillagerCreatedByPlayer(Location spawnLocation) {
    Output.log("Generating villager");
    villagers.add((LivingEntity) createNewVillageInLocation(spawnLocation));
    game.getScoreboardManager().updateScoreboard(ScoreboardType.INGAME);
    game.getScoreboardManager().updateScoreboard(ScoreboardType.SPECTATOR);
  }

  public void startWave() {
    if (getGame().getArena().getMobSpawns().isEmpty()) {
      Output.logError("La arena '" + getGame().getArena().getName()
          + "' no tiene puntos de aparicion (mobSpawns) configurados. No se puede iniciar la oleada.");
      for (GamePlayer player : game.getPlayersInGame()) {
        player.sendMessage(Translator.getPrefix() + ChatColor.RED
            + "Esta arena no tiene puntos de aparicion configurados. Avisa a un administrador.");
      }
      // Sin mobSpawns no hay aldeanos: villagersAreDead() sera true y GameTimer
      // reiniciara la partida de forma limpia en el siguiente tick.
      return;
    }

    this.wave++;

    final Random r = java.util.concurrent.ThreadLocalRandom.current();

    this.state = WaveState.PROGRESS;

    spawnEnemies();
    spawnVillagers(wave);

    if (MobManager.getBossEnemyObjects().size() > 0 && isBossWave()) {

      BossEnemy e = MobManager.getBossEnemyObjects().get(r.nextInt(MobManager.getBossEnemyObjects().size()));
      //Se actualiza la scoreboard
      enemies.add(e.spawnEntity(game.getArena().getMobSpawns().get(r.nextInt(game.getArena().getMobSpawns().size())), Main.getInstance(), getGame().getWave().getWaveNumber()));

      for (GamePlayer player : game.getPlayersInGame()) {
        player.sendMessage(Translator.getPrefix() + ChatColor.BLUE + ChatColor.stripColor(e.getCustomName()) + ChatColor.YELLOW + " ha aparecido!");
        game.getScoreboardManager().updateScoreboard(ScoreboardType.INGAME);
        game.getScoreboardManager().updateScoreboard(ScoreboardType.SPECTATOR);
      }
    }

    prepareNextWave();
  }

  private void spawnEnemies() {
    this.enemies.clear();
    this.pendingSpawns = toSpawn.size();

    long value = 5L;
    int timeSpawn = 1;
    final Random r = java.util.concurrent.ThreadLocalRandom.current();

    for (final EnemyIA e : toSpawn) {
      Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
        enemies.add(e.spawnEntity(
            getGame().getArena().getMobSpawns().get(r.nextInt(getGame().getArena().getMobSpawns().size())),
            Main.getInstance(),
            getGame().getWave().getWaveNumber())
        );
        pendingSpawns--;
        game.getScoreboardManager().updateScoreboard(ScoreboardType.INGAME);
        game.getScoreboardManager().updateScoreboard(ScoreboardType.SPECTATOR);
      }, value * (timeSpawn++));
    }
  }

  private void spawnVillagers(int cant) {
    this.villagers.clear();

    long value = 5L;
    int timeSpawn = 1;
    for (int i = 0; i < cant * 2; i++) {

      Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
        villagers.add((LivingEntity) createNewVillager());
        game.getScoreboardManager().updateScoreboard(ScoreboardType.INGAME);
        game.getScoreboardManager().updateScoreboard(ScoreboardType.SPECTATOR);
      }, value * (timeSpawn++));
    }
  }

  private Entity createNewVillager() {
    Random r = java.util.concurrent.ThreadLocalRandom.current();
    Location spawnLocation = getGame().getArena().getMobSpawns().get(r.nextInt(getGame().getArena().getMobSpawns().size()));
    Entity e = spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.VILLAGER);
    LivingEntity entity = (LivingEntity) e;
    entity.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
    return entity;
  }

  public Entity createNewVillageInLocation(Location spawnLocation) {
    Entity e = spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.VILLAGER);
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

    for (LivingEntity le : enemies) {
      if (le != null && !le.isDead()) {
        livingZombieCount++;
      }
    }

    // Mobs aun programados (ventana de spawn) cuentan como vivos: la oleada no ha terminado.
    livingZombieCount += pendingSpawns;

    if (livingZombieCount == 0 && enemies.isEmpty()) {
      return -1;
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

  public void endWave() {
    this.killVillagers();
  }

  private void prepareNextWave() {
    // Snapshot de los campos en el main-thread (aqui se llama desde startWave).
    final int currentDifficulty = this.difficulty;
    final int nextWave = this.wave + 1;

    Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
      // El calculo pesado (getNextEnemyWave) queda async, pero solo sobre locales.
      final int newDifficulty = (((currentDifficulty * nextWave) + (nextWave * 3)) / ((nextWave % 50) + 1)) + 10;
      final ArrayList<EnemyIA> toSpawnInNextWave = MobManager.getNextEnemyWave(nextWave, newDifficulty);

      // Las escrituras de difficulty/toSpawn se aplican en el main-thread, que es
      // quien las lee en startWave()/spawnEnemies() -> sin data race.
      Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
        this.difficulty = newDifficulty;
        setToSpawn(toSpawnInNextWave);
      });
    });
  }

  public enum WaveState {
    STARTING, PROGRESS, RESTARTING, ENDING
  }

}
