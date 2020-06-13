package com.hyuchiha.village_defense.Mobs.v1_10_R1;

import com.hyuchiha.village_defense.Mobs.CustomMob.SkeletonMob;
import com.hyuchiha.village_defense.Mobs.EntityType;
import com.hyuchiha.village_defense.Mobs.MobUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.Random;

public class WitherSkullMob extends SkeletonMob {

  public WitherSkullMob(ConfigurationSection section, EntityType type) {
    super(section, type);
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
    org.bukkit.entity.Skeleton skeleton = (org.bukkit.entity.Skeleton) entity;

    skeleton.setMetadata("gems", new FixedMetadataValue(plugin, (Math.max(getMinDroppedGold(), random.nextInt(getMaxDroppedGold()) + 1))));

    int difficultyOfMob = (wave / 10);

    if (wave > 10) {
      ItemStack[] armor = MobUtils.getRandomArmor(difficultyOfMob);
      skeleton.getEquipment().setHelmet(armor[0]);
      skeleton.getEquipment().setChestplate(armor[1]);
      skeleton.getEquipment().setLeggings(armor[2]);
      skeleton.getEquipment().setBoots(armor[3]);
    }

    skeleton.getEquipment().setItemInHand(MobUtils.addRandonBowEnchantments(new ItemStack(Material.BOW), difficultyOfMob));

    skeleton.getEquipment().setHelmetDropChance(0F);
    skeleton.getEquipment().setChestplateDropChance(0F);
    skeleton.getEquipment().setLeggingsDropChance(0F);
    skeleton.getEquipment().setBootsDropChance(0F);
    skeleton.getEquipment().setItemInHandDropChance(0F);
    skeleton.setFireTicks(0);
    skeleton.setHealth(skeleton.getMaxHealth());
    skeleton.setCanPickupItems(false);
    skeleton.setSkeletonType(Skeleton.SkeletonType.WITHER);

    return skeleton;
  }
}
