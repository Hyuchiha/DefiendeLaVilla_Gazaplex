package com.hyuchiha.village_defense.Command;

import com.google.common.base.Enums;
import com.hyuchiha.village_defense.Database.Base.Account;
import com.hyuchiha.village_defense.Database.StatType;
import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Messages.Translator;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TopsCommand implements CommandExecutor {

  private Main plugin;

  public TopsCommand(Main plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

    if (sender instanceof Player) {
      if (args.length > 0) {

        StatType stat = Enums.getIfPresent(StatType.class, args[0].toUpperCase()).orNull();

        if (stat != null) {
          listTopStat((Player) sender, stat);
        } else {
          sender.sendMessage(ChatColor.RED + Translator.getColoredString("ERROR.STAT_NOT_FOUND"));
        }

      } else {
        sender.sendMessage(Translator.getPrefix() + " /top [KILLS, DEATHS, BOSSES_KILLS, MAX_WAVE_REACHED, MIN_WAVE_REACHED]");
      }
    } else {
      sender.sendMessage(ChatColor.RED + Translator.getColoredString("ERROR.CONSOLE_PLAYER_COMMAND"));
      return true;
    }

    return true;
  }

  private void listTopStat(Player sender, StatType stat) {
    String GRAY = ChatColor.GRAY.toString();
    String DARK_AQUA = ChatColor.DARK_AQUA.toString();
    String AQUA = ChatColor.AQUA.toString();

    sender.sendMessage(GRAY + "=========[ " + DARK_AQUA +  Translator.getString("INFO.TOP") + " " + stat.name() + GRAY
        + " ]=========");

    List<Account> tops = new ArrayList<>();

    switch (stat) {
      case KILLS:
        tops = plugin.getDatabase().getTopKillsAccounts(10);
        break;
      case DEATHS:
        tops = plugin.getDatabase().getTopDeathsAccounts(10);
        break;
      case BOSSES_KILLS:
        tops = plugin.getDatabase().getTopBossesAccounts(10);
        break;
      case MIN_WAVE_REACHED:
        tops = plugin.getDatabase().getTopMinWaveAccounts(10);
        break;
      case MAX_WAVE_REACHED:
        tops = plugin.getDatabase().getTopMaxWaveAccounts(10);
        break;
    }

    for (Account account : tops) {
      int statValue = getPlayerStat(stat, account);

      String resultPlayer = DARK_AQUA + account.getName() + " - " + AQUA + statValue;

      sender.sendMessage(resultPlayer);
    }

    sender.sendMessage(GRAY + "=========================");
  }

  private int getPlayerStat(StatType stat, Account account) {
    switch (stat) {
      case KILLS:
        return account.getKills();
      case DEATHS:
        return account.getDeaths();
      case BOSSES_KILLS:
        return account.getBosses_kills();
      case MIN_WAVE_REACHED:
        return account.getMin_wave_reached();
      case MAX_WAVE_REACHED:
        return account.getMax_wave_reached();
    }

    return 0;
  }
}
