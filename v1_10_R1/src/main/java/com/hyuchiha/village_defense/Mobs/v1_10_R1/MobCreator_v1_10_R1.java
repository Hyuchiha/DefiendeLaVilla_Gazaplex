package com.hyuchiha.village_defense.Mobs.v1_10_R1;

import com.hyuchiha.village_defense.Mobs.BossEnemy;
import com.hyuchiha.village_defense.Mobs.CustomMob.Boss.MagmaBoss;
import com.hyuchiha.village_defense.Mobs.CustomMob.Boss.SlimeBoss;
import com.hyuchiha.village_defense.Mobs.CustomMob.*;
import com.hyuchiha.village_defense.Mobs.CustomMob.SpecialMobs.GiantMob;
import com.hyuchiha.village_defense.Mobs.CustomMob.SpecialMobs.WitherMob;
import com.hyuchiha.village_defense.Mobs.EnemyIA;
import com.hyuchiha.village_defense.Mobs.MobCreator;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class MobCreator_v1_10_R1 implements MobCreator {
    @Override
    public List<EnemyIA> createWaveMobs(ConfigurationSection config) {
        List<EnemyIA> mobs = new ArrayList<>();

        mobs.add(new ZombieMob(config.getConfigurationSection("Zombie"), CustomEntityType.CUSTOM_ZOMBIE));
        mobs.add(new PigmanMob(config.getConfigurationSection("Pigman"), CustomEntityType.CUSTOM_PIGZOMBIE));
        mobs.add(new SkeletonMob(config.getConfigurationSection("Skeleton"), CustomEntityType.CUSTOM_SKELETON));
        mobs.add(new SpiderMob(config.getConfigurationSection("Spider"), CustomEntityType.CUSTOM_SPIDER));
        mobs.add(new SpiderCaveMob(config.getConfigurationSection("SpiderCave"), CustomEntityType.CUSTOM_CAVESPIDER));
        mobs.add(new WitchMob(config.getConfigurationSection("Witch"), CustomEntityType.CUSTOM_WITCH));
        mobs.add(new WitherSkullMob(config.getConfigurationSection("WitherSkull"), CustomEntityType.CUSTOM_SKELETON));
        mobs.add(new CreeperMob(config.getConfigurationSection("Creeper"), CustomEntityType.CUSTOM_CREEPER));
        mobs.add(new CreeperChargedMob(config.getConfigurationSection("CreeperCharged"), CustomEntityType.CUSTOM_CREEPER));
        mobs.add(new Tank(config.getConfigurationSection("Tank"), CustomEntityType.CUSTOM_IRONGOLEM));

        return mobs;
    }

    @Override
    public List<BossEnemy> createBossMobs(ConfigurationSection config) {
        List<BossEnemy> bosses = new ArrayList<>();

        bosses.add(new MagmaBoss(config.getConfigurationSection("MagmaBoss"), CustomEntityType.CUSTOM_MAGMA_CUBE));
        bosses.add(new SlimeBoss(config.getConfigurationSection("SlimeBoss"), CustomEntityType.CUSTOM_SLIME));

        return bosses;
    }

    @Override
    public List<BossEnemy> createSpecialMobs() {
        List<BossEnemy> specialsMob = new ArrayList<>();

        specialsMob.add(new GiantMob(CustomEntityType.CUSTOM_GIANT));
        specialsMob.add(new WitherMob(CustomEntityType.CUSTOM_WITHER));

        return specialsMob;
    }
}
