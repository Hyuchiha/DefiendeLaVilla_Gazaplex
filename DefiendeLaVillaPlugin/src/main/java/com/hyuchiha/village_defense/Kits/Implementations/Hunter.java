package com.hyuchiha.village_defense.Kits.Implementations;

import com.hyuchiha.village_defense.Game.GamePlayer;
import com.hyuchiha.village_defense.Game.Kit;
import com.hyuchiha.village_defense.Kits.Base.BaseKit;
import com.hyuchiha.village_defense.Manager.PlayerManager;
import com.hyuchiha.village_defense.Utils.KitUtils;
import org.bukkit.ChatColor;
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
import org.bukkit.inventory.meta.ItemMeta;

public class Hunter extends BaseKit{

    public Hunter(String name, ItemStack icon, ConfigurationSection section) {
        super(name, icon, section);
    }

    @Override
    protected void setupSpawnItems() {
        spawnItems.add(new ItemStack(Material.WOOD_SWORD));
        ItemStack bone = new ItemStack(Material.BONE, 3);
        ItemMeta meta = bone.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Wolf");
        bone.setItemMeta(meta);
        spawnItems.add(bone);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void RightClickChecks(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        GamePlayer eventPlayer = PlayerManager.getPlayer(player);

        ItemStack stack = event.getItem();
        if (stack != null) {
            if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) || (event.getAction() == Action.RIGHT_CLICK_AIR)) {

                if(eventPlayer.getKit() == Kit.HUNTER){
                    if (stack.getType() == Material.BONE) {
                        if (KitUtils.isItem(stack, ChatColor.GOLD + "Wolf")) {
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
            }
        }
    }

    @EventHandler
    public void onWolfDamage(EntityDamageByEntityEvent event) {
        Entity damage = event.getDamager();
        Entity wolf = event.getEntity();

        if (wolf instanceof Wolf && damage instanceof Player) {
            event.setCancelled(true);
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

        if (shot instanceof Wolf) {
            event.setCancelled(true);
        }
    }
}
