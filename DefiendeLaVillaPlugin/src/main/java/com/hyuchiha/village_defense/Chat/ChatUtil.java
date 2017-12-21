package com.hyuchiha.village_defense.Chat;

import com.hyuchiha.village_defense.Game.GamePlayer;
import com.hyuchiha.village_defense.Game.PlayerState;
import com.hyuchiha.village_defense.Manager.PlayerManager;
import com.hyuchiha.village_defense.Messages.Translator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ChatUtil {

    private static final String DARK_AQUA = ChatColor.DARK_AQUA.toString();
    private static final String DARK_GRAY = ChatColor.DARK_GRAY.toString();
    private static final String DARK_PURPLE = ChatColor.DARK_PURPLE.toString();
    private static final String DARK_RED = ChatColor.DARK_RED.toString();
    private static final String RESET = ChatColor.RESET.toString();
    private static final String GRAY = ChatColor.GRAY.toString();
    private static final String AQUA = ChatColor.AQUA.toString();
    private static final String GOLD = ChatColor.GOLD.toString();
    private static final String RED = ChatColor.RED.toString();
    private static final String BOLD = ChatColor.BOLD.toString();

    public static String allMessage(Player sender) {
        String group;
        String playerName = sender.getName();
        String s;
        World w = sender.getWorld();
        GamePlayer player = PlayerManager.getPlayer(sender);

        if (player.getState() == PlayerState.LOBBY) {
            group = DARK_GRAY + "[" + DARK_PURPLE + "Lobby" + DARK_GRAY + "] ";
            String primaryGroup0
                    = VaultHooks.getPermissionManager().getPrimaryGroup(Bukkit.getPlayer(playerName));
            String gprefix = VaultHooks.getChatManager().getGroupPrefix(w,
                    primaryGroup0);
            s = group
                    + fixDefault(ChatColor.translateAlternateColorCodes('&', gprefix)) + " "
                    + playerName + DARK_AQUA + "" + BOLD + " > " + RESET;
        } else {
            group
                    = DARK_GRAY + "[" + ChatColor.RED + "Global" + DARK_GRAY + "] ";
            String primaryGroup
                    = VaultHooks.getPermissionManager().getPrimaryGroup(Bukkit.getPlayer(playerName));
            String gprefix = VaultHooks.getChatManager().getGroupPrefix(w,
                    primaryGroup);
            s = group
                    + fixDefault(ChatColor.translateAlternateColorCodes('&', gprefix))
                    + playerName + DARK_AQUA + "" + BOLD + " > " + RESET;
        }
        return s;
    }

    public static String prefixMessage(Player sender) {
        String group;
        String s;
        String playerName = sender.getName();
        World w = sender.getWorld();

        group = Translator.change("PREFIX");
        String primaryGroup0 = VaultHooks.getPermissionManager().getPrimaryGroup(Bukkit.getPlayer(playerName));
        String gprefix = VaultHooks.getChatManager().getGroupPrefix(w, primaryGroup0);
        s = group + fixDefault(ChatColor.translateAlternateColorCodes('&', gprefix)) + " " + playerName + DARK_AQUA + "" + BOLD + " > " + RESET;

        return s;
    }

    private static String fixDefault(String s) {
        if (s.contains("default")) {
            s = "";
        }
        return s;
    }

    public static void broadcast(String message) {
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                message));
    }

    public static String formatDeathMessage(Player victim) {
        String victimName = ChatColor.RED + victim.getName() + ChatColor.GRAY;

        String message = Translator.change("PLAYER_DEATH_MESSAGE");
        message = message.replace("%PLAYER%", victimName);

        return message;
    }
}
