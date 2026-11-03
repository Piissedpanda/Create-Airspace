package com.piissedpanda.createairspace.block.Cargo;

import com.piissedpanda.createairspace.CABlockEntities;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

// Sits in the wall next to a container: funnels and chutes at its back reach the
// container in front of it, a wrench switches between loading and unloading
public class ContainerPortBlock extends Block implements IBE<ContainerPortBlockEntity>, IWrenchable {

	public static final BooleanProperty UNLOADING = BooleanProperty.create("unloading");

	public ContainerPortBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH)
				.setValue(UNLOADING, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(HorizontalDirectionalBlock.FACING, UNLOADING));
	}

	// Faces away from the player, towards where the container goes
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection());
	}

	@Override
	public InteractionResult onWrenched(BlockState state, UseOnContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		if (!level.isClientSide) {
			level.setBlock(pos, state.cycle(UNLOADING), Block.UPDATE_ALL);
			level.invalidateCapabilities(pos);
			AllSoundEvents.WRENCH_ROTATE.playOnServer(level, pos, 1, 1.2f);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	protected BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(HorizontalDirectionalBlock.FACING,
				rotation.rotate(state.getValue(HorizontalDirectionalBlock.FACING)));
	}

	@Override
	protected BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(HorizontalDirectionalBlock.FACING)));
	}

	@Override
	public Class<ContainerPortBlockEntity> getBlockEntityClass() {
		return ContainerPortBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends ContainerPortBlockEntity> getBlockEntityType() {
		return CABlockEntities.CONTAINER_PORT.get();
	}

}
