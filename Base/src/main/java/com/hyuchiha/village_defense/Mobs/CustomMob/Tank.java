package com.hyuchiha.village_defense.Mobs.CustomMob;

import com.hyuchiha.village_defense.Mobs.EnemyIA;
import com.hyuchiha.village_defense.Mobs.EntityType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class Tank extends EnemyIA {

  public Tank(ConfigurationSection section, EntityType type) {
    super(type, section.getInt("difficulty"), section.getInt("min-gems"), section.getInt("max-gems"));

    setStartingWave(section.getInt("spawnAt"));
    super.setPotionEffects(new PotionEffect[]{new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1), new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1), new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1)});
  }

  @Override
  public LivingEntity spawnEntity(Location spawnLocation, Plugin plugin, int wave) {

    Entity e = getEntityType().spawnEntity(spawnLocation);

    if (!(e instanceof LivingEntity)) {
      e.remove();
    }

    Random random = new Random();
    LivingEntity entity = (LivingEntity) e;

    entity.setMetadata("gems", new FixedMetadataValue(plugin, (Math.max(getMinDroppedGold(), random.nextInt(getMaxDroppedGold()) + 1))));

    if (isBoss()) {
      entity.setCustomName(ChatColor.YELLOW + "[" + ChatColor.BLUE + "BOSS" + ChatColor.YELLOW + "] " + ChatColor.RESET + getCustomName());
      entity.setCustomNameVisible(true);
    }

    if (getArmor() != null) {
      entity.getEquipment().setArmorContents(getArmor().clone());
    }

    if (getWeapon() != null) {
      entity.getEquipment().setItemInHand(getWeapon().clone());
    }

    entity.getEquipment().setHelmetDropChance(0F);
    entity.getEquipment().setChestplateDropChance(0F);
    entity.getEquipment().setLeggingsDropChance(0F);
    entity.getEquipment().setBootsDropChance(0F);
    entity.getEquipment().setItemInHandDropChance(0F);

    if (getPotionEffects() != null) {
      for (PotionEffect effect : getPotionEffects()) {
        entity.addPotionEffect(effect);
      }
    }

    if (entity instanceof org.bukkit.entity.Zombie) {
      ((org.bukkit.entity.Zombie) entity).setBaby(isBaby());
      ((org.bukkit.entity.Zombie) entity).setVillager(isVillager());
    } else if (entity instanceof PigZombie) {
      ((PigZombie) entity).setBaby(isBaby());
    }


    entity.setFireTicks(0);
    entity.setHealth(entity.getMaxHealth());
    entity.setCanPickupItems(false);

    return entity;
  }

}
