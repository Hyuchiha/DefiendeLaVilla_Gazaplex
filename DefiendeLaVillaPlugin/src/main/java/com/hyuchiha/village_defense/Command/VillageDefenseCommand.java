/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyuchiha.village_defense.Command;

import com.hyuchiha.village_defense.Arena.Arena;
import com.hyuchiha.village_defense.Event.ArenaJoinEvent;
import com.hyuchiha.village_defense.Event.ArenaLeaveEvent;
import com.hyuchiha.village_defense.Game.GamePlayer;
import com.hyuchiha.village_defense.Game.PlayerState;
import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Manager.ArenaManager;
import com.hyuchiha.village_defense.Manager.PlayerManager;
import com.hyuchiha.village_defense.Messages.Translator;
import com.hyuchiha.village_defense.Output.Output;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author hyuchiha
 */
public class VillageDefenseCommand implements CommandExecutor {

    private Main plugin;

    public VillageDefenseCommand(Main main) {
        this.plugin = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(Translator.getPrefix() + "/vd join [arena]");
            sender.sendMessage(Translator.getPrefix() + "/vd leave");
            sender.sendMessage(Translator.getPrefix() + "/vd spect [arena]");
            //sender.sendMessage(plugin.getPrefix() + "/vd arena");

            return true;
        } else {
            switch (args[0]) {
                case "join":
                    try {
                        String arenaName = args[1];
                        Arena arena = ArenaManager.getArenaConfiguration(arenaName);
                        if (sender instanceof Player) {
                            Player player = (Player) sender;

                            if (arena == null) {
                                player.sendMessage(Translator.getPrefix() + " " + Translator.getColoredString("CANT_JOIN_ARENA"));
                                Output.logError("Se intento ingresa a la arena " + arenaName + " pero retorno null");
                                return true;
                            }

                            GamePlayer playerjoin = PlayerManager.getPlayer(player);

                            Bukkit.getPluginManager().callEvent(new ArenaJoinEvent(arenaName, playerjoin));

                        } else {
                            Output.log("Solo puedes unirte si eres un jugador");
                            return false;
                        }

                    } catch (Exception e) {
                        Output.logError(e.getMessage());
                    }

                    break;
                case "leave":
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        GamePlayer playervd = PlayerManager.getPlayer(player);

                        if (playervd.getState() == PlayerState.INGAME || playervd.getState() == PlayerState.LOBBY_GAME) {
                            Bukkit.getPluginManager().callEvent(new ArenaLeaveEvent(playervd, playervd.getArena()));
                        } else {
                            player.sendMessage(Translator.getPrefix() + " " + Translator.getColoredString("YOU_ARE_IN_GAME"));
                        }
                    }
                    break;
                case "spect":
                    try {
                        String arenaName = args[1];
                        Arena arena = ArenaManager.getArenaConfiguration(arenaName);
                        if (sender instanceof Player) {
                            Player player = (Player) sender;

                            if (arena == null) {
                                player.sendMessage(Translator.getPrefix() + " " + Translator.getColoredString("CANT_JOIN_ARENA"));
                                Output.logError("Se intento ingresa a la arena " + arenaName + " pero retorno null");
                                return true;
                            }

                            //Verificarse que tenga permiso para jugar
                            if (!player.getPlayer().hasPermission("VD.Player.spect")) {
                                player.getPlayer().sendMessage(Translator.getPrefix() + " " +
                                        Translator.getColoredString("DONT_HAVE_PERMISSION_TO_SPECT"));
                                return false;
                            } else {
                                GamePlayer playervd = PlayerManager.getPlayer(player);
                                arena.getGame().addSpectator(playervd);
                            }

                        } else {
                            Output.log("Solo puedes unirte si eres un jugador");
                            return false;
                        }
                    } catch (Exception e) {
                        Output.logError(e.getMessage());
                    }
                    break;
                default:

                    break;
            }
        }

        return false;
    }

}
