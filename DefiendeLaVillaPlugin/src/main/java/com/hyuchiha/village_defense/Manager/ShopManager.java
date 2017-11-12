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
 *
 * @author hyuchiha
 */
public class ShopManager {
    public static final String Equip =  "Equipment";
    public static final String Others = "Others";
    public static final String Vip = "Vip";
    
    private static final HashMap<String, ArrayList<ShopItem>> ArenaEquipmentsShop = new HashMap();
    private static final HashMap<String, ArrayList<ShopItem>> ArenaOtherShop = new HashMap();
    private static final HashMap<String, ArrayList<ShopItem>> ArenaVipShop = new HashMap();
    private static Shop EquipShop;
    private static Shop OtherShop;
    private static Shop vipShop;
            
    public static void initShops(){
        Output.log("Iniciando los Shops");
        Configuration config= Main.getInstance().getConfig("shops.yml");
        EquipShop = new Shop(Main.getInstance(), Equip , config);
        OtherShop = new Shop(Main.getInstance(), Others , config);
        vipShop = new Shop(Main.getInstance(), Vip , config);
    }
    
    public static void InitArenaShop(Arena arena){
        ArrayList<ShopItem> itemsEquipment = EquipShop.getRandomItemsForShop();
        ArenaEquipmentsShop.put(arena.getName(), itemsEquipment);
        
        ArrayList<ShopItem> itemsArmor = OtherShop.getRandomItemsForShop();
        ArenaOtherShop.put(arena.getName(), itemsArmor);
        
        ArrayList<ShopItem> itemsVip = vipShop.getRandomItemsForShop();
        ArenaVipShop.put(arena.getName(), itemsVip);
    }
    
    public static ArrayList<ShopItem> getShop(Arena arena, String shopType){
        switch(shopType){
                case Equip:
                    return  ArenaEquipmentsShop.get(arena.getName());
                case Others:
                    return  ArenaOtherShop.get(arena.getName());
                case Vip:
                    return  ArenaVipShop.get(arena.getName());
                default:
                    return null;
        }
    }
    
    public static Shop getShopConstructor(String shopType){
        switch(shopType){
            case Equip:
                    return EquipShop;
                case Others:
                    return OtherShop;
                case Vip:
                    return vipShop;
                default:
                    return null;
        }
    }
    
    public static void getNewRandomShops(Arena arena){
        ArenaEquipmentsShop.put(arena.getName(), EquipShop.getRandomItemsForShop());
        ArenaOtherShop.put(arena.getName(), OtherShop.getRandomItemsForShop());
        ArenaVipShop.put(arena.getName(), vipShop.getRandomItemsForShop());
    }
}
