package com.therandomlabs.randompatches.hook;

import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.util.BlockPos;

public final class ItemInWorldManagerHook {

    private ItemInWorldManagerHook() {}

    public static void sendBlockChangePacket(ItemInWorldManager manager, BlockPos pos) {
        manager.thisPlayerMP.playerNetServerHandler.sendPacket(new S23PacketBlockChange(manager.theWorld, pos));
    }
}