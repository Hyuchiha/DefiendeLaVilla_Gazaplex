package com.hyuchiha.village_defense.Mobs.v1_14_R1.Pathfinders;

import net.minecraft.server.v1_14_R1.EntityLiving;
import net.minecraft.server.v1_14_R1.EntitySpider;
import net.minecraft.server.v1_14_R1.PathfinderGoalNearestAttackableTarget;

public class PathfinderGoalSpiderNearestAttackableTarget<T extends EntityLiving> extends PathfinderGoalNearestAttackableTarget<T> {
  public PathfinderGoalSpiderNearestAttackableTarget(EntitySpider entityspider, Class<T> oclass) {
    super(entityspider, oclass, true);
  }

  public boolean a() {
    float f = this.e.aF();
    return f >= 0.5F ? false : super.a();
  }
}
