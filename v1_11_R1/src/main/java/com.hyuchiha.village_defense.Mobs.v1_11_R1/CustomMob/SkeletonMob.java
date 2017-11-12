/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Mobs.v1_11_R1.CustomMob;

import com.hyuchiha.village_defense.Mobs.EnemyIA;
import com.hyuchiha.village_defense.Mobs.MobUtils;
import com.hyuchiha.village_defense.Mobs.v1_11_R1.CustomEntityType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.Random;

/**
 *
 * @author hyuchiha
 */
public class SkeletonMob extends EnemyIA {

    public SkeletonMob(ConfigurationSection section) {
        super(CustomEntityType.CUSTOM_SKELETON, section.getInt("difficulty"), section.getInt("min-gems"), section.getInt("max-gems"));
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

        int difficultyOfMob = (wave/10);
        
        if(wave > 10){
            skeleton.getEquipment().setArmorContents(MobUtils.getRandomArmor(difficultyOfMob));
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

        return skeleton;
    }

}
