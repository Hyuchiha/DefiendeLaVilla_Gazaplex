/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Mobs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;

import java.util.Random;

/**
 * @author hyuchiha
 */
public abstract class BossEnemy extends EnemyIA {

    private org.bukkit.entity.EntityType bukkitEntityType;

    public BossEnemy(EntityType entityType, int minDroppedGold, int maxDroppedGold) {
        super(entityType, -1, minDroppedGold, maxDroppedGold, true);
        setCustomName("");
    }

    public LivingEntity spawnEntity(Location spawnLocation, Plugin plugin, int wave) {

        Entity e = getEntityType().spawnEntity(spawnLocation);

        this.bukkitEntityType = e.getType();

        if (!(e instanceof LivingEntity)) {
            e.remove();
        }

        Random random = new Random();
        LivingEntity entity = (LivingEntity) e;

        Integer gems = Math.max(getMinDroppedGold(), random.nextInt(getMaxDroppedGold())) + 1;

        entity.setMetadata("gems", new FixedMetadataValue(plugin, gems));

        if (isBoss()) {
            entity.setCustomName(ChatColor.YELLOW + "[" + ChatColor.BLUE + "BOSS" + ChatColor.YELLOW + "] " + ChatColor.RESET + getCustomName());
            entity.setCustomNameVisible(true);
        }

        if (getArmor() != null) {
            entity.getEquipment().setArmorContents(getArmor().clone());
        }

        if (getWeapon() != null) {
            entity.getEquipment().setItemInHand(getWeapon().clone());
        }

        entity.getEquipment().setHelmetDropChance(0F);
        entity.getEquipment().setChestplateDropChance(0F);
        entity.getEquipment().setLeggingsDropChance(0F);
        entity.getEquipment().setBootsDropChance(0F);
        entity.getEquipment().setItemInHandDropChance(0F);

        for (PotionEffect effect : getPotionEffects()) {
            entity.addPotionEffect(effect);
        }

        if (entity instanceof Zombie) {
            ((Zombie) entity).setBaby(isBaby());
            ((Zombie) entity).setVillager(isVillager());
        }

        entity.setMaxHealth(getMaxHealth());
        entity.setFireTicks(0);
        entity.setCanPickupItems(false);

        return entity;
    }

    public org.bukkit.entity.EntityType getBukkitEntityType() {
        return bukkitEntityType;
    }
}
