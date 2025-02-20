package com.elikill58.negativity.fabric.payload;

import com.elikill58.negativity.fabric.FabricNegativity;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record NegativityMessagePayload(byte[] content) implements CustomPayload {
	
	public static final PacketCodec<PacketByteBuf, NegativityMessagePayload> CODEC = PacketCodec.tuple(
			PacketCodecs.BYTE_ARRAY, NegativityMessagePayload::content,
			NegativityMessagePayload::new
	);

	@Override
	public Id<? extends CustomPayload> getId() {
		return FabricNegativity.ID_NEGATIVITY;
	}
}
