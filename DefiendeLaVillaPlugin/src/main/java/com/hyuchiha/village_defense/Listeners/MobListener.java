package com.hyuchiha.village_defense.Listeners;

import com.hyuchiha.village_defense.Arena.Arena;
import com.hyuchiha.village_defense.Game.Game;
import com.hyuchiha.village_defense.Game.GamePlayer;
import com.hyuchiha.village_defense.Game.PlayerState;
import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Manager.ArenaManager;
import com.hyuchiha.village_defense.Manager.PlayerManager;
import com.hyuchiha.village_defense.Manager.SpectatorManager;
import com.hyuchiha.village_defense.Messages.Translator;
import com.hyuchiha.village_defense.Output.Output;
import com.hyuchiha.village_defense.Scoreboard.ScoreboardType;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author hyuchiha
 */
public class MobListener implements Listener {

    private static final Set<EntityType> hostileEntityTypes = new HashSet<EntityType>() {
        private static final long serialVersionUID = 42L;

        {
            add(EntityType.SKELETON);
            add(EntityType.CREEPER);
            add(EntityType.SPIDER);
            add(EntityType.CAVE_SPIDER);
            add(EntityType.BAT);
            add(EntityType.ENDERMAN);
            add(EntityType.MAGMA_CUBE);
            add(EntityType.SLIME);
            add(EntityType.MAGMA_CUBE);
            add(EntityType.WITCH);
            add(EntityType.IRON_GOLEM);
            add(EntityType.WITHER_SKELETON);
            add(EntityType.ZOMBIE);
            add(EntityType.BLAZE);
            add(EntityType.PIG_ZOMBIE);
        }
    };
    public Main plugin;

    public MobListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        try {
            if (isHostile(event.getEntityType())) {
                LivingEntity mob = event.getEntity();

                List<MetadataValue> meta = mob.getMetadata("gems");

                event.getDrops().clear();
                event.setDroppedExp(0);
                if (meta.get(0) != null) {
                    ItemStack gems = new ItemStack(Material.EMERALD);
                    
                    Entity item = mob.getWorld().dropItem(mob.getLocation(), gems);
                    item.setMetadata("gems", new FixedMetadataValue(plugin, meta.get(0).asInt()));
                    //Se obtiene el jugador que lo mato si es q existe y se actualiza en BD
                    Player player = event.getEntity().getKiller();
                    if (player != null) {
                        //StatsManager.incrementStat(StatType.KILLS, player);
                        //TODO update stats
                        //PlayerStatsData data = PlayerStatsData.getPlayerStat(player.getName());
                        //data.setKills(data.getKills() + 1);
                    }

                    //Se verifica que tipo de mob es y se resta de la scoreboard
                    Arena arena = ArenaManager.getArenaConfiguration(mob.getWorld().getName());
                    Game game = arena.getGame();
                    game.getScoreboardManager().updateScoreboard(ScoreboardType.INGAME);
                    game.getScoreboardManager().updateScoreboard(ScoreboardType.SPECTATOR);
                }
            } else {
                if (event.getEntityType() == EntityType.VILLAGER) {
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
    
    @EventHandler
    public void onMobTarget(EntityTargetEvent event){
        if(isHostile(event.getEntityType()) && event.getTarget() != null && isHostile(event.getTarget().getType())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onVillagerDamageByPlayer(EntityDamageByEntityEvent event) {
        if (event.getEntityType() == EntityType.VILLAGER && event.getDamager().getType() == EntityType.PLAYER) {
            event.setCancelled(true);
            return;
        }

        if (event.getCause() != EntityDamageEvent.DamageCause.PROJECTILE) {
            return;
        }

        Projectile proj = (Projectile) event.getDamager();
        if (!(proj.getShooter() instanceof Player)) {
            return;
        }

        Entity shot = event.getEntity();

        if (shot instanceof Villager) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCombust(EntityCombustEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        switch (event.getCause()) {
            case DROWNING:
            case MAGIC:
            case SUFFOCATION:
                event.setCancelled(true);
                break;
        }
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {
        if (isHostile(e.getEntityType())) {
            if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) {
                return;
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickUp(PlayerPickupItemEvent event) {
        Item item = event.getItem();
        if (item.getItemStack().getType() == Material.EMERALD) {
            if (item.hasMetadata("gems")) {
                List<MetadataValue> meta = item.getMetadata("gems");

                Player player = event.getPlayer();

                GamePlayer pl = PlayerManager.getPlayer(player);

                if (SpectatorManager.isSpectator(player)) {
                    event.setCancelled(true);
                    return;
                }

                if (pl.getState() != PlayerState.INGAME) {
                    event.setCancelled(true);
                    return;
                }

                Random rand = new Random();
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F,
                        (rand.nextFloat() * 0.2F) + 0.9F);

                int gems = meta.get(0).asInt();
                player.sendMessage(Translator.change("GEMS_PICKUP").replace("%GEMS%", Integer.toString(gems)));
                pl.updateGems(meta.get(0).asInt());

                pl.getArena().getGame().getScoreboardManager().updateScoreboard(ScoreboardType.INGAME, player.getName());

                event.setCancelled(true);
                event.getItem().remove();
            } else {
                event.setCancelled(true);
                event.getItem().remove();
            }
        }
    }

    @EventHandler
    public void onCreeperExplode(EntityExplodeEvent e) {
        if (e.getEntityType() == EntityType.CREEPER) {
            e.setCancelled(true);
            List<Entity> entities = e.getEntity().getNearbyEntities(4, 4, 4);
            if (entities != null) {
                for (Entity attack : entities) {
                    if (attack.getType() == EntityType.PLAYER) {
                        Player attacked = (Player) attack;
                        if (SpectatorManager.isSpectator(attacked)) {
                            return;
                        }
                        if (((Creeper) e.getEntity()).isPowered()) {
                            attacked.damage(10.0D);
                        } else {
                            attacked.damage(7.0D);
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onTarget(EntityTargetEvent e) {
        try {
            if (isHostile(e.getEntityType())) {
                if (e.getTarget() instanceof Player) {
                    Player p = (Player) e.getTarget();
                    if(SpectatorManager.isSpectator(p)){
                        e.setTarget(null);
                        e.setCancelled(true);
                    }
                }
            }
        } catch (Exception ex) {
            
        }
    }

    private boolean isHostile(EntityType entityType) {
        return hostileEntityTypes.contains(entityType);
    }

}
