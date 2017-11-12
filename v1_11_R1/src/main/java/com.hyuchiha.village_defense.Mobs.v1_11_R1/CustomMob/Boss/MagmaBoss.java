/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Mobs.v1_11_R1.CustomMob.Boss;

import com.hyuchiha.village_defense.Mobs.BossEnemy;
import com.hyuchiha.village_defense.Mobs.v1_11_R1.CustomEntityType;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

/**
 *
 * @author hyuchiha
 */
public class MagmaBoss extends BossEnemy {

    public MagmaBoss(ConfigurationSection section) {
        super(CustomEntityType.CUSTOM_MAGMA_CUBE, section.getInt("min-gems"), section.getInt("max-gems"));

        super.setCustomName(section.getString("name"));
        super.setPotionEffects(new PotionEffect[]{new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 7), new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 2)});
    }

    @Override
    public LivingEntity spawnEntity(Location spawnLocation, Plugin plugin, int wave) {

        Entity e = getEntityType().spawnEntity(spawnLocation);

        if (!(e instanceof LivingEntity)) {
            e.remove();
        }

        Random random = new Random();
        LivingEntity entity = (LivingEntity) e;
        MagmaCube magmaBoss = (MagmaCube) entity;

        magmaBoss.setMetadata("gems", new FixedMetadataValue(plugin, (Math.max(getMinDroppedGold(), random.nextInt(getMaxDroppedGold()) + 1))));

        try {
            magmaBoss.setSize(5);
        } catch (Exception ex) {

        }

        return magmaBoss;
    }
}
