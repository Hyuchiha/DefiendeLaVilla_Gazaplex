package com.hyuchiha.village_defense.Kits.Implementations;

import com.hyuchiha.village_defense.Game.GamePlayer;
import com.hyuchiha.village_defense.Game.Kit;
import com.hyuchiha.village_defense.Kits.Base.BaseKit;
import com.hyuchiha.village_defense.Manager.PlayerManager;
import com.hyuchiha.village_defense.Utils.XMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Iceman extends BaseKit {

  public Iceman(String name, ItemStack icon, ConfigurationSection section) {
    super(name, icon, section);
  }

  @Override
  protected void setupSpawnItems() {
    ItemStack woodSword = XMaterial.WOODEN_SWORD.parseItem();
    spawnItems.add(woodSword);
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void KitDamageHelpers(EntityDamageByEntityEvent event) {
    Entity entityAttacker = event.getDamager();
    Entity entityAttacked = event.getEntity();
    if ((entityAttacker.getType() == EntityType.PLAYER) && entityAttacked instanceof LivingEntity && (entityAttacked.getType() != EntityType.PLAYER)) {
      Player damager = (Player) entityAttacker;

      GamePlayer player = PlayerManager.getPlayer(damager);

      if (player.getKit() == Kit.ICEMAN) {
        ((LivingEntity) entityAttacked).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 2));
      }

    }
  }
}
