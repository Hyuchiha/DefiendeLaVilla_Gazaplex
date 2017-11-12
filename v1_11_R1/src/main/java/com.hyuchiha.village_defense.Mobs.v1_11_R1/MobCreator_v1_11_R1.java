package com.hyuchiha.village_defense.Mobs.v1_11_R1;

import com.hyuchiha.village_defense.Mobs.BossEnemy;
import com.hyuchiha.village_defense.Mobs.EnemyIA;
import com.hyuchiha.village_defense.Mobs.MobCreator;
import com.hyuchiha.village_defense.Mobs.v1_11_R1.CustomMob.Boss.MagmaB;
import com.hyuchiha.village_defense.Mobs.v1_11_R1.CustomMob.Boss.SlimeB;
import com.hyuchiha.village_defense.Mobs.v1_11_R1.CustomMob.*;
import com.hyuchiha.village_defense.Mobs.v1_11_R1.CustomMob.SpecialMobs.GiantSp;
import com.hyuchiha.village_defense.Mobs.v1_11_R1.CustomMob.SpecialMobs.WitherSp;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class MobCreator_v1_11_R1 implements MobCreator {
    @Override
    public List<EnemyIA> createWaveMobs(ConfigurationSection config) {
        List<EnemyIA> mobs = new ArrayList<>();

        mobs.add(new ZombieM(config.getConfigurationSection("ZombieM")));
        mobs.add(new PigmanM(config.getConfigurationSection("PigmanM")));
        mobs.add(new SkeletonM(config.getConfigurationSection("SkeletonM")));
        mobs.add(new SpiderM(config.getConfigurationSection("SpiderM")));
        mobs.add(new SpiderCaveM(config.getConfigurationSection("SpiderCaveM")));
        mobs.add(new WitchM(config.getConfigurationSection("WitchM")));
        mobs.add(new WhiterSkullM(config.getConfigurationSection("WhiterSkullM")));
        mobs.add(new CreeperM(config.getConfigurationSection("CreeperM")));
        mobs.add(new CreeperChargedM(config.getConfigurationSection("CreeperChargedM")));
        mobs.add(new TankM(config.getConfigurationSection("TankM")));

        return mobs;
    }

    @Override
    public List<BossEnemy> createBossMobs(ConfigurationSection config) {
        List<BossEnemy> bosses = new ArrayList<>();

        bosses.add(new MagmaB(config.getConfigurationSection("MagmaB")));
        bosses.add(new SlimeB(config.getConfigurationSection("SlimeB")));

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
