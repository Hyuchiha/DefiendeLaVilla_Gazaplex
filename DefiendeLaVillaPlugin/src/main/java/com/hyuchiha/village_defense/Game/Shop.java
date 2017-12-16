package com.hyuchiha.village_defense.Game;

import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Manager.PlayerManager;
import com.hyuchiha.village_defense.Manager.ShopManager;
import com.hyuchiha.village_defense.Messages.Translator;
import com.hyuchiha.village_defense.Output.Output;
import com.hyuchiha.village_defense.Scoreboard.ScoreboardType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Random;

public class Shop implements Listener {

    private final String name;
    private ArrayList<ShopItem> itemsShop;
    private Main plugin;

    public Shop(Main plugin, String name, Configuration config) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.name = name;
        loadConfig(config);
    }

    public ArrayList<ShopItem> getRandomItemsForShop() {
        ArrayList<ShopItem> arenaItems = new ArrayList<>();

        int size = arenaItems.size() >= 9 ? 9: arenaItems.size() ;
        Random ran = new Random();

        while (size != 0) {
            int val = ran.nextInt(itemsShop.size());

            ShopItem itemToAdd = itemsShop.get(val);

            if(!arenaItems.contains(itemToAdd)){
                arenaItems.add(itemToAdd);
                size--;
            }
            
        }

        return arenaItems;
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onShopInventoryClick(InventoryClickEvent e) {
        Player buyer = (Player) e.getWhoClicked();
        GamePlayer vdplayer = PlayerManager.getPlayer(buyer);
        if (e.getInventory().getName().equals(name + " Shop")) {
            int slot = e.getRawSlot();
            if (slot < e.getInventory().getSize() && slot >= 0) {
                ArrayList<ShopItem> items = ShopManager.getShop(vdplayer.getArena(), name);

                if (slot < items.size() && items.get(slot) != null) {
                    sellItem(buyer, items.get(slot));
                }
                e.setCancelled(true);
            }
            buyer.updateInventory();
            e.setCancelled(true);
            buyer.closeInventory();
        }
    }

    @EventHandler
    public void onInteractI(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        GamePlayer vdplayer = PlayerManager.getPlayer(player);
        Action action = e.getAction();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            ItemStack handItem = player.getInventory().getItemInMainHand();
            if (handItem != null && handItem.hasItemMeta() && handItem.getItemMeta().hasDisplayName()) {
                switch (handItem.getType()) {
                    case EMERALD:
                        if (handItem.getItemMeta().hasDisplayName()) {
                            if (handItem.getItemMeta().getDisplayName().contains(Translator.change("EQUIPMENT_STORE"))) {
                                if (vdplayer.getState() == PlayerState.INGAME) {
                                    ShopManager.getShopConstructor(ShopManager.Equip).openShop(player);
                                }
                            }
                        }
                        break;
                    case QUARTZ:
                        if (handItem.getItemMeta().hasDisplayName()) {
                            if (handItem.getItemMeta().getDisplayName().contains(Translator.change("COMBAT_STORE"))) {
                                if (vdplayer.getState() == PlayerState.INGAME) {
                                    ShopManager.getShopConstructor(ShopManager.Combat).openShop(player);
                                }
                            }
                        }
                        break;
                    case REDSTONE:
                        if (handItem.getItemMeta().hasDisplayName()) {
                            if (handItem.getItemMeta().getDisplayName().contains(Translator.change("OTHER_STORE"))) {
                                if (vdplayer.getState() == PlayerState.INGAME) {
                                    ShopManager.getShopConstructor(ShopManager.Others).openShop(player);
                                }
                            }
                        }
                        break;
                }
            }
        }
    }

    @EventHandler
    public void ItemMoveEvent(InventoryMoveItemEvent event) {
        Inventory inv = event.getSource();
        if (inv.getName().equals(name + " Shop")) {
            event.setCancelled(true);
        }
    }

    public void openShop(Player player) {
        int size = 9;
        Inventory shopInv = Bukkit.getServer().createInventory(player, size,
                name + " Shop");

        GamePlayer vdplayer = PlayerManager.getPlayer(player);
        ArrayList<ShopItem> items = ShopManager.getShop(vdplayer.getArena(), name);

        for (int i = 0; i < items.size(); i++) {
            ShopItem item = items.get(i);
            if (item != null) {
                shopInv.setItem(i, item.getShopStack());
            } else {
                shopInv.setItem(i, null);
            }
        }
        player.openInventory(shopInv);
    }

    private void sellItem(Player buyer, ShopItem item) {
        Inventory buyerInv = buyer.getInventory();
        GamePlayer player = PlayerManager.getPlayer(buyer);
        ItemStack stackToGive = item.getItemStack();
        int price = item.getPrice();

        String stackName = ChatColor.WHITE + item.getName();
        if (player.getGems() >= price) {
            buyerInv.addItem(stackToGive);
            buyer.updateInventory();
            player.setGems(player.getGems() - price);
            buyer.sendMessage(Translator.change("PLAYER_PURCHASE_ITEM").replace("%ITEM%", stackName));
        } else {
            buyer.sendMessage(Translator.change("NOT_ENOUGHT_GEMS_TO_PURCHASE"));
        }

        player.getArena().getGame().getScoreboardManager().giveScoreboard(buyer.getName(), ScoreboardType.INGAME);
    }

    private void loadConfig(Configuration config) {
        itemsShop = new ArrayList<>();

        ConfigurationSection section = config.getConfigurationSection(name);
        for (String entry : section.getKeys(false)) {
            ShopItem item = loadItem(config, entry);
            itemsShop.add(item);
        }
    }

    private ShopItem loadItem(Configuration config, String itemName) {
        try {
            Material type = Material.getMaterial(config.getString(name + "." + itemName + ".type"));
            int price = config.getInt(name + "." + itemName + ".price");

            ShopItem item = null;
            if (type == Material.POTION) {

                String potionType   = config.getString(name + "." + itemName + ".potionType");
                int potionEffectNum = config.getInt(name + "." + itemName + ".potionEffectNum");
                boolean splash      = config.getBoolean(name + "." + itemName + ".splash");
                boolean extended    = config.getBoolean(name + "." + itemName + ".extended");

                Potion potion = new Potion(PotionType.valueOf(potionType), potionEffectNum);
                potion.setSplash(splash);

                if(extended){
                    potion.setHasExtendedDuration(extended);
                }

                item = new ShopItem(potion.toItemStack(1), price);

            } else {
                int qty = config.getInt(name + "." + itemName + ".amount");

                item = new ShopItem(type, qty, price);

                boolean hasEnchant = config.getBoolean(name + "." + itemName + ".hasEnchant");
                if (hasEnchant) {
                    ConfigurationSection section = config.getConfigurationSection(name + "." + itemName + ".enchants");
                    for (String keys : section.getKeys(false)) {
                        Enchantment newench = Enchantment.getByName(keys);
                        int level = section.getInt(keys);
                        item.addEnchant(newench, level);
                    }
                }
            }

            return item;
        } catch (Exception e) {
            e.printStackTrace();
            Output.logError(e.getLocalizedMessage());
        }
        return null;
    }

    public String getName() {
        return name;
    }
}
