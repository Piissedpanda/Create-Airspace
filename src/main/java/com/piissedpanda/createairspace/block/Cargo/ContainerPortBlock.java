package com.piissedpanda.createairspace.block.Cargo;

import com.piissedpanda.createairspace.CABlockEntities;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

// Sits in the wall next to a docked container, logistics at its other sides reach
// the container through the port point in front
public class ContainerPortBlock extends Block implements IBE<ContainerPortBlockEntity>, IWrenchable {

	public ContainerPortBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(HorizontalDirectionalBlock.FACING));
	}

	// Faces away from the player, towards where the container goes
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection());
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
