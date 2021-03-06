package com.hyuchiha.village_defense.Listeners;

import com.hyuchiha.village_defense.Game.GamePlayer;
import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Manager.PlayerManager;
import com.hyuchiha.village_defense.Messages.Translator;
import com.hyuchiha.village_defense.MessagesApi.TitleAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * @author hyuchiha
 */
public class JoinListener implements Listener {

  private final Main plugin;

  public JoinListener(Main main) {
    this.plugin = main;
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent e) {
    e.setJoinMessage("");
    final Player player = e.getPlayer();

    player.sendMessage(Translator.getPrefix() + Translator.getColoredString("GAME.PLAYER_JOIN_MESSAGE"));
    GamePlayer vdplayer = PlayerManager.getPlayer(player);

    plugin.getMainDatabase().createAccount(player.getUniqueId().toString(), player.getName());

    //Se envia el jugador al lobby principal
    vdplayer.sendPlayerToLobby();

    TitleAPI.send(player,
        Translator.getColoredString("TITLE.SERVER_JOIN_TITLE"),
        Translator.getColoredString("TITLE.SERVER_JOIN_SUBTITLE"),
        10,
        30,
        10);

  }

}
