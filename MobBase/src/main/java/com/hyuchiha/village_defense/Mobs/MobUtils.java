/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Mobs;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.Random;

/**
 *
 * @author hyuchiha
 */
public class MobUtils {

    private static final Random ran = new Random();

    public static ItemStack[] getRandomArmor(int difficulty) {
        ItemStack helmet = getRandomHelmet(difficulty);
        ItemStack chessplate = getRandomChestplate(difficulty);
        ItemStack leggings = getRandomLeggings(difficulty);
        ItemStack boots = getRandomBoots(difficulty);

        ItemStack[] armor = new ItemStack[]{
            helmet, chessplate, leggings, boots
        };

        for (ItemStack stack : armor) {
            stack = addRandomArmorEnchantments(stack, difficulty);
        }

        return armor;
    }

    //Plantilla basica de encantamiento de armaduras
    private static ItemStack addRandomArmorEnchantments(ItemStack armor, int difficulty) {
        try {
            
            if(armor == null){
                return armor;
            }
            
            switch (ran.nextInt(10)) {
                case 0:
                    armor.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, ran.nextInt(5));
                case 1:
                    armor.addUnsafeEnchantment(Enchantment.PROTECTION_FIRE, ran.nextInt(5));
                case 2:
                    armor.addUnsafeEnchantment(Enchantment.THORNS, ran.nextInt(5));
                case 3:
                    armor.addUnsafeEnchantment(Enchantment.DURABILITY, ran.nextInt(5));
                case 4:
                    armor.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, ran.nextInt(5));
                    break;
                default:
                    break;
            }
        } catch (ArithmeticException e) {

        }
        return armor;
    }

    //Encantamientos para espadas
    private static ItemStack addRandomSwordEnchantments(ItemStack weapon, int difficulty) {
        try {
            
            if(weapon == null){
                return weapon;
            }
            
            switch (ran.nextInt(15)) {
                case 0:
                    weapon.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, ran.nextInt(5));
                case 1:
                    weapon.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, ran.nextInt(5));
                case 2:
                    weapon.addUnsafeEnchantment(Enchantment.KNOCKBACK, ran.nextInt(5));
                case 3:
                    weapon.addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, ran.nextInt(5));
                case 4:
                    weapon.addUnsafeEnchantment(Enchantment.DURABILITY, ran.nextInt(5));
                    break;
                default:
                    break;
            }
        } catch (ArithmeticException e) {

        }
        return weapon;
    }

    //Encantamientos para arcos
    public static ItemStack addRandonBowEnchantments(ItemStack bow, int difficulty) {
        try {
            switch (ran.nextInt(15)) {
                case 0:
                    bow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, ran.nextInt(5));
                case 1:
                    bow.addUnsafeEnchantment(Enchantment.ARROW_FIRE, ran.nextInt(5));
                case 2:
                    bow.addUnsafeEnchantment(Enchantment.DURABILITY, ran.nextInt(5));
                case 3:
                    bow.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, ran.nextInt(5));
                case 4:
                    bow.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, ran.nextInt(10));
                    break;
                default:
                    break;
            }
        } catch (ArithmeticException e) {

        }
        return bow;
    }

    public static ItemStack getRandomWeapon(int difficulty) {
        if(ran.nextFloat() > .3){
            return null;
        }
        
        ItemStack weapon = null;
        switch (difficulty) {
            case 1:
                weapon = new ItemStack(Material.WOOD_SWORD);
                break;
            case 2:
                weapon = new ItemStack(Material.GOLD_SWORD);
                break;
            case 3:
                weapon = new ItemStack(Material.IRON_SWORD);
                break;
            case 4:
            default:
                weapon = new ItemStack(Material.DIAMOND_SWORD);
                break;
        }

        return addRandomSwordEnchantments(weapon, difficulty);
    }

    private static ItemStack getRandomChestplate(int difficulty) {
        if(ran.nextFloat() > .5){
            return null;
        }
        
        ItemStack Chestplate = null;
        switch (difficulty) {
            case 1:
                Chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
                break;
            case 2:
                Chestplate = new ItemStack(Material.IRON_CHESTPLATE);
                break;
            case 3:
                Chestplate = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
                break;
            case 4:
                Chestplate = new ItemStack(Material.GOLD_CHESTPLATE);
                break;
            case 5:
            default:
                Chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
                break;
        }
        return Chestplate;
    }

    private static ItemStack getRandomBoots(int difficulty) {
        if(ran.nextFloat() > .5){
            return null;
        }
        
        ItemStack Boots = null;
        switch (difficulty) {
            case 1:
                Boots = new ItemStack(Material.LEATHER_BOOTS);
                break;
            case 2:
                Boots = new ItemStack(Material.IRON_BOOTS);
                break;
            case 3:
                Boots = new ItemStack(Material.CHAINMAIL_BOOTS);
                break;
            case 4:
                Boots = new ItemStack(Material.GOLD_BOOTS);
                break;
            case 5:
            default:
                Boots = new ItemStack(Material.DIAMOND_BOOTS);
                break;
        }
        return Boots;
    }

    private static ItemStack getRandomHelmet(int difficulty) {
        if(ran.nextFloat() > .5){
            return null;
        }
        
        ItemStack Helmet = null;
        switch (difficulty) {
            case 1:
                Helmet = new ItemStack(Material.LEATHER_HELMET);
                break;
            case 2:
                Helmet = new ItemStack(Material.IRON_HELMET);
                break;
            case 3:
                Helmet = new ItemStack(Material.CHAINMAIL_HELMET);
                break;
            case 4:
                Helmet = new ItemStack(Material.GOLD_HELMET);
                break;
            case 5:
                Helmet = new ItemStack(Material.DIAMOND_HELMET);
                break;
        }
        return Helmet;
    }

    private static ItemStack getRandomLeggings(int difficulty) {
        if(ran.nextFloat() > .5){
            return null;
        }
        
        ItemStack Leggings = null;
        switch (difficulty) {
            case 1:
                Leggings = new ItemStack(Material.LEATHER_LEGGINGS);
                break;
            case 2:
                Leggings = new ItemStack(Material.IRON_LEGGINGS);
                break;
            case 3:
                Leggings = new ItemStack(Material.CHAINMAIL_LEGGINGS);
                break;
            case 4:
                Leggings = new ItemStack(Material.GOLD_LEGGINGS);
                break;
            case 5:
                Leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
                break;
        }
        return Leggings;
    }

    public static void addRandomPotionEffects(int waveNumber, LivingEntity entity) {
        Random ran = new Random();

        int potionsToApply = ran.nextInt(waveNumber)%3;

        for (int i = 0; i < potionsToApply; i++) {
            int potion = ran.nextInt(10);

            switch (potion) {
                case 1:
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3));
                    break;
                case 2:
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 3));
                    break;
                case 3:
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 3));
                    break;
                case 4:
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, Integer.MAX_VALUE, 3));
                    break;
                case 5:
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 3));
                    break;
                case 6:
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, Integer.MAX_VALUE, 3));
                    break;
                case 7:
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 3));
                    break;
                case 8:
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, Integer.MAX_VALUE, 3));
                    break;
                case 9:
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 3));
                    break;
                case 10:
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 5));
                    break;
            }

        }
    }

    public static Object getPrivateField(String fieldName, Class clazz, Object object) {
        Field field;
        Object o = null;

        try {
            field = clazz.getDeclaredField(fieldName);

            field.setAccessible(true);

            o = field.get(object);
        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
        }

        return o;
    }
}
