package com.hyuchiha.village_defense.Kits.Implementations;

import com.hyuchiha.village_defense.Game.GamePlayer;
import com.hyuchiha.village_defense.Game.Kit;
import com.hyuchiha.village_defense.Game.PlayerState;
import com.hyuchiha.village_defense.Kits.Base.BaseKit;
import com.hyuchiha.village_defense.Manager.PlayerManager;
import com.hyuchiha.village_defense.Messages.Translator;
import com.hyuchiha.village_defense.Utils.KitUtils;
import com.hyuchiha.village_defense.Utils.XMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class Hunter extends BaseKit {

  public Hunter(String name, ItemStack icon, ConfigurationSection section) {
    super(name, icon, section);
  }

  @Override
  protected void setupSpawnItems() {
    ItemStack woodSword = XMaterial.WOODEN_SWORD.parseItem();
    if (woodSword != null) {
      spawnItems.add(woodSword);
    }

    ItemStack bone = new ItemStack(Material.BONE, 3);
    ItemMeta meta = bone.getItemMeta();
    if (meta != null) {
      meta.setDisplayName(Translator.getColoredString("KITS.HUNTER_ITEM"));
      bone.setItemMeta(meta);
    }
    spawnItems.add(bone);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void RightClickChecks(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    GamePlayer gPlayer = PlayerManager.getPlayer(player);
    Action action = event.getAction();

    if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
      PlayerInventory inventory = player.getInventory();
      ItemStack handItem = inventory.getItemInMainHand();

      if (handItem != null && KitUtils.isItem(handItem, "KITS.HUNTER_ITEM")
          && isKitActive(gPlayer, Kit.HUNTER)) {
        //Se invoca a un lobo
        event.setCancelled(true);

        KitUtils.createWolf(player);

        //Se elimina el objeto en mano
        int amount = player.getInventory().getItemInMainHand().getAmount() - 1;
        if (amount <= 0) {
          player.getInventory().removeItem(player.getInventory().getItemInMainHand());
        } else {
          player.getInventory().getItemInMainHand().setAmount(amount);
        }
      }

    }
  }

  @EventHandler
  public void onWolfDamage(EntityDamageByEntityEvent event) {
    Entity damage = event.getDamager();
    Entity wolf = event.getEntity();

    // Solo se protege a lobos invocados (tamed) cuando el atacante es un jugador
    // que esta dentro de una partida. Antes cancelaba TODO daño jugador->lobo del
    // server entero.
    if (wolf instanceof Wolf && ((Wolf) wolf).isTamed() && damage instanceof Player) {
      GamePlayer gPlayer = PlayerManager.getPlayer((Player) damage);
      if (gPlayer.getState() == PlayerState.INGAME) {
        event.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    if (event.getCause() != EntityDamageEvent.DamageCause.PROJECTILE) {
      return;
    }
    Projectile proj = (Projectile) event.getDamager();
    if (!(proj.getShooter() instanceof Player)) {
      return;
    }

    Entity shot = event.getEntity();

    if (shot instanceof Wolf && ((Wolf) shot).isTamed()) {
      GamePlayer gPlayer = PlayerManager.getPlayer((Player) proj.getShooter());
      if (gPlayer.getState() == PlayerState.INGAME) {
        event.setCancelled(true);
      }
    }
  }
}
