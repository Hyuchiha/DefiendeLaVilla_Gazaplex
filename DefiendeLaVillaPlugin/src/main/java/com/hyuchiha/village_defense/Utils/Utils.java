package com.hyuchiha.village_defense.Utils;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Utils {
  public static boolean isWallSign(Material type) {
    return type == XMaterial.ACACIA_WALL_SIGN.parseMaterial() ||
        type == XMaterial.SPRUCE_WALL_SIGN.parseMaterial() ||
        type == XMaterial.BIRCH_WALL_SIGN.parseMaterial() ||
        type == XMaterial.DARK_OAK_WALL_SIGN.parseMaterial() ||
        type == XMaterial.JUNGLE_WALL_SIGN.parseMaterial() ||
        type == XMaterial.OAK_WALL_SIGN.parseMaterial();
  }

  public static ItemStack getDyeGlassPane(DyeColor color) {
    String name = color.name().toUpperCase() + "_STAINED_GLASS_PANE";
    return XMaterial.valueOf(name).parseItem();
  }

  public static ItemStack getVillagerEgg(int quantity) {
    return XMaterial.VILLAGER_SPAWN_EGG.parseItem();
  }
}
