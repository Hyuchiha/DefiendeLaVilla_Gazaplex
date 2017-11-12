package com.hyuchiha.village_defense.Mobs.v1_12_R1.CustomMob;

import com.hyuchiha.village_defense.Mobs.CustomMob.Tank;
import com.hyuchiha.village_defense.Mobs.v1_12_R1.CustomEntityType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class TankM extends Tank {

    public TankM(ConfigurationSection section) {
        super(section, CustomEntityType.CUSTOM_IRONGOLEM);

        setStartingWave(section.getInt("spawnAt"));
        super.setPotionEffects(new PotionEffect[]{new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1), new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1), new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1)});
    }

}
