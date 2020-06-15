package com.hyuchiha.village_defense.Utils;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.inventivetalent.reflection.minecraft.Minecraft;

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
    if (Minecraft.Version.getVersion().olderThan(Minecraft.Version.v1_13_R1)) {
      return new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"), 1, (byte) color.getDyeData());
    } else {
      String name = color.name().toUpperCase() + "_STAINED_GLASS_PANE";
      return new ItemStack(Material.getMaterial(name), 1);
    }
  }

  public static ItemStack getVillagerEgg(int quantity) {
    if (Minecraft.Version.getVersion().olderThan(Minecraft.Version.v1_13_R1)) {
      return new ItemStack(Material.getMaterial("MONSTER_EGG"), quantity, (short) 12);
    } else {
      return new ItemStack(Material.VILLAGER_SPAWN_EGG, quantity);
    }
  }
}
