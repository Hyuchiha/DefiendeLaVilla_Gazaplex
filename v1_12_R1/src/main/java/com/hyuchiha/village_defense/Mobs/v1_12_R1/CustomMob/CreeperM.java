/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Mobs.v1_12_R1.CustomMob;

import com.hyuchiha.village_defense.Mobs.CustomMob.CreeperMob;
import com.hyuchiha.village_defense.Mobs.v1_12_R1.CustomEntityType;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author hyuchiha
 */
public class CreeperM extends CreeperMob {

    public CreeperM(ConfigurationSection section) {
        super(section, CustomEntityType.CUSTOM_CREEPER);
        setStartingWave(section.getInt("spawnAt"));
    }

}
