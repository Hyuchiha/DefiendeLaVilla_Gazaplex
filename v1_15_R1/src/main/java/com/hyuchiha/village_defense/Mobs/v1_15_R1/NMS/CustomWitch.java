package com.hyuchiha.village_defense.Mobs.v1_15_R1.NMS;

import com.hyuchiha.village_defense.Mobs.MobUtils;
import net.minecraft.server.v1_15_R1.*;

import java.util.Set;

/**
 * @author hyuchiha
 */
public class CustomWitch extends EntityWitch {

  public CustomWitch(World world) {
    super(EntityTypes.WITCH, world);
    Set goalB = (Set) MobUtils.getPrivateField("d", PathfinderGoalSelector.class, goalSelector);
    goalB.clear();

    Set targetB = (Set) MobUtils.getPrivateField("d", PathfinderGoalSelector.class, targetSelector);
    targetB.clear();

    this.goalSelector.a(1, new PathfinderGoalFloat(this));
    this.goalSelector.a(2, new PathfinderGoalArrowAttack(this, 1.0, 60, 10.0f));
    this.goalSelector.a(2, new PathfinderGoalRandomStroll(this, 1.0));
    this.goalSelector.a(3, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0f));
    this.goalSelector.a(3, new PathfinderGoalRandomLookaround(this));
    this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, new Class[]{EntityRaider.class}));
    this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true, true));
    this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityVillager.class, true, true));
  }
}
