package com.elikill58.negativity.fabric.payload;

import com.elikill58.negativity.fabric.FabricNegativity;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record BungeecordSendToServerMessagePayload(String server) implements CustomPayload {
	
	public static final PacketCodec<PacketByteBuf, BungeecordSendToServerMessagePayload> CODEC = PacketCodec.tuple(
			PacketCodecs.STRING, BungeecordSendToServerMessagePayload::server,
			BungeecordSendToServerMessagePayload::new
	);

	@Override
	public Id<? extends CustomPayload> getId() {
		return FabricNegativity.ID_BUNGEECORD;
	}
}
