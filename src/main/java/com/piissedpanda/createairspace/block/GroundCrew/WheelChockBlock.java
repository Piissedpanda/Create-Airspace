package com.piissedpanda.createairspace.block.GroundCrew;

import com.piissedpanda.createairspace.CAShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

// Two chocks on a rope, decoration until aircraft wheels can be held (phase 3)
public class WheelChockBlock extends Block implements IWrenchable, ProperWaterloggedBlock {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	public WheelChockBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH)
				.setValue(WATERLOGGED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder.add(FACING, WATERLOGGED));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return withWater(defaultBlockState().setValue(FACING, pContext.getHorizontalDirection()), pContext);
	}

	@Override
	public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState,
			LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
		updateWater(pLevel, pState, pPos);
		return pState;
	}

	@Override
	public FluidState getFluidState(BlockState pState) {
		return fluidState(pState);
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return CAShapes.WHEEL_CHOCK.get(pState.getValue(FACING));
	}

	@Override
	public BlockState rotate(BlockState pState, Rotation pRotation) {
		return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState pState, Mirror pMirror) {
		return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
	}

}
