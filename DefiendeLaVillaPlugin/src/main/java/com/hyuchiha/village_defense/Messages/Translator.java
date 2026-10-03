package com.hyuchiha.village_defense.Messages;

import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Output.Output;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Translator {
  private static final Main plugin = Main.getInstance();
  private static final HashMap<String, String> messages = new HashMap<>();
  private static final HashMap<String, List<String>> listMessages = new HashMap<>();
  // Cache de strings ya traducidos (& -> §): evita re-correr translateAlternateColorCodes
  // en cada envio (scoreboard, mensajes en loop). Se limpia al recargar mensajes.
  private static final HashMap<String, String> coloredCache = new HashMap<>();
  private static String prefixCache = null;

  public static void initMessages() {
    Output.log("Registering messages");

    messages.clear();
    listMessages.clear();
    coloredCache.clear();
    prefixCache = null;

    // Claves del archivo + las del messages.yml del jar (defaults de ConfigManager): un
    // messages.yml viejo sin una clave nueva lee el texto de fabrica. getKeys no ve los
    // defaults con copyDefaults off, por eso se unen a mano; get/is* si los ven.
    Configuration section = plugin.getConfig("messages.yml");
    Set<String> keys = new LinkedHashSet<>(section.getKeys(true));
    if (section.getDefaults() != null) {
      keys.addAll(section.getDefaults().getKeys(true));
    }

    for (String key : keys) {
      if (section.isString(key)) {
        messages.put(key, section.getString(key));
      } else if (section.isList(key)) {
        listMessages.put(key, section.getStringList(key));
      }
    }
  }

  public static String getString(String id) {
    String ss = findMessageWithId(id);
    return ChatColor.stripColor(ss);
  }

  public static String getColoredString(String s) {
    String cached = coloredCache.get(s);
    if (cached != null) {
      return cached;
    }

    String ss = findMessageWithId(s);
    String colored = ChatColor.translateAlternateColorCodes('&', ss);
    coloredCache.put(s, colored);
    return colored;
  }

  public static List<String> getMultiMessage(String id) {
    List<String> messages = new ArrayList<>();
    if (listMessages.containsKey(id)) {
      messages = listMessages.get(id);
    }

    return messages;
  }

  public static String getPrefix() {
    if (prefixCache == null) {
      prefixCache = getColoredString("PREFIX") + " ";
    }
    return prefixCache;
  }

  private static String findMessageWithId(String id) {
    if (messages.containsKey(id)) {
      return messages.get(id);
    }
    return id;
  }
}

