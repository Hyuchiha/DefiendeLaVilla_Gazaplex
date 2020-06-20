package com.hyuchiha.village_defense.Mobs.v1_14_R1.NMS;

import com.hyuchiha.village_defense.Mobs.MobUtils;
import com.hyuchiha.village_defense.Mobs.v1_14_R1.Pathfinders.PathfinderGoalSpiderMeleeAttack;
import com.hyuchiha.village_defense.Mobs.v1_14_R1.Pathfinders.PathfinderGoalSpiderNearestAttackableTarget;
import net.minecraft.server.v1_14_R1.*;

import java.util.Set;

/**
 * @author hyuchiha
 */
public class CustomSpider extends EntitySpider {

  public CustomSpider(World world) {
    super(EntityTypes.SPIDER, world);
    Set goalB = (Set) MobUtils.getPrivateField("d", PathfinderGoalSelector.class, goalSelector);
    goalB.clear();

    Set targetB = (Set) MobUtils.getPrivateField("d", PathfinderGoalSelector.class, targetSelector);
    targetB.clear();

    this.goalSelector.a(1, new PathfinderGoalFloat(this));
    this.goalSelector.a(3, new PathfinderGoalLeapAtTarget(this, 0.4F));
    this.goalSelector.a(4, new PathfinderGoalSpiderMeleeAttack(this));
    this.goalSelector.a(5, new PathfinderGoalRandomStrollLand(this, 0.8D));
    this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
    this.goalSelector.a(6, new PathfinderGoalRandomLookaround(this));
    this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, new Class[0]));
    this.targetSelector.a(2, new PathfinderGoalSpiderNearestAttackableTarget(this, EntityVillager.class));
    this.targetSelector.a(3, new PathfinderGoalSpiderNearestAttackableTarget(this, EntityHuman.class));
  }
}
