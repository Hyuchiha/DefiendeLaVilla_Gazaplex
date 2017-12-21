/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Utils;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 *
 * @author hyuchiha
 */
public class KitUtils {

    public static void removePlayerWolfs(Player player) {
        for (World w : player.getServer().getWorlds()) {
            for (LivingEntity e : w.getLivingEntities()) {
                if (e instanceof Wolf && ((Wolf) e).isTamed() && player.equals(((Wolf) e).getOwner())) {
                    e.setHealth(0.0D);
                }
            }
        }
    }

    private static int countPlayerWolves(Player p) {
        int i = 0;
        for (World w : p.getServer().getWorlds()) {
            for (LivingEntity e : w.getLivingEntities()) {
                if (e instanceof Wolf && ((Wolf) e).isTamed() && p.equals(((Wolf) e).getOwner())) {
                    i++;
                }
            }
        }

        return i;
    }

    public static void createWolf(Player player) {
        Wolf wolf = (Wolf) player.getWorld().spawnEntity(player.getLocation(), EntityType.WOLF);

        // Just to make sure it's a normal wolf.
        wolf.setAdult();
        wolf.setTamed(true);
        wolf.setOwner(player);

        // We don't want extra wolves.
        wolf.setBreed(false);

        // Clarify the owner.
        wolf.setCustomName(ChatColor.BLUE + player.getName() + "'s Wolf");
        wolf.setCustomNameVisible(true);

        // Let's have a little bit of variation
        wolf.setCollarColor(DyeColor.PURPLE);

        // Misc.
        wolf.setHealth(wolf.getMaxHealth());
        wolf.setCanPickupItems(false);

        wolf.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 4));
        wolf.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1));
        wolf.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
    }

    public static void strikeDamage(Player p) {
        List<Entity> entities = p.getNearbyEntities(5, 5, 5);
        if (entities != null) {
            for (Entity attack : entities) {
                if (attack instanceof LivingEntity) {
                    if (attack.getType() != EntityType.PLAYER) {
                        LivingEntity entity = (LivingEntity) attack;
                        World world = p.getWorld();
                        world.strikeLightningEffect(entity.getLocation());
                        entity.damage(4.0D);

                    }
                }
            }
        }
    }

    public static boolean isItem(ItemStack stack, String name) {
        if (stack == null) {
            return false;
        }
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return false;
        }
        return meta.hasDisplayName() && meta.getDisplayName().equalsIgnoreCase(name);
    }
}
