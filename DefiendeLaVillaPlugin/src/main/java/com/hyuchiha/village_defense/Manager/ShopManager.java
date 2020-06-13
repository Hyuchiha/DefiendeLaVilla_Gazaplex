/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Manager;

import com.hyuchiha.village_defense.Arena.Arena;
import com.hyuchiha.village_defense.Game.Shop;
import com.hyuchiha.village_defense.Game.ShopItem;
import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Output.Output;
import org.bukkit.configuration.Configuration;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author hyuchiha
 */
public class ShopManager {
  public static final String Equip = "Equipment";
  public static final String Combat = "Combat";
  public static final String Others = "Others";

  private static final HashMap<String, ArrayList<ShopItem>> ArenaEquipmentsShop = new HashMap<>();
  private static final HashMap<String, ArrayList<ShopItem>> ArenaCombatShop = new HashMap<>();
  private static final HashMap<String, ArrayList<ShopItem>> ArenaOtherShop = new HashMap<>();

  private static Shop EquipShop;
  private static Shop OtherShop;
  private static Shop CombatShop;

  public static void initShops() {
    Output.log("Iniciando los Shops");
    Configuration config = Main.getInstance().getConfig("shops.yml");
    EquipShop = new Shop(Main.getInstance(), Equip, config);
    CombatShop = new Shop(Main.getInstance(), Combat, config);
    OtherShop = new Shop(Main.getInstance(), Others, config);
  }

  public static void InitArenaShop(Arena arena) {
    ArrayList<ShopItem> itemsEquipment = EquipShop.getRandomItemsForShop();
    ArenaEquipmentsShop.put(arena.getName(), itemsEquipment);

    ArrayList<ShopItem> itemsCombat = CombatShop.getRandomItemsForShop();
    ArenaCombatShop.put(arena.getName(), itemsCombat);

    ArrayList<ShopItem> itemsArmor = OtherShop.getRandomItemsForShop();
    ArenaOtherShop.put(arena.getName(), itemsArmor);
  }

  public static ArrayList<ShopItem> getShop(Arena arena, String shopType) {
    switch (shopType) {
      case Equip:
        return ArenaEquipmentsShop.get(arena.getName());
      case Others:
        return ArenaOtherShop.get(arena.getName());
      case Combat:
        return ArenaCombatShop.get(arena.getName());
      default:
        return null;
    }
  }

  public static Shop getShopConstructor(String shopType) {
    switch (shopType) {
      case Equip:
        return EquipShop;
      case Others:
        return OtherShop;
      case Combat:
        return CombatShop;
      default:
        return null;
    }
  }

  public static void getNewRandomShops(Arena arena) {
    ArenaEquipmentsShop.put(arena.getName(), EquipShop.getRandomItemsForShop());
    ArenaCombatShop.put(arena.getName(), CombatShop.getRandomItemsForShop());
    ArenaOtherShop.put(arena.getName(), OtherShop.getRandomItemsForShop());
  }
}
