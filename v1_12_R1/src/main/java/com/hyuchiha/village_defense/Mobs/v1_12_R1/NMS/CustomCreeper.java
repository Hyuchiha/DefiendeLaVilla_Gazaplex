/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Mobs.v1_12_R1.NMS;

import com.hyuchiha.village_defense.Mobs.MobUtils;
import net.minecraft.server.v1_12_R1.*;

import java.util.Set;

/**
 * @author hyuchiha
 */
public class CustomCreeper extends EntityCreeper {

  public CustomCreeper(World world) {
    super(world);
    Set goalB = (Set) MobUtils.getPrivateField("b", PathfinderGoalSelector.class, goalSelector);
    goalB.clear();
    Set goalC = (Set) MobUtils.getPrivateField("c", PathfinderGoalSelector.class, goalSelector);
    goalC.clear();
    Set targetB = (Set) MobUtils.getPrivateField("b", PathfinderGoalSelector.class, targetSelector);
    targetB.clear();
    Set targetC = (Set) MobUtils.getPrivateField("c", PathfinderGoalSelector.class, targetSelector);
    targetC.clear();

    this.goalSelector.a(1, new PathfinderGoalFloat(this));
    this.goalSelector.a(2, new PathfinderGoalSwell(this));
    this.goalSelector.a(3, new PathfinderGoalAvoidTarget(this, EntityOcelot.class, 6.0f, 1.0, 1.2));
    this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, 1.0, false));
    this.goalSelector.a(5, new PathfinderGoalRandomStroll(this, 0.8));
    this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0f));
    this.goalSelector.a(6, new PathfinderGoalRandomLookaround(this));
    this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget(this, EntityVillager.class, true));
    this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));
    this.targetSelector.a(2, new PathfinderGoalHurtByTarget(this, false));
  }
}
