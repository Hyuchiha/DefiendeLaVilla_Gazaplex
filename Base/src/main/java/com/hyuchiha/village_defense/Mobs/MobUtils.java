/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Mobs;

import com.hyuchiha.village_defense.Utils.XMaterial;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
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
      addRandomArmorEnchantments(stack, difficulty);
    }

    return armor;
  }

  private static final Enchantment[] ARMOR_ENCHANTS = {
      Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_FIRE,
      Enchantment.THORNS, Enchantment.DURABILITY, Enchantment.PROTECTION_PROJECTILE
  };
  private static final Enchantment[] SWORD_ENCHANTS = {
      Enchantment.DAMAGE_ALL, Enchantment.FIRE_ASPECT, Enchantment.KNOCKBACK,
      Enchantment.DAMAGE_UNDEAD, Enchantment.DURABILITY
  };
  private static final Enchantment[] BOW_ENCHANTS = {
      Enchantment.ARROW_DAMAGE, Enchantment.ARROW_FIRE, Enchantment.DURABILITY,
      Enchantment.DAMAGE_ALL, Enchantment.ARROW_KNOCKBACK
  };

  /**
   * Aplica encantamientos escalados por dificultad: a mayor dificultad (que crece
   * con la oleada, ver wave/10 en los mobs) se aplican MAS encantamientos distintos
   * y de mayor nivel, para que el mob sea mas duro de matar. El numero se topa al
   * tamaño del pool. Se eligen distintos (sin repetir) mezclando el pool.
   */
  private static void applyScaledEnchants(ItemStack item, Enchantment[] pool, int difficulty) {
    if (item == null) {
      return;
    }

    int tier = Math.max(1, difficulty);
    int count = Math.min(tier, pool.length);
    int maxLevel = Math.min(tier, 5);

    List<Enchantment> shuffled = new ArrayList<>(Arrays.asList(pool));
    Collections.shuffle(shuffled, ran);

    for (int i = 0; i < count; i++) {
      int level = 1 + ran.nextInt(maxLevel);
      item.addUnsafeEnchantment(shuffled.get(i), level);
    }
  }

  //Plantilla basica de encantamiento de armaduras
  private static ItemStack addRandomArmorEnchantments(ItemStack armor, int difficulty) {
    applyScaledEnchants(armor, ARMOR_ENCHANTS, difficulty);
    return armor;
  }

  //Encantamientos para espadas
  private static ItemStack addRandomSwordEnchantments(ItemStack weapon, int difficulty) {
    applyScaledEnchants(weapon, SWORD_ENCHANTS, difficulty);
    return weapon;
  }

  //Encantamientos para arcos
  public static ItemStack addRandonBowEnchantments(ItemStack bow, int difficulty) {
    applyScaledEnchants(bow, BOW_ENCHANTS, difficulty);
    return bow;
  }

  public static ItemStack getRandomWeapon(int difficulty) {
    if (ran.nextFloat() > .3) {
      return null;
    }

    ItemStack weapon = null;
    switch (difficulty) {
      case 1:
        weapon = XMaterial.WOODEN_SWORD.parseItem();
        break;
      case 2:
        weapon = XMaterial.GOLDEN_SWORD.parseItem();
        break;
      case 3:
        weapon = XMaterial.IRON_SWORD.parseItem();
        break;
      case 4:
      default:
        weapon = XMaterial.DIAMOND_SWORD.parseItem();
        break;
    }

    return addRandomSwordEnchantments(weapon, difficulty);
  }

  private static ItemStack getRandomChestplate(int difficulty) {
    if (ran.nextFloat() > .5) {
      return null;
    }

    ItemStack Chestplate = null;
    switch (difficulty) {
      case 1:
        Chestplate =  XMaterial.LEATHER_CHESTPLATE.parseItem();
        break;
      case 2:
        Chestplate = XMaterial.IRON_CHESTPLATE.parseItem();
        break;
      case 3:
        Chestplate = XMaterial.CHAINMAIL_CHESTPLATE.parseItem();
        break;
      case 4:
        Chestplate = XMaterial.GOLDEN_CHESTPLATE.parseItem();
        break;
      case 5:
      default:
        Chestplate = XMaterial.DIAMOND_CHESTPLATE.parseItem();
        break;
    }
    return Chestplate;
  }

  private static ItemStack getRandomBoots(int difficulty) {
    if (ran.nextFloat() > .5) {
      return null;
    }

    ItemStack Boots = null;
    switch (difficulty) {
      case 1:
        Boots = XMaterial.LEATHER_BOOTS.parseItem();
        break;
      case 2:
        Boots = XMaterial.IRON_BOOTS.parseItem();
        break;
      case 3:
        Boots = XMaterial.CHAINMAIL_BOOTS.parseItem();
        break;
      case 4:
        Boots = XMaterial.GOLDEN_BOOTS.parseItem();
        break;
      case 5:
      default:
        Boots = XMaterial.DIAMOND_BOOTS.parseItem();
        break;
    }
    return Boots;
  }

  private static ItemStack getRandomHelmet(int difficulty) {
    if (ran.nextFloat() > .5) {
      return null;
    }

    ItemStack Helmet = null;
    switch (difficulty) {
      case 1:
        Helmet = XMaterial.LEATHER_HELMET.parseItem();
        break;
      case 2:
        Helmet = XMaterial.IRON_HELMET.parseItem();
        break;
      case 3:
        Helmet = XMaterial.CHAINMAIL_HELMET.parseItem();
        break;
      case 4:
        Helmet = XMaterial.GOLDEN_HELMET.parseItem();
        break;
      case 5:
        Helmet = XMaterial.DIAMOND_HELMET.parseItem();
        break;
    }
    return Helmet;
  }

  private static ItemStack getRandomLeggings(int difficulty) {
    if (ran.nextFloat() > .5) {
      return null;
    }

    ItemStack Leggings = null;
    switch (difficulty) {
      case 1:
        Leggings = XMaterial.LEATHER_LEGGINGS.parseItem();
        break;
      case 2:
        Leggings = XMaterial.IRON_LEGGINGS.parseItem();
        break;
      case 3:
        Leggings = XMaterial.CHAINMAIL_LEGGINGS.parseItem();
        break;
      case 4:
        Leggings = XMaterial.GOLDEN_LEGGINGS.parseItem();
        break;
      case 5:
        Leggings = XMaterial.DIAMOND_LEGGINGS.parseItem();
        break;
    }
    return Leggings;
  }

  /**
   * Velocidad de movimiento escalada por oleada. Antes los mobs llevaban SPEED
   * amplificador 3 (Velocidad IV) FIJO desde la oleada 1 -> se sentian demasiado
   * rapidos al inicio. Ahora la curva sube con la oleada:
   *   oleada  1-4  -> sin velocidad extra
   *   oleada  5-9  -> Velocidad I  (amp 0)
   *   oleada 10-14 -> Velocidad II (amp 1)
   *   oleada 15+   -> Velocidad III (amp 2, tope)
   * Devuelve -1 cuando no debe aplicarse ningun efecto de velocidad.
   */
  public static int getMovementSpeedAmplifier(int wave) {
    if (wave < 5) {
      return -1;
    }
    if (wave < 10) {
      return 0;
    }
    if (wave < 15) {
      return 1;
    }
    return 2;
  }

  /** Aplica SPEED escalado por oleada (ver {@link #getMovementSpeedAmplifier(int)}). */
  public static void applyScaledSpeed(int wave, LivingEntity entity) {
    int amplifier = getMovementSpeedAmplifier(wave);
    if (amplifier < 0) {
      return;
    }
    entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, amplifier));
  }

  public static void addRandomPotionEffects(int waveNumber, LivingEntity entity) {
    Random ran = java.util.concurrent.ThreadLocalRandom.current();

    int potionsToApply = ran.nextInt(waveNumber) % 3;

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
    } catch (NoSuchFieldException | IllegalAccessException e) {
    }

    return o;
  }
}
