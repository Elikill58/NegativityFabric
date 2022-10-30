package com.elikill58.negativity.fabric.nms;

import com.elikill58.negativity.api.packets.nms.VersionAdapter;
import com.elikill58.negativity.api.packets.nms.channels.AbstractChannel;
import com.elikill58.negativity.api.packets.nms.channels.netty.NettyChannel;
import com.elikill58.negativity.universal.utils.ReflectionUtils;

import io.netty.channel.Channel;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerPlayerEntity;

public class Fabric_1_18_2 extends VersionAdapter<ServerPlayerEntity> {

	public Fabric_1_18_2() {
		super("v1_18_2");
	}

	@Override
	public AbstractChannel getPlayerChannel(ServerPlayerEntity p) {
		try {
			return new NettyChannel(ReflectionUtils.getFirstWith(p.networkHandler.connection, ClientConnection.class, Channel.class));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
