/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Manager;

import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Messages.Translator;
import com.hyuchiha.village_defense.Game.GamePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hyuchiha
 */
public class PlayerManager {

    public static List<GamePlayer> players = new ArrayList<>();

    public static GamePlayer getPlayer(Player player) {
        for (GamePlayer vdplayer : players) {
            if (vdplayer.getPlayerUUID().toString().equalsIgnoreCase(player.getUniqueId().toString())) {
                return vdplayer;
            }
        }

        return createPlayerData(player);
    }
    
    private static GamePlayer createPlayerData(Player player) {
        GamePlayer actualPlayer = new GamePlayer(player.getUniqueId());
        players.add(actualPlayer);
        return actualPlayer;
    }

    public static void removePlayer(Player player) {
        GamePlayer vdplayer = getPlayer(player);
        players.remove(vdplayer);
    }

    public static void addMoney(Player p, double money) {
        try {
            if (!Main.getInstance().getEconomy().hasAccount(p.getName())) {
                Main.getInstance().getEconomy().createPlayerAccount(p.getName());
            }
            
            p.sendMessage(Translator.change("PLAYER_MONEY_GRANT").replace("%MONEY%", Double.toString(money)));
            Main.getInstance().getEconomy().depositPlayer(p.getName(), money);
            
        } catch (NullPointerException e) {

        }
    }

    public static double getMoney(Player p) {
        try{
            if(!Main.getInstance().getEconomy().hasAccount(p.getName())){
                return 0;
            }
            
            return Main.getInstance().getEconomy().getBalance(p.getName());
        }catch(NullPointerException e){
            
        }
        
        return 0;
    }

    public static boolean withdrawMoney(Player p, double money) {
        try {
            Main.getInstance().getEconomy().withdrawPlayer(p.getName(), money);
            return true;
        } catch (Exception e) {
            p.sendMessage(Translator.change("PLAYER_DONT_HAVE_REQUIRED_MONEY"));
            return false;
        }
    }
}
