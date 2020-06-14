package com.hyuchiha.village_defense.Mobs.v1_14_R1.NMS;

import net.minecraft.server.v1_14_R1.EntitySkeleton;
import net.minecraft.server.v1_14_R1.EntityTypes;
import net.minecraft.server.v1_14_R1.World;

/**
 * @author hyuchiha
 */
public class CustomSkeleton extends EntitySkeleton {

  public CustomSkeleton(World world) {
    super(EntityTypes.SKELETON, world);
  }
}
