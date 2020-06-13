/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Mobs.v1_9_R2;

import com.hyuchiha.village_defense.Mobs.EntityType;
import com.hyuchiha.village_defense.Mobs.MobUtils;
import com.hyuchiha.village_defense.Mobs.v1_9_R2.NMS.*;
import net.minecraft.server.v1_9_R2.Entity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_9_R2.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import java.util.Map;

/**
 * @author hyuchiha
 */
public enum CustomEntityType implements EntityType {

  CUSTOM_ZOMBIE("Zombie", 54, CustomZombie.class),
  CUSTOM_WITCH("Witch", 66, CustomWitch.class),
  CUSTOM_SPIDER("Spider", 52, CustomSpider.class),
  CUSTOM_SKELETON("Skeleton", 51, CustomSkeleton.class),
  CUSTOM_PIGZOMBIE("PigZombie", 57, CustomPigZombie.class),
  CUSTOM_IRONGOLEM("VillagerGolem", 99, CustomIronGolem.class),
  CUSTOM_CREEPER("Creeper", 50, CustomCreeper.class),
  CUSTOM_CAVESPIDER("CaveSpider", 59, CustomCaveSpider.class),
  CUSTOM_MAGMA_CUBE("MagmaCube", 62, CustomMagmaCube.class),
  CUSTOM_SLIME("Slime", 55, CustomSlime.class),
  CUSTOM_GIANT("Giant", 53, CustomGiant.class),
  CUSTOM_WITHER("WitherBoss", 64, CustomWither.class);

  CustomEntityType(String name, int id, Class<? extends Entity> custom) {
    addToMaps(custom, name, id);
  }

  public org.bukkit.entity.Entity spawnEntity(Location location) {
    Entity entity = getEntity(this, location.getWorld());

    entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    ((CraftWorld) location.getWorld()).getHandle().addEntity(entity, SpawnReason.CUSTOM);

    return entity.getBukkitEntity();
  }

  public void addToMaps(Class clazz, String name, int id) {
    //getPrivateField is the method from above.
    //Remove the lines with // in front of them if you want to override default entities (You'd have to remove the default entity from the map first though).
    ((Map) MobUtils.getPrivateField("c", net.minecraft.server.v1_9_R2.EntityTypes.class, null)).put(name, clazz);
    ((Map) MobUtils.getPrivateField("d", net.minecraft.server.v1_9_R2.EntityTypes.class, null)).put(clazz, name);
    //((Map)getPrivateField("e", net.minecraft.server.v1_7_R4.EntityTypes.class, null)).put(Integer.valueOf(id), clazz);
    ((Map) MobUtils.getPrivateField("f", net.minecraft.server.v1_9_R2.EntityTypes.class, null)).put(clazz, id);
    //((Map)getPrivateField("g", net.minecraft.server.v1_7_R4.EntityTypes.class, null)).put(name, Integer.valueOf(id));
  }

  public Entity getEntity(CustomEntityType entity, World world) {
    switch (entity) {
      case CUSTOM_CAVESPIDER:
        return new CustomCaveSpider(((CraftWorld) world).getHandle());
      case CUSTOM_CREEPER:
        return new CustomCreeper(((CraftWorld) world).getHandle());
      case CUSTOM_IRONGOLEM:
        return new CustomIronGolem(((CraftWorld) world).getHandle());
      case CUSTOM_PIGZOMBIE:
        return new CustomPigZombie(((CraftWorld) world).getHandle());
      case CUSTOM_SKELETON:
        return new CustomSkeleton(((CraftWorld) world).getHandle());
      case CUSTOM_SPIDER:
        return new CustomSpider(((CraftWorld) world).getHandle());
      case CUSTOM_WITCH:
        return new CustomWitch(((CraftWorld) world).getHandle());
      case CUSTOM_GIANT:
        return new CustomGiant(((CraftWorld) world).getHandle());
      case CUSTOM_MAGMA_CUBE:
        return new CustomMagmaCube(((CraftWorld) world).getHandle());
      case CUSTOM_SLIME:
        return new CustomSlime(((CraftWorld) world).getHandle());
      case CUSTOM_WITHER:
        return new CustomWither(((CraftWorld) world).getHandle());
      case CUSTOM_ZOMBIE:
      default:
        return new CustomZombie(((CraftWorld) world).getHandle());
    }
  }
}
