/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Mobs.v1_11_R1.CustomMob.SpecialMobs;

import com.hyuchiha.village_defense.Mobs.CustomMob.SpecialMobs.GiantMob;
import com.hyuchiha.village_defense.Mobs.v1_11_R1.CustomEntityType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Kev'
 */
public class GiantSp extends GiantMob {

    public GiantSp() {
        super(CustomEntityType.CUSTOM_GIANT);
        setArmorContents(new ItemStack(Material.DIAMOND_HELMET), new ItemStack(Material.DIAMOND_CHESTPLATE), new ItemStack(Material.DIAMOND_LEGGINGS), new ItemStack(Material.DIAMOND_BOOTS));
        setWeaponItemStack(new ItemStack(Material.DIAMOND_SWORD));
    }
    
}
