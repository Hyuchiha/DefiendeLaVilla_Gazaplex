package com.hyuchiha.village_defense.Base;

import net.minecraft.server.v1_13_R2.EntityPlayer;
import net.minecraft.server.v1_13_R2.PacketPlayInClientCommand;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Respawner_v1_13_R2 implements Respawner{
  @Override
  public void respawm(Player player) {
    PacketPlayInClientCommand in = new PacketPlayInClientCommand(
        PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN);
    EntityPlayer cPlayer = ((CraftPlayer) player).getHandle();
    cPlayer.playerConnection.a(in);
  }
}
