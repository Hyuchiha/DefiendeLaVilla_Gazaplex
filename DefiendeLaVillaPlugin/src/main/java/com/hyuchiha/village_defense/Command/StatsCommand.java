package com.hyuchiha.village_defense.Command;

import com.hyuchiha.village_defense.Database.Base.Account;
import com.hyuchiha.village_defense.Database.StatType;
import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Manager.PlayerManager;
import com.hyuchiha.village_defense.Messages.Translator;
import com.hyuchiha.village_defense.Output.Output;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class StatsCommand implements CommandExecutor {

    private Main plugin;

    public StatsCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            if (args.length > 0){
                Player playerToCheck = Bukkit.getPlayer(args[0]);

                if(playerToCheck != null){
                    listStats((Player) sender, playerToCheck.getName(), StatType.values());
                }else {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                    if(offlinePlayer != null && offlinePlayer.hasPlayedBefore()){
                        listStats((Player) sender, offlinePlayer.getName(), StatType.values());
                    }else {
                        sender.sendMessage(ChatColor.RED + Translator.string("ERROR_PLAYER_NOT_FOUND"));
                    }
                }

            }else{
                listStats((Player) sender);
            }
        }else {
            sender.sendMessage(ChatColor.RED + Translator.string("ERROR_CONSOLE_PLAYERCOMMAND"));
        }

        return true;
    }

    private void listStats(Player player) {
        listStats(player, player.getName(), StatType.values());
    }

    private void listStats(Player sender, String player, StatType[] stats) {
        String GRAY = ChatColor.GRAY.toString();
        String DARK_AQUA = ChatColor.DARK_AQUA.toString();
        String AQUA = ChatColor.AQUA.toString();
        sender.sendMessage(GRAY + "=========[ " + DARK_AQUA + Translator.string("INFO_COMMAND_STATS") + GRAY
                + " ]=========");

        sender.sendMessage(GRAY + "=========  " + AQUA + player + GRAY + "  =========");

        for (StatType stat : stats) {
            String name = WordUtils.capitalize(stat.name().toLowerCase()
                    .replace('_', ' '));

            sender.sendMessage(DARK_AQUA + name + ": " + AQUA
                    + getStat(stat, player));
        }

        sender.sendMessage(GRAY + "=========================");
    }


    private int getStat(StatType statType, String playerName){
       Account account = getPlayerAccount(playerName);

       if(account != null){
           switch (statType){
               case KILLS:
                   return account.getKills();
               case DEATHS:
                   return account.getDeaths();
               case BOSSES_KILLS:
                   return account.getBosses_kills();
               case MAX_WAVE_REACHED:
                   return account.getMax_wave_reached();
               case MIN_WAVE_REACHED:
                   return account.getMin_wave_reached();
           }
       }

       return 0;
    }

    private Account getPlayerAccount(String playerName){
        Player player = Bukkit.getPlayer(playerName);

        if(player != null){
            Account onlineAccount = plugin.getDatabase().getAccount(
                    player.getUniqueId().toString(),
                    player.getName()
            );

            if(onlineAccount != null){
                return onlineAccount;
            }

        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);

        if(offlinePlayer != null){
            Account offlineAccount = plugin.getDatabase().getAccount(
                    offlinePlayer.getUniqueId().toString(),
                    offlinePlayer.getName()
            );

            if(offlineAccount != null){
                return offlineAccount;
            }
        }


        return null;
    }
}
