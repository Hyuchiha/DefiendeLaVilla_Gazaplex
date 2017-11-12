/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Mobs.CustomMob.SpecialMobs;

import com.hyuchiha.village_defense.Mobs.BossEnemy;
import com.hyuchiha.village_defense.Mobs.EntityType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author Kev'
 */
public class GiantMob extends BossEnemy {

    public GiantMob(EntityType type) {
        super(type, 100, 200);
        setArmorContents(new ItemStack(Material.DIAMOND_HELMET), new ItemStack(Material.DIAMOND_CHESTPLATE), new ItemStack(Material.DIAMOND_LEGGINGS), new ItemStack(Material.DIAMOND_BOOTS));
        setWeaponItemStack(new ItemStack(Material.DIAMOND_SWORD));
    }

}
