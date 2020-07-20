package com.hyuchiha.village_defense.Mobs.v1_15_R1.NMS;

import com.hyuchiha.village_defense.Mobs.MobUtils;
import net.minecraft.server.v1_15_R1.*;

import java.util.Set;

/**
 * @author hyuchiha
 */
public class CustomPigZombie extends EntityPigZombie {

  public CustomPigZombie(World world) {
    super(EntityTypes.ZOMBIE_PIGMAN, world);

    Set goalB = (Set) MobUtils.getPrivateField("d", PathfinderGoalSelector.class, goalSelector);
    goalB.clear();
    Set targetB = (Set) MobUtils.getPrivateField("d", PathfinderGoalSelector.class, targetSelector);
    targetB.clear();

    this.goalSelector.a(0, new PathfinderGoalFloat(this));
    this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, 1.0D, false));
    this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, 1.0D, true));
    this.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 6.0D));
    this.goalSelector.a(6, new PathfinderGoalMoveThroughVillage(this, 1.0D, true, 4, this::ey));
    this.goalSelector.a(7, new PathfinderGoalRandomStroll(this, 1.0D));
    this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
    this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
    this.targetSelector.a(1, (new PathfinderGoalHurtByTarget(this, new Class[0])).a(new Class[]{EntityPigZombie.class}));
    this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityVillager.class, true, true));
    this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true, false));
  }
}
