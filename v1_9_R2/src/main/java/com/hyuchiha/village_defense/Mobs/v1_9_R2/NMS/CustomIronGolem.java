/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Mobs.v1_9_R2.NMS;

import com.google.common.base.Predicate;
import com.hyuchiha.village_defense.Mobs.MobUtils;
import net.minecraft.server.v1_9_R2.*;

import java.util.List;

/**
 * @author hyuchiha
 */
public class CustomIronGolem extends EntityIronGolem {

    public CustomIronGolem(net.minecraft.server.v1_9_R2.World world) {
        super(world);
        List goalB = (List) MobUtils.getPrivateField("b", PathfinderGoalSelector.class, goalSelector);
        goalB.clear();
        List goalC = (List) MobUtils.getPrivateField("c", PathfinderGoalSelector.class, goalSelector);
        goalC.clear();
        List targetB = (List) MobUtils.getPrivateField("b", PathfinderGoalSelector.class, targetSelector);
        targetB.clear();
        List targetC = (List) MobUtils.getPrivateField("c", PathfinderGoalSelector.class, targetSelector);
        targetC.clear();

        this.goalSelector.a(1, new PathfinderGoalMeleeAttack(this, 1.0D, true));
        this.goalSelector.a(2, new PathfinderGoalMoveTowardsTarget(this, 0.9D, 32.0F));
        this.goalSelector.a(3, new PathfinderGoalMoveThroughVillage(this, 0.6D, true));
        this.goalSelector.a(4, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
        this.goalSelector.a(5, new PathfinderGoalOfferFlower(this));
        this.goalSelector.a(6, new PathfinderGoalRandomStroll(this, 0.6D));
        this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalDefendVillage(this));
        this.targetSelector.a(2, new PathfinderGoalHurtByTarget(this, false));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, 10, false, true, new Predicate() {
            public boolean a(EntityHuman entityinsentient) {
                return (entityinsentient != null);
            }

            @Override
            public boolean apply(Object object) {
                return true;
            }
        }));
    }
}
