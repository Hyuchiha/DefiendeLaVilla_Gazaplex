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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.Random;

/**
 *
 * @author hyuchiha
 */
public class CreeperChargedMob extends EnemyIA {
    
    public CreeperChargedMob(ConfigurationSection section) {
        super(CustomEntityType.CUSTOM_CREEPER, section.getInt("difficulty"), section.getInt("min-gems"), section.getInt("max-gems"));
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
        Creeper creeper = (Creeper) entity;

        creeper.setPowered(true);
        creeper.setMetadata("gems", new FixedMetadataValue(plugin, (Math.max(getMinDroppedGold(), random.nextInt(getMaxDroppedGold()) + 1))));

        if (wave > 10) {
            MobUtils.addRandomPotionEffects(wave, creeper);
        }

        creeper.setHealth(creeper.getMaxHealth());

        return creeper;
    }


}
