package com.hyuchiha.village_defense.Kits.Base;

import com.hyuchiha.village_defense.Game.GamePlayer;
import com.hyuchiha.village_defense.Game.Kit;
import com.hyuchiha.village_defense.Game.PlayerState;
import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Utils.XMaterial;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseKit implements Listener {

  private final String name;
  private final ItemStack icon;
  protected final List<ItemStack> spawnItems = new ArrayList<>();

  public BaseKit(String name, ItemStack icon, ConfigurationSection section) {
    this.name = name;
    this.icon = icon;

    setupLore(section);
    setupSpawnItems();

    registerListener();
  }

  private void registerListener() {
    Plugin plugin = Main.getInstance();
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  private void setupLore(ConfigurationSection section) {
    List<String> lore = new ArrayList<>();

    for (String line : section.getStringList("lore")) {
      line = line.replaceAll("(&([a-fk-or0-9]))", "§$2");
      lore.add(line);
    }

    ItemMeta meta = icon.getItemMeta();
    if (meta != null) {
      meta.setLore(lore);
      icon.setItemMeta(meta);
    }
  }

  /**
   * Los listeners de kit son globales (un solo registro, nunca des-registrado), asi
   * que cada handler debe verificar que el efecto aplique: kit correcto, jugador
   * dentro de una partida en curso y con arena. Sin esto los efectos disparaban en
   * el lobby y en todos los mundos del server.
   */
  protected boolean isKitActive(GamePlayer gamePlayer, Kit kit) {
    return gamePlayer != null
        && gamePlayer.getKit() == kit
        && gamePlayer.getState() == PlayerState.INGAME
        && gamePlayer.getArena() != null;
  }

  protected abstract void setupSpawnItems();

  public void giveSpawnItems(Player recipient) {
    PlayerInventory inv = recipient.getInventory();

    ItemStack shield = XMaterial.SHIELD.parseItem();
    if (shield != null) {
      inv.setItemInOffHand(shield);
    }

    for (ItemStack item : spawnItems) {
      if (item == null) {
        continue;
      }
      ItemStack toGive = item.clone();
      inv.addItem(toGive);
    }

  }

  public ItemStack getIcon() {
    return icon;
  }

  public String getName() {
    return name.substring(0, 1) + name.substring(1).toLowerCase();
  }
}
