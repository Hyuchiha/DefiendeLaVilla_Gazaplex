package com.hyuchiha.village_defense.Kits.Implementations;

import com.hyuchiha.village_defense.Kits.Base.BaseKit;
import com.hyuchiha.village_defense.Utils.XMaterial;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class Civilian extends BaseKit {

  public Civilian(String name, ItemStack icon, ConfigurationSection section) {
    super(name, icon, section);
  }

  @Override
  protected void setupSpawnItems() {
    ItemStack woodSword = XMaterial.WOODEN_SWORD.parseItem();
    spawnItems.add(woodSword);
  }
}
