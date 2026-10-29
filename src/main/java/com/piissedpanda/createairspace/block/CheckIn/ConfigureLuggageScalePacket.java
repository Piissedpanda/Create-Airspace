package com.piissedpanda.createairspace.block.CheckIn;

import com.piissedpanda.createairspace.CAPackets;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

// Sends the weight range from the scale screen to the server
public class ConfigureLuggageScalePacket extends BlockEntityConfigurationPacket<LuggageScaleBlockEntity> {

	public static final StreamCodec<ByteBuf, ConfigureLuggageScalePacket> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, packet -> packet.pos,
			ByteBufCodecs.INT, packet -> packet.minKg,
			ByteBufCodecs.INT, packet -> packet.maxKg,
			ConfigureLuggageScalePacket::new);

	private final int minKg, maxKg;

	public ConfigureLuggageScalePacket(BlockPos pos, int minKg, int maxKg) {
		super(pos);
		this.minKg = minKg;
		this.maxKg = maxKg;
	}

	@Override
	protected void applySettings(ServerPlayer player, LuggageScaleBlockEntity be) {
		be.setRange(minKg, maxKg);
	}

	@Override
	public PacketTypeProvider getTypeProvider() {
		return CAPackets.CONFIGURE_LUGGAGE_SCALE;
	}

}
