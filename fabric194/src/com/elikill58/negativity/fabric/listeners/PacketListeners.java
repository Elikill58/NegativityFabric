package com.elikill58.negativity.fabric.listeners;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.packets.nms.channels.netty.NettyPacketListener;
import com.elikill58.negativity.fabric.impl.entity.FabricEntityManager;
import com.elikill58.negativity.universal.utils.ReflectionUtils;

import io.netty.channel.Channel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class PacketListeners extends NettyPacketListener {

	public PacketListeners() {
		ServerPlayConnectionEvents.JOIN.register(this::onPlayerJoin);
		ServerPlayConnectionEvents.DISCONNECT.register(this::onLeave);
	}

	public void onPlayerJoin(ServerPlayNetworkHandler e, PacketSender sender, MinecraftServer srv) {
		join(FabricEntityManager.getPlayer(e.player));
	}

	public void onLeave(ServerPlayNetworkHandler e, MinecraftServer srv) {
		left(FabricEntityManager.getPlayer(e.player));
	}

	@Override
	public Channel getChannel(Player p) {
		try {
			return ReflectionUtils.getFirstWith(((ServerPlayerEntity) p.getDefault()).networkHandler.getConnectionAddress(), ClientConnection.class, Channel.class);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
