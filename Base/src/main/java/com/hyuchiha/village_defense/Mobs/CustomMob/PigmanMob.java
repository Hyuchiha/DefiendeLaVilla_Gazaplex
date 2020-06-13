/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Mobs.CustomMob;

import com.hyuchiha.village_defense.Mobs.EnemyIA;
import com.hyuchiha.village_defense.Mobs.EntityType;
import com.hyuchiha.village_defense.Mobs.MobUtils;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

/**
 * @author hyuchiha
 */
public class PigmanMob extends EnemyIA {

  public PigmanMob(ConfigurationSection section, EntityType type) {
    super(type, section.getInt("difficulty"), section.getInt("min-gems"), section.getInt("max-gems"));
    setStartingWave(section.getInt("spawnAt"));
  }

  @Override
  public LivingEntity spawnEntity(Location spawnLocation, Plugin plugin, int wave) {

    Entity e = getEntityType().spawnEntity(spawnLocation);

    if (!(e instanceof LivingEntity)) {
      e.remove();
    }

    Random random = new Random();
    LivingEntity entity = (LivingEntity) e;
    PigZombie pig = ((PigZombie) entity);

    pig.setMetadata("gems", new FixedMetadataValue(plugin, (Math.max(getMinDroppedGold(), random.nextInt(getMaxDroppedGold()) + 1))));

    int difficultyOfMob = (wave / 10);

    if (wave > 5) {
      ItemStack[] armor = MobUtils.getRandomArmor(difficultyOfMob);
      pig.getEquipment().setHelmet(armor[0]);
      pig.getEquipment().setChestplate(armor[1]);
      pig.getEquipment().setLeggings(armor[2]);
      pig.getEquipment().setBoots(armor[3]);
    }

    if (random.nextBoolean()) {
      pig.getEquipment().setItemInHand(MobUtils.getRandomWeapon(difficultyOfMob));
    }

    pig.getEquipment().setHelmetDropChance(0F);
    pig.getEquipment().setChestplateDropChance(0F);
    pig.getEquipment().setLeggingsDropChance(0F);
    pig.getEquipment().setBootsDropChance(0F);
    pig.getEquipment().setItemInHandDropChance(0F);

    if (wave > 5) {
      MobUtils.addRandomPotionEffects(wave, pig);
    }

    if (random.nextDouble() < .2) {
      pig.setBaby(random.nextBoolean());
      pig.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
      pig.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1));
    }
    pig.setAngry(true);

    pig.setFireTicks(0);
    pig.setHealth(pig.getMaxHealth());
    pig.setCanPickupItems(false);

    return pig;
  }

}
