package com.hyuchiha.village_defense.Mobs.v1_14_R1.NMS;

import net.minecraft.server.v1_14_R1.EntitySlime;
import net.minecraft.server.v1_14_R1.EntityTypes;
import net.minecraft.server.v1_14_R1.World;

public class CustomSlime extends EntitySlime {
  public CustomSlime(World world) {
    super(EntityTypes.SLIME, world);
  }
}
