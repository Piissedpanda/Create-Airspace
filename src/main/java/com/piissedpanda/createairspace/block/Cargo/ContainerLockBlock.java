package com.piissedpanda.createairspace.block.Cargo;

import com.piissedpanda.createairspace.CABlockEntities;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

// Sits in the floor or the wall and holds a cargo container in front of it,
// redstone lets go of it
public class ContainerLockBlock extends Block implements IBE<ContainerLockBlockEntity>, IWrenchable {

	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final BooleanProperty LOCKED = BooleanProperty.create("locked");

	public ContainerLockBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(DirectionalBlock.FACING, Direction.UP)
				.setValue(POWERED, false)
				.setValue(LOCKED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(DirectionalBlock.FACING, POWERED, LOCKED));
	}

	// Faces away from the block it was placed on, towards where the container goes
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(DirectionalBlock.FACING, context.getClickedFace())
				.setValue(POWERED, context.getLevel()
						.hasNeighborSignal(context.getClickedPos()));
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos,
			boolean isMoving) {
		boolean powered = level.hasNeighborSignal(pos);
		if (powered != state.getValue(POWERED))
			level.setBlock(pos, state.setValue(POWERED, powered), Block.UPDATE_CLIENTS);
	}

	@Override
	protected boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
		return state.getValue(LOCKED) ? 15 : 0;
	}

	@Override
	protected BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(DirectionalBlock.FACING, rotation.rotate(state.getValue(DirectionalBlock.FACING)));
	}

	@Override
	protected BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(DirectionalBlock.FACING)));
	}

	@Override
	public Class<ContainerLockBlockEntity> getBlockEntityClass() {
		return ContainerLockBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends ContainerLockBlockEntity> getBlockEntityType() {
		return CABlockEntities.CONTAINER_LOCK.get();
	}

}
