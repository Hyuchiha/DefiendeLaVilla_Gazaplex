package com.hyuchiha.village_defense.Mobs.v1_12_R1;

import com.hyuchiha.village_defense.Mobs.BossEnemy;
import com.hyuchiha.village_defense.Mobs.EnemyIA;
import com.hyuchiha.village_defense.Mobs.MobCreator;
import com.hyuchiha.village_defense.Mobs.v1_12_R1.CustomMob.Boss.MagmaB;
import com.hyuchiha.village_defense.Mobs.v1_12_R1.CustomMob.Boss.SlimeB;
import com.hyuchiha.village_defense.Mobs.v1_12_R1.CustomMob.*;
import com.hyuchiha.village_defense.Mobs.v1_12_R1.CustomMob.SpecialMobs.GiantSp;
import com.hyuchiha.village_defense.Mobs.v1_12_R1.CustomMob.SpecialMobs.WitherSp;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class MobCreator_v1_12_R1 implements MobCreator {
    @Override
    public List<EnemyIA> createWaveMobs(ConfigurationSection config) {
        List<EnemyIA> mobs = new ArrayList<>();

        mobs.add(new ZombieM(config.getConfigurationSection("ZombieMob")));
        mobs.add(new PigmanM(config.getConfigurationSection("PigmanMob")));
        mobs.add(new SkeletonM(config.getConfigurationSection("SkeletonMob")));
        mobs.add(new SpiderM(config.getConfigurationSection("SpiderMob")));
        mobs.add(new SpiderCaveM(config.getConfigurationSection("SpiderCaveMob")));
        mobs.add(new WitchM(config.getConfigurationSection("WitchMob")));
        mobs.add(new WhiterSkullM(config.getConfigurationSection("WitherSkullMob")));
        mobs.add(new CreeperM(config.getConfigurationSection("CreeperMob")));
        mobs.add(new CreeperChargedM(config.getConfigurationSection("CreeperChargedMob")));
        mobs.add(new TankM(config.getConfigurationSection("Tank")));

        return mobs;
    }

    @Override
    public List<BossEnemy> createBossMobs(ConfigurationSection config) {
        List<BossEnemy> bosses = new ArrayList<>();

        bosses.add(new MagmaB(config.getConfigurationSection("MagmaBoss")));
        bosses.add(new SlimeB(config.getConfigurationSection("SlimeBoss")));

        return bosses;
    }

    @Override
    public List<BossEnemy> createSpecialMobs() {
        List<BossEnemy> specialsMob = new ArrayList<>();

        specialsMob.add(new GiantSp());
        specialsMob.add(new WitherSp());

        return specialsMob;
    }
}
