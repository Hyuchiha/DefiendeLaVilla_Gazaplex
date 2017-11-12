package com.hyuchiha.village_defense.Mobs;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public interface MobCreator {
    List<EnemyIA> createWaveMobs(ConfigurationSection configuration);
    List<BossEnemy> createBossMobs(ConfigurationSection configuration);
    List<BossEnemy> createSpecialMobs();
}
