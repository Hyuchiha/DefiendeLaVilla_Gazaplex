package com.hyuchiha.village_defense.Mobs;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public interface EntityType {
    Entity spawnEntity(Location location);
    void addToMaps(Class clazz, String name, int id);
}
