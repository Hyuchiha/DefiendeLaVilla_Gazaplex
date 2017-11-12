/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Mobs.v1_11_R1.CustomMob.Boss;

import com.hyuchiha.village_defense.Mobs.CustomMob.Boss.MagmaBoss;
import com.hyuchiha.village_defense.Mobs.v1_11_R1.CustomEntityType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author hyuchiha
 */
public class MagmaB extends MagmaBoss {

    public MagmaB(ConfigurationSection section) {
        super(section, CustomEntityType.CUSTOM_MAGMA_CUBE);

        super.setCustomName(section.getString("name"));
        super.setPotionEffects(new PotionEffect[]{new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 7), new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 2)});
    }
}
