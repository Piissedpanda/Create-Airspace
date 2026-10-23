package com.piissedpanda.createairspace;

import java.util.Locale;

import com.piissedpanda.createairspace.block.BarrierPost.BarrierPostConnectionPacket;
import com.piissedpanda.createairspace.block.BarrierPost.BarrierPostDyeingPacket;

import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.CatnipPacketRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public enum CAPackets implements BasePacketPayload.PacketTypeProvider {

    BARRIER_POST_CONNECT(BarrierPostConnectionPacket.class, BarrierPostConnectionPacket.STREAM_CODEC),
    BARRIER_POST_DYE(BarrierPostDyeingPacket.class, BarrierPostDyeingPacket.STREAM_CODEC);

    private final CatnipPacketRegistry.PacketType<?> type;

    <T extends BasePacketPayload> CAPackets(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        String name = this.name().toLowerCase(Locale.ROOT);
        this.type = new CatnipPacketRegistry.PacketType<>(
                new CustomPacketPayload.Type<>(CreateAirspace.asResource(name)),
                clazz, codec);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends CustomPacketPayload> CustomPacketPayload.Type<T> getType() {
        return (CustomPacketPayload.Type<T>) this.type.type();
    }

    public static void register() {
        CatnipPacketRegistry packetRegistry = new CatnipPacketRegistry(CreateAirspace.MODID, CreateAirspace.VERSION);
        for (CAPackets packet : CAPackets.values()) {
            packetRegistry.registerPacket(packet.type);
        }
        packetRegistry.registerAllPackets();
    }

}
