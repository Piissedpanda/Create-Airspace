package com.piissedpanda.createaviation.block.BarrierPost;

import com.piissedpanda.createaviation.CAPackets;
import com.piissedpanda.createaviation.foundation.config.CAConfigs;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public class BarrierPostDyeingPacket extends BlockEntityConfigurationPacket<BarrierPostBlockEntity> {
    public static final StreamCodec<RegistryFriendlyByteBuf, BarrierPostDyeingPacket> STREAM_CODEC = StreamCodec
            .composite(
                    BlockPos.STREAM_CODEC, packet -> packet.pos,
                    BlockPos.STREAM_CODEC, packet -> packet.targetPos,
                    ItemStack.STREAM_CODEC, packet -> packet.dye,
                    DyeColor.STREAM_CODEC, packet -> packet.dyeColor,
                    BarrierPostDyeingPacket::new);

    private final BlockPos targetPos;
    private final ItemStack dye;
    private final DyeColor dyeColor;

    public BarrierPostDyeingPacket(BlockPos pos, BlockPos targetPos, ItemStack dye, DyeColor color) {
        super(pos);
        this.targetPos = targetPos;
        this.dye = dye;
        this.dyeColor = color;
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return CAPackets.BARRIER_POST_DYE;
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

        be.setBeltColor(targetPos.subtract(pos), dyeColor);
        clbe.setBeltColor(pos.subtract(targetPos), dyeColor);

        be.getLevel().playSound(null, player.blockPosition(), SoundEvents.LEASH_KNOT_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);
    }
}
