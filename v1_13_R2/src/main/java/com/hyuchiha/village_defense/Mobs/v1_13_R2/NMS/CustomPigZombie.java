package com.hyuchiha.village_defense.Mobs.v1_13_R2.NMS;

import com.hyuchiha.village_defense.Mobs.MobUtils;
import net.minecraft.server.v1_13_R2.*;

import java.util.Set;

/**
 * @author hyuchiha
 */
public class CustomPigZombie extends EntityPigZombie {

  public CustomPigZombie(World world) {
    super(world);
    Set goalB = (Set) MobUtils.getPrivateField("b", PathfinderGoalSelector.class, goalSelector);
    goalB.clear();
    Set goalC = (Set) MobUtils.getPrivateField("c", PathfinderGoalSelector.class, goalSelector);
    goalC.clear();
    Set targetB = (Set) MobUtils.getPrivateField("b", PathfinderGoalSelector.class, targetSelector);
    targetB.clear();
    Set targetC = (Set) MobUtils.getPrivateField("c", PathfinderGoalSelector.class, targetSelector);
    targetC.clear();

    this.goalSelector.a(0, new PathfinderGoalFloat(this));
    this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, 1.0D, false));
    this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, 1.0D, true));
    this.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 6.0D));
    this.goalSelector.a(6, new PathfinderGoalMoveThroughVillage(this, 1.0D, false));
    this.goalSelector.a(7, new PathfinderGoalRandomStroll(this, 1.0D));
    this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
    this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
    this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, true));
    this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityVillager.class, true, true));
    this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true, false));
  }
}
