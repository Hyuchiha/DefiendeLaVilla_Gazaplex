package com.hyuchiha.village_defense.Game;

import com.hyuchiha.village_defense.Database.KitsUnlockedManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public enum Kit {

    CIVIL(Material.WORKBENCH) {
        {
            ItemStack sword =new ItemStack(Material.WOOD_SWORD);
            spawnItems.add(sword);
            lore.add(ChatColor.DARK_AQUA + "Clase inicial");
            lore.add("");
            lore.add(ChatColor.AQUA + "Este kit te da lo");
            lore.add(ChatColor.AQUA + "basico para sobrevivir");
        }
    },
    TANQUE(Material.DIAMOND_CHESTPLATE) {
        {
            spawnItems.add(new ItemStack(Material.WOOD_SWORD));
            lore.add(ChatColor.DARK_AQUA + "Tanques todos los ataques");
            lore.add("");
            lore.add(ChatColor.AQUA + "Empiezas con un peto de diamante");
            lore.add(ChatColor.AQUA + "y te encargas de empezar las peleas");
        }
    },
    PYRO(Material.BLAZE_POWDER){
        {
            spawnItems.add(new ItemStack(Material.STONE_SWORD));
            lore.add(ChatColor.DARK_AQUA + "Pyromano");
            lore.add("");
            lore.add(ChatColor.AQUA + "Tienes la habilidad de");
            lore.add(ChatColor.AQUA + "prenderle fuego a todo lo que golpeas");
        }
    },
    COMANDO(Material.IRON_HELMET){
        {
            spawnItems.add(new ItemStack(Material.STONE_SWORD));
            lore.add(ChatColor.DARK_AQUA + "Tu diriges el combate");
            lore.add("");
            lore.add(ChatColor.AQUA + "Eres el encargado de dirigir");
            lore.add(ChatColor.AQUA + "a tus colegas en la batalla.");
            lore.add("");
            lore.add(ChatColor.AQUA + "Tienes mas resistencia a los golpes");
        }
    },
    CONGELADOR(Material.ICE){
        {
            spawnItems.add(new ItemStack(Material.WOOD_SWORD));
            lore.add(ChatColor.DARK_AQUA + "Tus ataques son como el hielo");
            lore.add("");
            lore.add(ChatColor.AQUA + "Con tu habilidad alentas a todo");
            lore.add(ChatColor.AQUA + "enemigo que golpees por 3 seg.");
        }
    },
    INVOCADOR(Material.EGG){
        {
            spawnItems.add(new ItemStack(Material.WOOD_SWORD));
            ItemStack invocador = new ItemStack(Material.MONSTER_EGG, 3, (short) 12);
            ItemMeta meta = invocador.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "Aldeano");
            invocador.setItemMeta(meta);
            spawnItems.add(invocador);
            lore.add(ChatColor.DARK_AQUA + "Puedes invocar desde la muerte");
            lore.add("");
            lore.add(ChatColor.AQUA + "Puedes reaparecer a 3 aldeanos");
            lore.add(ChatColor.AQUA + "y evitar que ellos desaparezcan.");
        }
    },
    CAZADOR(Material.BONE){
        {
            spawnItems.add(new ItemStack(Material.WOOD_SWORD));
            ItemStack bone = new ItemStack(Material.BONE, 3);
            ItemMeta meta = bone.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "Lobo");
            bone.setItemMeta(meta);
            spawnItems.add(bone);
            lore.add(ChatColor.DARK_AQUA + "Eres una cazador experimentado");
            lore.add("");
            lore.add(ChatColor.AQUA + "Tienes la capacidad de invocar");
            lore.add(ChatColor.AQUA + "3 lobos que te ayuden a cazar.");
        }
    },
    MERCADER(Material.CHEST){
        {
            spawnItems.add(new ItemStack(Material.STONE_SWORD));
            lore.add(ChatColor.DARK_AQUA + "El autentico comerciante");
            lore.add("");
            lore.add(ChatColor.AQUA + "Tienes un 33% de descuentos en la tienda");
            lore.add(ChatColor.AQUA + "y cuando otros compran recibes un 3%");
        }
    },
    THOR(Material.GOLD_AXE){
        {
            spawnItems.add(new ItemStack(Material.STONE_SWORD));
            ItemStack hammer = new ItemStack(Material.GOLD_AXE);
            ItemMeta meta = hammer.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "Hammer");
            hammer.setItemMeta(meta);
            spawnItems.add(hammer);
            lore.add(ChatColor.DARK_AQUA + "Thor");
            lore.add("");
            lore.add(ChatColor.AQUA + "Con tu martillo puedes");
            lore.add(ChatColor.AQUA + "invocar rayos que causan daño");
            lore.add(ChatColor.AQUA + "a tus enemigos cercanos.");
        }
    };

    static {
        for (Kit kit : values()) {
            kit.init();
        }
    }

    private ItemStack icon;
    List<String> lore = new ArrayList<>();
    List<ItemStack> spawnItems = new ArrayList<>();

    Kit(Material m) {
        icon = new ItemStack(m);

        ItemMeta meta = icon.getItemMeta();
        meta.setDisplayName(getName());
        icon.setItemMeta(meta);
    }

    private void init() {
        for (int i = 0; i < lore.size(); i++) {
            String s = lore.get(i);
            s = ChatColor.AQUA + s;
            lore.set(i, s);
        }
        ItemMeta meta = icon.getItemMeta();
        meta.setLore(lore);
        icon.setItemMeta(meta);
    }

    public static Kit getKit(String name) {
        for (Kit type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

    public void give(final Player recipient) {
        PlayerInventory inv = recipient.getInventory();

        for (ItemStack item : spawnItems) {
            ItemStack toGive = item.clone();
            inv.addItem(toGive);
        }

        switch(this){
            case TANQUE:
                recipient.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
                recipient.updateInventory();
                break;
            case COMANDO:
                recipient.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
                break;
        }
    }

    public String getName() {
        return name().substring(0, 1) + name().substring(1).toLowerCase();
    }

    public boolean isOwnedBy(Player p) {
        return p.isOp() || this == CIVIL || p.hasPermission("VD.Class." + getName().toLowerCase())
                || KitsUnlockedManager.hasKit(p.getUniqueId().toString() , this.name());
    }

    public ItemStack getIcon() {
        return icon;
    }

    public static boolean isItem(ItemStack stack, String name) {
        if (stack == null) {
            return false;
        }
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return false;
        }
        return meta.hasDisplayName() && meta.getDisplayName().equalsIgnoreCase(name);
    }
}
