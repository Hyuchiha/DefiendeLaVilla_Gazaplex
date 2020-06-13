/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Mobs.v1_11_R1.NMS;

import net.minecraft.server.v1_11_R1.EntitySkeleton;

/**
 * @author hyuchiha
 */
public class CustomSkeleton extends EntitySkeleton {

    public CustomSkeleton(net.minecraft.server.v1_11_R1.World world) {
        super(world);
        /**
         List goalB = (List) MobUtils.getPrivateField("b", PathfinderGoalSelector.class, goalSelector);
         goalB.clear();
         List goalC = (List) MobUtils.getPrivateField("c", PathfinderGoalSelector.class, goalSelector);
         goalC.clear();
         List targetB = (List) MobUtils.getPrivateField("b", PathfinderGoalSelector.class, targetSelector);
         targetB.clear();
         List targetC = (List) MobUtils.getPrivateField("c", PathfinderGoalSelector.class, targetSelector);
         targetC.clear();

         this.goalSelector.a(1, new PathfinderGoalFloat(this));
         this.goalSelector.a(3, new PathfinderGoalAvoidTarget(this, EntityWolf.class, 6.0F, 1.0D, 1.2D));
         this.goalSelector.a(4, new PathfinderGoalRandomStroll(this, 5.0D));
         this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 2.0F));
         this.goalSelector.a(6, new PathfinderGoalRandomLookaround(this));
         this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, false, new Class[0]));
         this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityVillager.class, true));
         this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));
         */
    }
}
