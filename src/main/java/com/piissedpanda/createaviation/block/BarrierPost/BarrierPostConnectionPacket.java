package com.piissedpanda.createaviation.block.BarrierPost;

import com.piissedpanda.createaviation.CAPackets;
import com.piissedpanda.createaviation.foundation.config.CAConfigs;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;

public class BarrierPostConnectionPacket extends BlockEntityConfigurationPacket<BarrierPostBlockEntity> {
    public static final StreamCodec<RegistryFriendlyByteBuf, BarrierPostConnectionPacket> STREAM_CODEC = StreamCodec
            .composite(
                    BlockPos.STREAM_CODEC, packet -> packet.pos,
                    BlockPos.STREAM_CODEC, packet -> packet.targetPos,
                    ItemStack.STREAM_CODEC, packet -> packet.belt,
                    ByteBufCodecs.BOOL, packet -> packet.connect,
                    BarrierPostConnectionPacket::new);

    private final BlockPos targetPos;
    private final ItemStack belt;
    private final boolean connect;

    public BarrierPostConnectionPacket(BlockPos pos, BlockPos targetPos, ItemStack belt, boolean connect) {
        super(pos);
        this.targetPos = targetPos;
        this.belt = belt;
        this.connect = connect;
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return CAPackets.BARRIER_POST_CONNECT;
    }

    @Override
    protected int maxRange() {
        return CAConfigs.server().maxBarrierPostLength.get() + 16;
    }

    @Override
    protected void applySettings(ServerPlayer player, BarrierPostBlockEntity be) {
        if (!be.getBlockPos()
                .closerThan(targetPos, maxRange() - 16 + 1))
            return;
        if (!(be.getLevel()
                .getBlockEntity(targetPos) instanceof BarrierPostBlockEntity clbe))
            return;

        if (!connect) {
            be.beltDestroyed(targetPos.subtract(be.getBlockPos()), true);
            be.getLevel()
                    .playSound(null, player.blockPosition(), SoundEvents.LEASH_KNOT_BREAK, SoundSource.BLOCKS);
        }

        if (connect) {
            if (!clbe.addConnectionTo(be.getBlockPos()))
                return;
        } else
            clbe.removeConnectionTo(be.getBlockPos());

        if (connect) {
            if (!be.addConnectionTo(targetPos))
                clbe.removeConnectionTo(be.getBlockPos());
        } else
            be.removeConnectionTo(targetPos);
    }
}
