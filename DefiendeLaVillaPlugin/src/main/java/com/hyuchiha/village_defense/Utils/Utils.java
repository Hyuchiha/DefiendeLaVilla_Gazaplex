package com.hyuchiha.village_defense.Utils;

import org.bukkit.DyeColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.inventory.ItemStack;

public class Utils {
  public static boolean isWallSign(Block block) {
    BlockData data = block.getBlockData();
    return data instanceof Sign || data instanceof WallSign;
  }

  public static ItemStack getDyeGlassPane(DyeColor color) {
    String name = color.name().toUpperCase() + "_STAINED_GLASS_PANE";
    return XMaterial.valueOf(name).parseItem();
  }

  public static ItemStack getVillagerEgg(int quantity) {
    return XMaterial.VILLAGER_SPAWN_EGG.parseItem();
  }
}
