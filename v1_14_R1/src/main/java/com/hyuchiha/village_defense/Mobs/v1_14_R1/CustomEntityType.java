package com.hyuchiha.village_defense.Mobs.v1_14_R1;

import com.hyuchiha.village_defense.Mobs.EntityType;
import com.hyuchiha.village_defense.Mobs.v1_14_R1.NMS.*;
import net.minecraft.server.v1_14_R1.Entity;
import net.minecraft.server.v1_14_R1.EntityTypes;
import net.minecraft.server.v1_14_R1.MinecraftKey;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

public enum CustomEntityType implements EntityType {
  CUSTOM_ZOMBIE("Zombie", 54, CustomZombie.class),
  CUSTOM_WITCH("Witch", 66, CustomWitch.class),
  CUSTOM_SPIDER("Spider", 52, CustomSpider.class),
  CUSTOM_SKELETON("Skeleton", 51, CustomSkeleton.class),
  CUSTOM_WITHER_SKELETON("Wither_Skeleton", 5, CustomWitherSkeleton.class),
  CUSTOM_PIGZOMBIE("PigZombie", 57, CustomPigZombie.class),
  CUSTOM_IRONGOLEM("VillagerGolem", 99, CustomIronGolem.class),
  CUSTOM_CREEPER("Creeper", 50, CustomCreeper.class),
  CUSTOM_CAVESPIDER("CaveSpider", 59, CustomCaveSpider.class),
  CUSTOM_MAGMA_CUBE("MagmaCube", 62, CustomMagmaCube.class),
  CUSTOM_SLIME("Slime", 55, CustomSlime.class),
  CUSTOM_GIANT("Giant", 53, CustomGiant.class),
  CUSTOM_WITHER("Wither", 64, CustomWither.class);

  private static CustomEntityRegistry ENTITY_REGISTRY = CustomEntityRegistry.getInstance();

  CustomEntityType(String name, int id, Class<? extends Entity> custom) {
    addToMaps(custom, name, id);
  }

  public org.bukkit.entity.Entity spawnEntity(Location location) {
    Entity entity = getEntity(this, location.getWorld());

    entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    ((CraftWorld) location.getWorld()).getHandle().addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);

    return entity.getBukkitEntity();
  }

  public void addToMaps(Class clazz, String name, int id) {
    registerEntityClass(clazz);
  }

  public Entity getEntity(CustomEntityType entity, World world) {
    CraftWorld craftWorld = (CraftWorld) world;

    switch (entity) {
      case CUSTOM_CAVESPIDER:
        return new CustomCaveSpider(craftWorld.getHandle());
      case CUSTOM_CREEPER:
        return new CustomCreeper(craftWorld.getHandle());
      case CUSTOM_IRONGOLEM:
        return new CustomIronGolem(craftWorld.getHandle());
      case CUSTOM_PIGZOMBIE:
        return new CustomPigZombie(craftWorld.getHandle());
      case CUSTOM_SKELETON:
        return new CustomSkeleton(craftWorld.getHandle());
      case CUSTOM_SPIDER:
        return new CustomSpider(craftWorld.getHandle());
      case CUSTOM_WITCH:
        return new CustomWitch(craftWorld.getHandle());
      case CUSTOM_WITHER_SKELETON:
        return new CustomWitherSkeleton(craftWorld.getHandle());
      case CUSTOM_GIANT:
        return new CustomGiant(craftWorld.getHandle());
      case CUSTOM_MAGMA_CUBE:
        return new CustomMagmaCube(craftWorld.getHandle());
      case CUSTOM_SLIME:
        return new CustomSlime(craftWorld.getHandle());
      case CUSTOM_WITHER:
        return new CustomWither(craftWorld.getHandle());
      case CUSTOM_ZOMBIE:
      default:
        return new CustomZombie(craftWorld.getHandle());
    }
  }

  public void registerEntityClass(Class<?> clazz) {
    if (ENTITY_REGISTRY == null)
      return;

    Class<?> search = clazz;
    while ((search = search.getSuperclass()) != null && Entity.class.isAssignableFrom(search)) {
      EntityTypes<?> type = ENTITY_REGISTRY.findType(search);
      MinecraftKey key = ENTITY_REGISTRY.getKey(type);
      if (key == null || type == null)
        continue;
      int code = ENTITY_REGISTRY.a(type);
      ENTITY_REGISTRY.put(code, key, type);
      return;
    }
    throw new IllegalArgumentException("unable to find valid entity superclass for class " + clazz.toString());
  }
}
