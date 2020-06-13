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
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

/**
 * @author hyuchiha
 */
public class WitchMob extends EnemyIA {

  public WitchMob(ConfigurationSection section, EntityType type) {
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
    org.bukkit.entity.Witch witch = (org.bukkit.entity.Witch) entity;

    witch.setMetadata("gems", new FixedMetadataValue(plugin, (Math.max(getMinDroppedGold(), random.nextInt(getMaxDroppedGold()) + 1))));

    if (wave > 10) {
      MobUtils.addRandomPotionEffects(wave, witch);
    }

    witch.setHealth(witch.getMaxHealth());
    witch.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3));

    return witch;
  }
}
