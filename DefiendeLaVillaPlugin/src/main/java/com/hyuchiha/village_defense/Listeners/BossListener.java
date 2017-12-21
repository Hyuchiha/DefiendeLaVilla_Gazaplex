/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Listeners;

import com.hyuchiha.village_defense.Arena.Arena;
import com.hyuchiha.village_defense.Database.Base.Account;
import com.hyuchiha.village_defense.Game.Game;
import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Manager.ArenaManager;
import com.hyuchiha.village_defense.Manager.MobManager;
import com.hyuchiha.village_defense.Manager.PlayerManager;
import com.hyuchiha.village_defense.Mobs.BossEnemy;
import com.hyuchiha.village_defense.Output.Output;
import com.hyuchiha.village_defense.Scoreboard.ScoreboardType;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

/**
 *
 * @author hyuchiha
 */
public class BossListener implements Listener {

    public Main plugin;

    public BossListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        try {
            LivingEntity entity = event.getEntity();

            for (BossEnemy e : MobManager.getBossEnemyObjects()) {
                if (e.getBukkitEntityType() == entity.getType()) {
                    
                    event.getDrops().clear();

                    entity.getWorld().dropItemNaturally(entity.getLocation(), getRandomItemBoss());

                    Player player = entity.getKiller();
                    int moneyToGive = plugin.getConfig("config.yml").getInt("Game.money-boss-kill");
                    PlayerManager.addMoney(player, moneyToGive);

                    //Se actualiza la BD
                    Account data = plugin.getDatabase().getAccount(player.getUniqueId().toString(), player.getName());
                    data.setBosses_kills(data.getBosses_kills() + 1);

                    entity.remove();
                    
                    Arena arena = ArenaManager.getArenaConfiguration(event.getEntity().getWorld().getName());
                    Game game = arena.getGame();
                    game.getScoreboardManager().updateScoreboard(ScoreboardType.INGAME);
                    game.getScoreboardManager().updateScoreboard(ScoreboardType.SPECTATOR);
                }
            }
        } catch (Exception e) {
            Output.logError(e.getLocalizedMessage());
        }
    }

    private ItemStack getRandomItemBoss() {
        Random rand = new Random();
        ItemStack bossLoot = new ItemStack(Material.ROTTEN_FLESH);
        switch (rand.nextInt(5)) {
            case 1:
                bossLoot = new ItemStack(Material.DIAMOND_AXE);
                bossLoot.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 4);
                bossLoot.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 4);
                break;
            case 2:
                bossLoot = new ItemStack(Material.DIAMOND_SWORD);
                bossLoot.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 4);
                bossLoot.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 4);
                break;
            case 3:
                bossLoot = new ItemStack(Material.DIAMOND_LEGGINGS);
                bossLoot.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
                bossLoot.addUnsafeEnchantment(Enchantment.THORNS, 4);
                break;
            case 4:
                bossLoot = new ItemStack(Material.IRON_SWORD);
                bossLoot.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 7);
                bossLoot.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 2);
                break;
            case 5:
                bossLoot = new ItemStack(Material.DIAMOND_CHESTPLATE);
                bossLoot.addUnsafeEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 4);
                bossLoot.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, 4);
                break;
        }
        return bossLoot;
    }

}
