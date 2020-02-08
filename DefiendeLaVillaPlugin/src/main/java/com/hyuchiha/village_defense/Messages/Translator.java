package com.hyuchiha.village_defense.Messages;

import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Output.Output;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;

import java.util.HashMap;

public class Translator {

    private static final Main plugin = Main.getInstance();
    private static final HashMap<String, String> messages = new HashMap<>();

    public static void initMessages() {
        Output.log("Iniciando los mensajes");
        Configuration yml = plugin.getConfig("messages.yml");
        for (String s : yml.getKeys(false)) {
            messages.put(s, yml.getString(s));
        }
    }

    public static String getString(String id) {
        return ChatColor.stripColor(findMessageWithId(id));
    }

    public static String getColoredString(String s) {
        String ss = findMessageWithId(s);
        return ChatColor.translateAlternateColorCodes('&', ss);
    }

    public static String getPrefix() {
        return getColoredString("PREFIX") + " ";
    }

    private static String findMessageWithId(String id) {
        if (messages.containsKey(id)) {
            return messages.get(id);
        }
        return id;
    }
}
