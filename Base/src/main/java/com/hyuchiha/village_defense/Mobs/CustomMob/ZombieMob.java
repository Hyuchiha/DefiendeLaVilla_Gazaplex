package com.hyuchiha.village_defense.Mobs.CustomMob;

import com.hyuchiha.village_defense.Mobs.EnemyIA;
import com.hyuchiha.village_defense.Mobs.EntityType;
import com.hyuchiha.village_defense.Mobs.MobUtils;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class ZombieMob extends EnemyIA {

  public ZombieMob(ConfigurationSection section, EntityType type) {
    super(type, section.getInt("difficulty"), section.getInt("min-gems"), section.getInt("max-gems"));
    setStartingWave(section.getInt("spawnAt"));
  }

  @Override
  public LivingEntity spawnEntity(Location spawnLocation, Plugin plugin, int wave) {

    Entity e = getEntityType().spawnEntity(spawnLocation);

    if (!(e instanceof LivingEntity)) {
      e.remove();
    }

    int difficultyOfMob = (wave / 10);

    Random random = new Random();
    LivingEntity entity = (LivingEntity) e;
    org.bukkit.entity.Zombie zombie = (org.bukkit.entity.Zombie) entity;

    zombie.setMetadata("gems", new FixedMetadataValue(plugin, (Math.max(getMinDroppedGold(), random.nextInt(getMaxDroppedGold()) + 1))));

    if (wave > 10) {
      MobUtils.addRandomPotionEffects(wave, zombie);
      ItemStack[] armor = MobUtils.getRandomArmor(difficultyOfMob);
      zombie.getEquipment().setHelmet(armor[0]);
      zombie.getEquipment().setChestplate(armor[1]);
      zombie.getEquipment().setLeggings(armor[2]);
      zombie.getEquipment().setBoots(armor[3]);
      zombie.getEquipment().setItemInHand(MobUtils.getRandomWeapon(difficultyOfMob));
    }

    zombie.getEquipment().setHelmetDropChance(0F);
    zombie.getEquipment().setChestplateDropChance(0F);
    zombie.getEquipment().setLeggingsDropChance(0F);
    zombie.getEquipment().setBootsDropChance(0F);
    zombie.getEquipment().setItemInHandDropChance(0F);
    zombie.setFireTicks(0);
    zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3));
    zombie.setHealth(zombie.getMaxHealth());
    zombie.setCanPickupItems(false);

    return zombie;
  }
}
