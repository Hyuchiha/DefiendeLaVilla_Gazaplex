package com.hyuchiha.village_defense.Kits.Implementations;

import com.hyuchiha.village_defense.Kits.Base.BaseKit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Tank extends BaseKit {

    public Tank(String name, ItemStack icon, ConfigurationSection section) {
        super(name, icon, section);
    }

    @Override
    protected void setupSpawnItems() {
        spawnItems.add(new ItemStack(Material.WOOD_SWORD));
    }

    @Override
    public void giveSpawnItems(Player recipient) {
        super.giveSpawnItems(recipient);

        PlayerInventory inventory = recipient.getInventory();
        inventory.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
    }


}
