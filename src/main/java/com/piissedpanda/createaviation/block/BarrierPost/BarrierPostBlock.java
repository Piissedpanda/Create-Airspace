package com.piissedpanda.createaviation.block.BarrierPost;

import com.piissedpanda.createaviation.CABlockEntities;
import com.piissedpanda.createaviation.CAShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.IHaveBigOutline;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.Tags;

public class BarrierPostBlock extends Block
        implements IWrenchable, IBE<BarrierPostBlockEntity>, IHaveBigOutline {

    public BarrierPostBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return CAShapes.BARRIER_POST_INTERACTION;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return CAShapes.BARRIER_POST_INTERACTION;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos,
            CollisionContext pContext) {
        return CAShapes.BARRIER_POST_INTERACTION;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide() && stack.is(Tags.Items.TOOLS_WRENCH) && !player.isShiftKeyDown())
            return ItemInteractionResult.SUCCESS;
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        IBE.onRemove(pState, pLevel, pPos, pNewState);
    }

    @Override
    public Class<BarrierPostBlockEntity> getBlockEntityClass() {
        return BarrierPostBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends BarrierPostBlockEntity> getBlockEntityType() {
        return CABlockEntities.BARRIER_POST.get();
    }

}
