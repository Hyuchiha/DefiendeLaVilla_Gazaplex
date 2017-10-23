package com.hyuchiha.village_defense.Output;

import com.hyuchiha.village_defense.Main;

public class Output
{
  public static void log(String msg)
  {
    Main.getInstance().getLogger().info(msg);
  }

  public static void logError(String msg)
  {
    Main.getInstance().getLogger().severe(msg);
  }
}