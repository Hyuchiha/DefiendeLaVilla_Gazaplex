package com.hyuchiha.village_defense.Mobs;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;

public abstract class EnemyIA {

  private final EntityType entityType;
  private String customName = "";
  private final boolean boss;
  private boolean baby = false;
  private boolean villager = false;
  private boolean invisible = false;
  private final int minDroppedGold;
  private final int maxDroppedGold;
  private final int difficulty;
  private int startingWave = 1;
  private double Maxhealth = 50;

  private ItemStack weapon = null;
  private ItemStack[] armor = null;
  private PotionEffect[] potionEffects = null;

  public EnemyIA(EntityType entityType, int difficulty, int minDroppedGold, int maxDroppedGold) {
    this.entityType = entityType;
    this.difficulty = difficulty;
    this.minDroppedGold = minDroppedGold;
    this.maxDroppedGold = maxDroppedGold;
    this.boss = false;
  }

  public EnemyIA(EntityType entityType, int difficulty, int minDroppedGold, int maxDroppedGold, boolean boss) {
    this.entityType = entityType;
    this.difficulty = difficulty;
    this.minDroppedGold = minDroppedGold;
    this.maxDroppedGold = maxDroppedGold;
    this.boss = boss;
  }

  public EntityType getEntityType() {
    return entityType;
  }

  public int getDifficulty() {
    return difficulty;
  }

  public boolean isBoss() {
    return boss;
  }

  public String getCustomName() {
    return customName;
  }

  public void setMaxHealth(double health) {
    this.Maxhealth = health;
  }

  public void setCustomName(String customName) {
    this.customName = customName;
  }

  public boolean isBaby() {
    return baby;
  }

  public void setBaby(boolean baby) {
    this.baby = baby;
  }

  public boolean isZombieVillager() {
    return villager;
  }

  public void setZombieVillager(boolean villager) {
    this.villager = villager;
  }

  public ItemStack getWeaponItemStack() {
    return weapon;
  }

  public void setWeaponItemStack(ItemStack weapon) {
    this.weapon = weapon;
  }

  public ItemStack[] getArmorContents() {
    return armor;
  }

  public void setArmorContents(ItemStack[] armor) {
    this.armor = armor;
  }

  public void setArmorContents(ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots) {
    this.armor = new ItemStack[]{helmet, chestplate, leggings, boots};
  }

  public PotionEffect[] getPotionEffects() {
    return potionEffects;
  }

  public void setPotionEffects(PotionEffect[] potionEffects) {
    this.potionEffects = potionEffects;
  }

  public boolean isVillager() {
    return villager;
  }

  public int getMinDroppedGold() {
    return minDroppedGold;
  }

  public int getMaxDroppedGold() {
    return maxDroppedGold;
  }

  public ItemStack getWeapon() {
    return weapon;
  }

  public ItemStack[] getArmor() {
    return armor;
  }

  public boolean isInvisible() {
    return invisible;
  }

  public void setInvisible(boolean invisible) {
    this.invisible = invisible;
  }

  public double getMaxHealth() {
    return Maxhealth;
  }

  public int getStartingWave() {
    return startingWave;
  }

  public void setStartingWave(int startingWave) {
    this.startingWave = startingWave;
  }

  public abstract LivingEntity spawnEntity(Location spawnLocation, Plugin plugin, int wave);
}
