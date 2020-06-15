package com.hyuchiha.village_defense.Utils;

import org.bukkit.Material;

public class Utils {
  public static boolean isWallSign(Material type) {
    return type == XMaterial.ACACIA_WALL_SIGN.parseMaterial() ||
        type == XMaterial.SPRUCE_WALL_SIGN.parseMaterial() ||
        type == XMaterial.BIRCH_WALL_SIGN.parseMaterial() ||
        type == XMaterial.DARK_OAK_WALL_SIGN.parseMaterial() ||
        type == XMaterial.JUNGLE_WALL_SIGN.parseMaterial() ||
        type == XMaterial.OAK_WALL_SIGN.parseMaterial();
  }
}
