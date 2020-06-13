/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Mobs.v1_10_R1.NMS;

import com.hyuchiha.village_defense.Mobs.MobUtils;
import net.minecraft.server.v1_10_R1.*;

import java.util.Set;

/**
 * @author hyuchiha
 */
public class CustomWitch extends EntityWitch {

  public CustomWitch(World world) {
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
    this.goalSelector.a(2, new PathfinderGoalArrowAttack(this, 1.0, 60, 10.0f));
    this.goalSelector.a(2, new PathfinderGoalRandomStroll(this, 1.0));
    this.goalSelector.a(3, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0f));
    this.goalSelector.a(3, new PathfinderGoalRandomLookaround(this));
    this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, false));
    this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true, true));
    this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityVillager.class, true, true));
  }
}
