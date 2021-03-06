package com.hyuchiha.village_defense.Mobs.v1_14_R1.NMS;

import com.hyuchiha.village_defense.Mobs.MobUtils;
import net.minecraft.server.v1_14_R1.*;

import java.util.Set;

/**
 * @author hyuchiha
 */
public class CustomIronGolem extends EntityIronGolem {

  public CustomIronGolem(World world) {
    super(EntityTypes.IRON_GOLEM, world);
    Set goalB = (Set) MobUtils.getPrivateField("d", PathfinderGoalSelector.class, goalSelector);
    goalB.clear();

    Set targetB = (Set) MobUtils.getPrivateField("d", PathfinderGoalSelector.class, targetSelector);
    targetB.clear();

    this.goalSelector.a(1, new PathfinderGoalMeleeAttack(this, 1.0D, true));
    this.goalSelector.a(2, new PathfinderGoalMoveTowardsTarget(this, 0.9D, 32.0F));
    this.goalSelector.a(3, new PathfinderGoalMoveThroughVillage(this, 0.6D, false, 4, () -> {
      return false;
    }));
    this.goalSelector.a(4, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
    this.goalSelector.a(5, new PathfinderGoalOfferFlower(this));
    this.goalSelector.a(6, new PathfinderGoalRandomStroll(this, 0.6D));
    this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
    this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
    this.targetSelector.a(1, new PathfinderGoalDefendVillage(this));
    this.targetSelector.a(2, new PathfinderGoalHurtByTarget(this, new Class[0]));
    this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget(this, EntityInsentient.class, 5, false, false, (entityliving) -> {
      return entityliving instanceof IMonster && !(entityliving instanceof EntityCreeper);
    }));
  }
}
