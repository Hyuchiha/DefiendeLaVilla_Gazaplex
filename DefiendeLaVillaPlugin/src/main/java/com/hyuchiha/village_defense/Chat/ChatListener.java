package com.hyuchiha.village_defense.Chat;

import com.hyuchiha.village_defense.Game.GamePlayer;
import com.hyuchiha.village_defense.Game.PlayerState;
import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Manager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private final Main plugin;

    public ChatListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        final Player sender = e.getPlayer();
        boolean All = false;
        GamePlayer meta = PlayerManager.getPlayer(sender);

        if (e.isCancelled()) {
            return;
        }

        String msg = e.getMessage();
        if (e.getMessage().startsWith("!") && !e.getMessage().equalsIgnoreCase("!")) {
            msg = msg.substring(1);
            All = true;
        }

        if (All) {
            e.setFormat(ChatUtil.allMessage(sender) + msg);
        } else {
            if (PlayerManager.getPlayer(sender).getState() == PlayerState.LOBBY) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    GamePlayer metap = PlayerManager.getPlayer(p);
                    if (metap.getState() != PlayerState.LOBBY) {
                        e.getRecipients().remove(p);
                    }
                }

                e.setFormat(ChatUtil.allMessage(sender) + msg);

            } else {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    GamePlayer metap = PlayerManager.getPlayer(p);

                    if (metap.getArena() == null) {
                        e.getRecipients().remove(p);
                    } else {
                        if (!metap.getArena().getName().equals(meta.getArena().getName())) {
                            e.getRecipients().remove(p);
                        }
                    }
                }

                e.setFormat(ChatUtil.prefixMessage(sender) + msg);
            }
        }

    }

}
