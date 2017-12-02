/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Manager;

import com.hyuchiha.village_defense.Base.*;
import com.hyuchiha.village_defense.Chat.VaultHooks;
import com.hyuchiha.village_defense.Game.GamePlayer;
import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Messages.Translator;
import com.hyuchiha.village_defense.Output.Output;
import org.bukkit.entity.Player;
import org.inventivetalent.reflection.minecraft.Minecraft;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hyuchiha
 */
public class PlayerManager {

    public static List<GamePlayer> players = new ArrayList<>();
    private static Respawner respawner = null;

    public static void fetchRespawner(){
        Output.log("Getting respawner for the server version");

        switch (Minecraft.Version.getVersion()){
            case v1_9_R1:
                respawner = new Respawner_v1_9_R1();
                break;
            case v1_9_R2:
                respawner = new Respawner_v1_9_R2();
                break;
            case v1_10_R1:
                respawner = new Respawner_v1_10_R1();
                break;
            case v1_11_R1:
                respawner = new Respawner_v1_11_R1();
                break;
            case v1_12_R1:
                respawner = new Respawner_v1_12_R1();
                break;
            default:
                Output.logError("Version not supported");
                Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
                break;
        }
    }

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

    public static void respawnPlayer(Player player){
        if(respawner != null){
            respawner.respawm(player);
        }else {
            player.spigot().respawn();
        }
    }

    public static void addMoney(Player p, double money) {
        if (!VaultHooks.vault) {
            return;
        }

        if (!VaultHooks.getEconomyManager().hasAccount(p)) {
            VaultHooks.getEconomyManager().createPlayerAccount(p);
        }

        p.sendMessage(Translator.change("PLAYER_MONEY_GRANT")
                .replace("%MONEY%", Double.toString(money)));
        VaultHooks.getEconomyManager().depositPlayer(p, money);

    }

    public static double getMoney(Player p) {
        if (!VaultHooks.vault) {
            return 0;
        }

        if (!VaultHooks.getEconomyManager().hasAccount(p)) {
            return 0;
        }

        return VaultHooks.getEconomyManager().getBalance(p);
    }

    public static boolean withdrawMoney(Player p, double money) {

        if (VaultHooks.vault) {
            if (VaultHooks.getEconomyManager().has(p, money)) {
                VaultHooks.getEconomyManager().withdrawPlayer(p, money);
                return true;
            }
        }

        p.sendMessage(Translator.change("PLAYER_DONT_HAVE_REQUIRED_MONEY"));
        return false;

    }
}
