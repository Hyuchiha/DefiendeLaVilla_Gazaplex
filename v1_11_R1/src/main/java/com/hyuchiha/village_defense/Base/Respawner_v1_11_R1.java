package com.hyuchiha.village_defense.Base;

import net.minecraft.server.v1_11_R1.EntityPlayer;
import net.minecraft.server.v1_11_R1.PacketPlayInClientCommand;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Respawner_v1_11_R1 implements Respawner{
    @Override
    public void respawm(Player player) {
        PacketPlayInClientCommand in = new PacketPlayInClientCommand(
                PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN);
        EntityPlayer cPlayer = ((CraftPlayer) player).getHandle();
        cPlayer.playerConnection.a(in);
    }
}
