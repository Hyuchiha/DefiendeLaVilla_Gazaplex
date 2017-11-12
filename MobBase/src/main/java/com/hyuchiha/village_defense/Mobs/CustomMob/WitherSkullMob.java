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

import java.util.Random;

/**
 * @author hyuchiha
 */
public class WitherSkullMob extends EnemyIA {

    public WitherSkullMob(ConfigurationSection section, EntityType type) {
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
        org.bukkit.entity.WitherSkeleton skeleton = (org.bukkit.entity.WitherSkeleton) entity;

        skeleton.setMetadata("gems", new FixedMetadataValue(plugin, (Math.max(getMinDroppedGold(), random.nextInt(getMaxDroppedGold()) + 1))));

        int difficultyOfMob = (wave / 10);

        if (wave > 5) {
            skeleton.getEquipment().setArmorContents(MobUtils.getRandomArmor(difficultyOfMob));
            MobUtils.addRandomPotionEffects(wave, skeleton);
        }

        skeleton.getEquipment().setItemInHand(MobUtils.getRandomWeapon(difficultyOfMob));

        skeleton.getEquipment().setHelmetDropChance(0F);
        skeleton.getEquipment().setChestplateDropChance(0F);
        skeleton.getEquipment().setLeggingsDropChance(0F);
        skeleton.getEquipment().setBootsDropChance(0F);
        skeleton.getEquipment().setItemInHandDropChance(0F);
        skeleton.setFireTicks(0);
        skeleton.setHealth(skeleton.getMaxHealth());
        skeleton.setCanPickupItems(false);

        return skeleton;
    }

}
