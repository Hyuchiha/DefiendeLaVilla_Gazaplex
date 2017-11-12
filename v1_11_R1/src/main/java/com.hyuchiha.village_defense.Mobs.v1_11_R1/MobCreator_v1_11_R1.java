package com.hyuchiha.village_defense.Mobs.v1_11_R1;

import com.hyuchiha.village_defense.Mobs.BossEnemy;
import com.hyuchiha.village_defense.Mobs.EnemyIA;
import com.hyuchiha.village_defense.Mobs.MobCreator;
import com.hyuchiha.village_defense.Mobs.v1_11_R1.CustomMob.*;
import com.hyuchiha.village_defense.Mobs.v1_11_R1.CustomMob.Boss.MagmaBoss;
import com.hyuchiha.village_defense.Mobs.v1_11_R1.CustomMob.Boss.SlimeBoss;
import com.hyuchiha.village_defense.Mobs.v1_11_R1.CustomMob.SpecialMobs.GiantMob;
import com.hyuchiha.village_defense.Mobs.v1_11_R1.CustomMob.SpecialMobs.WitherMob;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class MobCreator_v1_11_R1 implements MobCreator {
    @Override
    public List<EnemyIA> createWaveMobs(ConfigurationSection config) {
        List<EnemyIA> mobs = new ArrayList<>();

        mobs.add(new ZombieMob(config.getConfigurationSection("ZombieMob")));
        mobs.add(new PigmanMob(config.getConfigurationSection("PigmanMob")));
        mobs.add(new SkeletonMob(config.getConfigurationSection("SkeletonMob")));
        mobs.add(new SpiderMob(config.getConfigurationSection("SpiderMob")));
        mobs.add(new SpiderCaveMob(config.getConfigurationSection("SpiderCaveMob")));
        mobs.add(new WitchMob(config.getConfigurationSection("WitchMob")));
        mobs.add(new WhiterSkullMob(config.getConfigurationSection("WhiterSkullMob")));
        mobs.add(new CreeperMob(config.getConfigurationSection("CreeperMob")));
        mobs.add(new CreeperChargedMob(config.getConfigurationSection("CreeperChargedMob")));
        mobs.add(new Tank(config.getConfigurationSection("Tank")));

        return mobs;
    }

    @Override
    public List<BossEnemy> createBossMobs(ConfigurationSection config) {
        List<BossEnemy> bosses = new ArrayList<>();

        bosses.add(new MagmaBoss(config.getConfigurationSection("MagmaBoss")));
        bosses.add(new SlimeBoss(config.getConfigurationSection("SlimeBoss")));

        return bosses;
    }

    @Override
    public List<BossEnemy> createSpecialMobs() {
        List<BossEnemy> specialsMob = new ArrayList<>();

        specialsMob.add(new GiantMob());
        specialsMob.add(new WitherMob());

        return specialsMob;
    }
}
