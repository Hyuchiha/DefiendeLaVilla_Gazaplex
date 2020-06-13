package com.hyuchiha.village_defense.Kits.Implementations;

import com.hyuchiha.village_defense.Kits.Base.BaseKit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Commander extends BaseKit {

  public Commander(String name, ItemStack icon, ConfigurationSection section) {
    super(name, icon, section);
  }

  @Override
  protected void setupSpawnItems() {
    spawnItems.add(new ItemStack(Material.STONE_SWORD));
  }

  @Override
  public void giveSpawnItems(Player recipient) {
    super.giveSpawnItems(recipient);

    recipient.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
  }
}
