package com.hyuchiha.village_defense.Utils;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.inventivetalent.reflection.minecraft.Minecraft;

public class Utils {
  public static boolean isWallSign(Block block) {
    if (Minecraft.Version.getVersion().olderThan(Minecraft.Version.v1_13_R1)) {
      Material clickedType = block.getType();
      return  clickedType == XMaterial.ACACIA_WALL_SIGN.parseMaterial()
          || clickedType == XMaterial.SPRUCE_WALL_SIGN.parseMaterial()
          || clickedType == XMaterial.BIRCH_WALL_SIGN.parseMaterial()
          || clickedType == XMaterial.DARK_OAK_WALL_SIGN.parseMaterial()
          || clickedType == XMaterial.JUNGLE_WALL_SIGN.parseMaterial()
          || clickedType == XMaterial.OAK_WALL_SIGN.parseMaterial();
    } else {
      return BaseUtils.isANewVersionSign(block);
    }
  }

  public static ItemStack getDyeGlassPane(DyeColor color) {
    String name = color.name().toUpperCase() + "_STAINED_GLASS_PANE";
    return XMaterial.valueOf(name).parseItem();
  }

  public static ItemStack getVillagerEgg(int quantity) {
    return XMaterial.VILLAGER_SPAWN_EGG.parseItem();
  }
}
