package com.hyuchiha.village_defense.Mobs.v1_11_R1.CustomMob;

import com.hyuchiha.village_defense.Mobs.CustomMob.ZombieMob;
import com.hyuchiha.village_defense.Mobs.v1_11_R1.CustomEntityType;
import org.bukkit.configuration.ConfigurationSection;

public class ZombieM extends ZombieMob {

    public ZombieM(ConfigurationSection section) {
        super(section, CustomEntityType.CUSTOM_ZOMBIE);
        setStartingWave(section.getInt("spawnAt"));
    }

}
