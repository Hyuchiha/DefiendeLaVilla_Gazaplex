package com.hyuchiha.village_defense.Listeners;

import com.hyuchiha.village_defense.Arena.Arena;
import com.hyuchiha.village_defense.Database.Base.Account;
import com.hyuchiha.village_defense.Event.ArenaLeaveEvent;
import com.hyuchiha.village_defense.Game.Game;
import com.hyuchiha.village_defense.Game.GamePlayer;
import com.hyuchiha.village_defense.Game.Kit;
import com.hyuchiha.village_defense.Game.PlayerState;
import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Manager.ArenaManager;
import com.hyuchiha.village_defense.Manager.PlayerManager;
import com.hyuchiha.village_defense.Manager.SpectatorManager;
import com.hyuchiha.village_defense.Messages.Translator;
import com.hyuchiha.village_defense.Scoreboard.ScoreboardType;
import com.hyuchiha.village_defense.Utils.KitUtils;
import com.hyuchiha.village_defense.Utils.MenuUtils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

/**
 * @author hyuchiha
 */
public class PlayerListener implements Listener {

  private final Main plugin;

  public PlayerListener(Main plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    final Player player = event.getEntity();

    event.setDeathMessage("");

    GamePlayer vdplayer = PlayerManager.getPlayer(player);

    if (vdplayer.getState() != PlayerState.INGAME) {
      return;
    }

    vdplayer.setKilled(true);
    vdplayer.getArena().getGame().playerDeathInfo(vdplayer);

    if (vdplayer.getKit() == Kit.HUNTER) {
      KitUtils.removePlayerWolfs(player);
    }

    event.getDrops().clear();

    Account data = plugin.getMainDatabase().getAccount(player.getUniqueId().toString(), player.getName());
    data.setDeaths(data.getDeaths() + 1);

    Arena arena = ArenaManager.getArenaConfiguration(player.getWorld().getName());
    Game game = arena.getGame();
    game.getScoreboardManager().updateScoreboard(ScoreboardType.INGAME);
    game.getScoreboardManager().updateScoreboard(ScoreboardType.SPECTATOR);

    Bukkit.getScheduler().runTaskLater(plugin, () -> {
      PlayerManager.respawnPlayer(player);
    }, 5L);
  }

  @EventHandler
  public void onPlayerRespawn(PlayerRespawnEvent e) {
    Player player = e.getPlayer();

    GamePlayer gamePlayer = PlayerManager.getPlayer(player);

    if (gamePlayer.getState() == PlayerState.INGAME) {
      Location location = gamePlayer.getArena().getSpawnArenaLocation();

      e.setRespawnLocation(location);

      SpectatorManager.addSpectator(player);
    }
  }

  @EventHandler
  public void BlockBreak(BlockBreakEvent e) {
    if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void BlockPlace(BlockPlaceEvent e) {
    if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void onTestEntityDamage(EntityDamageByEntityEvent event) {
    if (event.getDamager() instanceof Player) {
      if (event.getEntity() instanceof Player) {
        event.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void BlockBreak(PlayerDropItemEvent e) {
    Material Type = e.getItemDrop().getItemStack().getType();
    ItemStack stak = e.getItemDrop().getItemStack();
    switch (Type) {
      case EMERALD:
      case DIAMOND:
      case QUARTZ:
      case GOLD_INGOT:
      case COMPASS:
      case APPLE:
        if (stak.getItemMeta().hasDisplayName()) {
          e.setCancelled(true);
        }
        break;
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

    if (shot instanceof Player) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onInteractI(PlayerInteractEvent e) {
    Player player = e.getPlayer();
    GamePlayer pmeta = PlayerManager.getPlayer(player);
    Action action = e.getAction();
    if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
      ItemStack handItem = player.getInventory().getItemInMainHand();
      if (handItem != null) {
        switch (handItem.getType()) {
          case DIAMOND:
            if (handItem.getItemMeta().hasDisplayName()) {
              if (handItem.getItemMeta().getDisplayName().contains("Kit")) {
                e.setCancelled(true);

                if (pmeta.getArena() == null) {
                  return;
                }

                MenuUtils.showKitSelector(player);
              }
            }
            break;
          case GOLD_INGOT:
            if (handItem.getItemMeta().hasDisplayName()) {
              if (handItem.getItemMeta().getDisplayName().contains(Translator.getColoredString("GAME.UNLOCK_KIT"))) {
                e.setCancelled(true);

                if (pmeta.getArena() == null) {
                  return;
                }

                MenuUtils.showUnlockerSelector(player);
              }
            }
            break;
          case COMPASS:
            if (handItem.getItemMeta().hasDisplayName()) {
              if (handItem.getItemMeta().getDisplayName().contains(Translator.getColoredString("GAME.LEAVE_ARENA"))) {
                e.setCancelled(true);
                if (pmeta.getArena() == null) {
                  return;
                }

                Bukkit.getServer().getPluginManager().callEvent(
                    new ArenaLeaveEvent(pmeta, pmeta.getArena()));
              } else {
                if (handItem.getItemMeta().getDisplayName().contains(Translator.getColoredString("GAME.RETURN_TO_LOBBY"))) {
                  e.setCancelled(true);
                  final String ServerExit = plugin.getConfig().getString("ServerToConnect");
                  try {
                    final ByteArrayOutputStream b = new ByteArrayOutputStream();
                    final DataOutputStream out = new DataOutputStream(b);
                    out.writeUTF("Connect");
                    out.writeUTF(ServerExit);
                    player.sendPluginMessage(this.plugin, "BungeeCord", b.toByteArray());
                    b.close();
                    out.close();
                  } catch (Exception error) {
                    player.sendMessage(Translator.getPrefix() + ChatColor.RED + "Could not connect to " + ServerExit);
                  }
                }
              }
            }
            break;
          case APPLE:
            if (handItem.getItemMeta().hasDisplayName()) {
              if (handItem.getItemMeta().getDisplayName().contains(Translator.getColoredString("GAME.LEAVE_SPECTATOR"))) {
                e.setCancelled(true);
                if (SpectatorManager.isSpectator(player)) {
                  pmeta.getArena().getGame().removeSpectator(pmeta);
                }
              }
            }
            break;

        }
      }
    }
  }

  @EventHandler
  public void dmg(EntityDamageEvent event) {
    Entity e = event.getEntity();
    if (e instanceof Player) {
      Player player = (Player) e;
      GamePlayer vdplayer = PlayerManager.getPlayer(player);
      if (vdplayer.getState() != PlayerState.INGAME) {
        event.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void onFoodLevelChange(FoodLevelChangeEvent event) {
    if (event.getEntity() instanceof Player) {
      GamePlayer player = PlayerManager.getPlayer((Player) event.getEntity());
      if (player.getState() != PlayerState.INGAME) {
        event.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
    if (event.getRightClicked().getType() == EntityType.VILLAGER) {
      event.setCancelled(true);
    }
  }
}
