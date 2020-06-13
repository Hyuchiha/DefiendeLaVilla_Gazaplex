package com.hyuchiha.village_defense.Game;

import com.hyuchiha.village_defense.Database.Base.Account;
import com.hyuchiha.village_defense.Kits.Base.BaseKit;
import com.hyuchiha.village_defense.Kits.Implementations.*;
import com.hyuchiha.village_defense.Main;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

public enum Kit {

    CIVILIAN("CIVILIAN", Material.WORKBENCH),
    COMMANDER("COMMANDER", Material.IRON_HELMET),
    HUNTER("HUNTER", Material.BONE),
    ICEMAN("ICEMAN", Material.ICE),
    INVOKER("INVOKER", Material.EGG),
    MERCHANT("MERCHANT", Material.CHEST),
    PYRO("PYRO", Material.BLAZE_POWDER),
    TANK("TANK", Material.DIAMOND_CHESTPLATE),
    THOR("THOR", Material.GOLD_AXE);

    private HashMap<String, BaseKit> kits = new HashMap<>();

    Kit(String name, Material m) {

        ItemStack icon = new ItemStack(m);
        ItemMeta meta = icon.getItemMeta();
        meta.setDisplayName(name.substring(0, 1) + name.substring(1).toLowerCase());
        icon.setItemMeta(meta);

        Configuration configuration = Main.getInstance().getConfig("kits.yml");

        loadKit(name, icon, configuration.getConfigurationSection("Kits." + name().toUpperCase()));
    }

    private void loadKit(String name, ItemStack icon, ConfigurationSection configurationSection) {
        switch (name) {
            case "CIVILIAN":
                kits.put(name, new Civilian(name, icon, configurationSection));
                break;
            case "COMMANDER":
                kits.put(name, new Commander(name, icon, configurationSection));
                break;
            case "HUNTER":
                kits.put(name, new Hunter(name, icon, configurationSection));
                break;
            case "ICEMAN":
                kits.put(name, new Iceman(name, icon, configurationSection));
                break;
            case "INVOKER":
                kits.put(name, new Invoker(name, icon, configurationSection));
                break;
            case "MERCHANT":
                kits.put(name, new Merchant(name, icon, configurationSection));
                break;
            case "PYRO":
                kits.put(name, new Pyro(name, icon, configurationSection));
                break;
            case "TANK":
                kits.put(name, new Tank(name, icon, configurationSection));
                break;
            case "THOR":
                kits.put(name, new Thor(name, icon, configurationSection));
                break;
        }
    }

    public BaseKit getKit() {
        return kits.get(name());
    }

    public String getName() {
        return name().substring(0, 1) + name().substring(1).toLowerCase();
    }

    public boolean isOwnedBy(Player p) {
        Account account = Main.getInstance().getDatabase().getAccount(p.getUniqueId().toString(), p.getName());

        return p.isOp()
                || this == CIVILIAN
                || p.hasPermission("VD.Class." + getName().toLowerCase())
                || (account != null && account.hasKit(this));
    }
}
