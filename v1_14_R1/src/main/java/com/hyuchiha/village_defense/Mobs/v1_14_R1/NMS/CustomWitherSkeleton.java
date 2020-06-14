package com.hyuchiha.village_defense.Mobs.v1_14_R1.NMS;

import net.minecraft.server.v1_14_R1.EntitySkeletonWither;
import net.minecraft.server.v1_14_R1.EntityTypes;
import net.minecraft.server.v1_14_R1.World;

/**
 * @author Kev'
 */
public class CustomWitherSkeleton extends EntitySkeletonWither {

  public CustomWitherSkeleton(World world) {
    super(EntityTypes.WITHER_SKELETON, world);
  }

}
