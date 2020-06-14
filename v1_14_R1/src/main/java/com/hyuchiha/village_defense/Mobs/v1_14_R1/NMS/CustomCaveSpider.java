package com.hyuchiha.village_defense.Mobs.v1_14_R1.NMS;

import net.minecraft.server.v1_14_R1.EntityCaveSpider;
import net.minecraft.server.v1_14_R1.EntityTypes;
import net.minecraft.server.v1_14_R1.World;

/**
 * @author hyuchiha
 */
public class CustomCaveSpider extends EntityCaveSpider {

  public CustomCaveSpider(World world) {
    super(EntityTypes.CAVE_SPIDER, world);
  }
}
