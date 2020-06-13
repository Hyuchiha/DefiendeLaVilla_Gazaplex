package com.hyuchiha.village_defense.Database.Base;

import com.hyuchiha.village_defense.Database.StatType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

public abstract class Database {
  private final Plugin plugin;
  protected final HashMap<String, Account> cachedAccounts;

  public Database(Plugin plugin) {
    this.plugin = plugin;

    this.cachedAccounts = new HashMap<>();
  }

  public boolean init() {
    return false;
  }

  public List<Account> getTopKillsAccounts(int size) {
    List<Account> topAccounts = loadTopAccountsByStatType(StatType.KILLS, size * 2);

    Collections.sort(topAccounts, new Comparator<Account>() {
      public int compare(Account account1, Account account2) {
        return (account2.getKills() - account1.getKills());
      }
    });

    if (topAccounts.size() > size) {
      topAccounts = topAccounts.subList(0, size);
    }

    return topAccounts;
  }

  public List<Account> getTopDeathsAccounts(int size) {
    List<Account> topAccounts = loadTopAccountsByStatType(StatType.DEATHS, size * 2);

    Collections.sort(topAccounts, new Comparator<Account>() {
      public int compare(Account account1, Account account2) {
        return (account2.getDeaths() - account1.getDeaths());
      }
    });

    if (topAccounts.size() > size) {
      topAccounts = topAccounts.subList(0, size);
    }

    return topAccounts;
  }

  public List<Account> getTopBossesAccounts(int size) {
    List<Account> topAccounts = loadTopAccountsByStatType(StatType.BOSSES_KILLS, size * 2);

    Collections.sort(topAccounts, new Comparator<Account>() {
      public int compare(Account account1, Account account2) {
        return (account2.getBosses_kills() - account1.getBosses_kills());
      }
    });

    if (topAccounts.size() > size) {
      topAccounts = topAccounts.subList(0, size);
    }

    return topAccounts;
  }

  public List<Account> getTopMaxWaveAccounts(int size) {
    List<Account> topAccounts = loadTopAccountsByStatType(StatType.MAX_WAVE_REACHED, size * 2);

    Collections.sort(topAccounts, new Comparator<Account>() {
      public int compare(Account account1, Account account2) {
        return (account2.getMax_wave_reached() - account1.getMax_wave_reached());
      }
    });

    if (topAccounts.size() > size) {
      topAccounts = topAccounts.subList(0, size);
    }

    return topAccounts;
  }

  public List<Account> getTopMinWaveAccounts(int size) {
    List<Account> topAccounts = loadTopAccountsByStatType(StatType.MIN_WAVE_REACHED, size * 2);

    Collections.sort(topAccounts, new Comparator<Account>() {
      public int compare(Account account1, Account account2) {
        return (account2.getMin_wave_reached() - account1.getMin_wave_reached());
      }
    });

    if (topAccounts.size() > size) {
      topAccounts = topAccounts.subList(0, size);
    }

    return topAccounts;
  }

  /**
   * Database code for loading based in stats
   **/
  protected abstract List<Account> loadTopAccountsByStatType(StatType type, int size);

  /**
   * Database code for create the account registry
   **/
  protected abstract void createAccountAndAddToDatabase(Account account);

  /**
   * Database code for load account data
   **/
  protected abstract Account loadAccount(String uuid);

  /**
   * Database code for update account data
   **/
  public abstract void saveAccount(Account account);

  public abstract void addUnlockedKit(String uuid, String kit);

  /**
   * Called every time we need the acount
   **/
  public Account getAccount(String uuid, String name) {
    Account account = getCachedAccount(uuid, name);

    if (account != null) {
      return account;
    }

    Account loadedAccount = loadAccount(uuid);

    if (loadedAccount != null) {
      loadedAccount.setName(name);
      return loadedAccount;
    }

    return null;
  }

  /**
   * Called when login user
   **/
  public Account createAccount(String uuid, String name) {
    Account account = getAccount(uuid, name);

    if (account == null) {
      account = createAndAddAccount(uuid, name);
    }

    return account;
  }

  public boolean removeCachedAccount(Account account) {
    Account removed = cachedAccounts.remove(account.getUUID());
    return removed != null;
  }

  private Account createAndAddAccount(String uuid, String name) {
    Account account = new Account(uuid, name);

    Player player = plugin.getServer().getPlayer(UUID.fromString(uuid));

    if (player != null) {
      createAccountAndAddToDatabase(account);
      cachedAccounts.put(account.getUUID(), account);
    }

    return account;
  }

  private Account getCachedAccount(String uuid, String name) {
    Account account = cachedAccounts.get(uuid);

    if (account != null) {
      account.setName(name);
    }

    return account;
  }

  public void close() {
    Collection<Account> collection = cachedAccounts.values();

    for (Account account : collection) {
      saveAccount(account);
    }
  }

}
