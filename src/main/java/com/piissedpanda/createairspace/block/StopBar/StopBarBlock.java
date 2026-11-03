package com.piissedpanda.createairspace.block.StopBar;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.piissedpanda.createairspace.CAShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

// Red inset lights across a taxiway, a whole row goes dark as soon as one of them gets a signal
public class StopBarBlock extends Block implements IWrenchable {

	public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	public static final int MAX_ROW = 64;

	public StopBarBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(AXIS, Direction.Axis.X)
				.setValue(LIT, true));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder.add(AXIS, LIT));
	}

	// The row runs across the view of the player
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return defaultBlockState().setValue(AXIS, pContext.getHorizontalDirection()
				.getClockWise()
				.getAxis());
	}

	@Override
	protected void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState,
			boolean pMovedByPiston) {
		if (!pOldState.is(this))
			updateRow(pLevel, pPos);
	}

	@Override
	protected void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock,
			BlockPos pNeighborPos, boolean pMovedByPiston) {
		updateRow(pLevel, pPos);
	}

	private void updateRow(Level level, BlockPos pos) {
		if (level.isClientSide)
			return;
		List<BlockPos> row = getRow(level, pos);
		boolean lit = true;
		for (BlockPos member : row)
			if (level.hasNeighborSignal(member))
				lit = false;
		for (BlockPos member : row) {
			BlockState state = level.getBlockState(member);
			if (state.getValue(LIT) != lit)
				level.setBlock(member, state.setValue(LIT, lit), 2);
		}
	}

	public static List<BlockPos> getRow(BlockGetter level, BlockPos pos) {
		List<BlockPos> row = new ArrayList<>();
		BlockState origin = level.getBlockState(pos);
		if (!(origin.getBlock() instanceof StopBarBlock))
			return row;
		Direction.Axis axis = origin.getValue(AXIS);
		row.add(pos);
		for (Direction.AxisDirection sign : Direction.AxisDirection.values()) {
			Direction side = Direction.fromAxisAndDirection(axis, sign);
			for (int i = 1; i < MAX_ROW; i++) {
				BlockPos next = pos.relative(side, i);
				BlockState state = level.getBlockState(next);
				if (!(state.getBlock() instanceof StopBarBlock) || state.getValue(AXIS) != axis)
					break;
				row.add(next);
			}
		}
		return row;
	}

	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos,
			@Nullable Direction direction) {
		return direction != null && direction.getAxis()
				.isHorizontal();
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return CAShapes.STOP_BAR;
	}

	@Override
	protected BlockState rotate(BlockState pState, Rotation pRotation) {
		if (pRotation == Rotation.NONE || pRotation == Rotation.CLOCKWISE_180)
			return pState;
		return pState.cycle(AXIS);
	}

	@Override
	protected boolean isPathfindable(BlockState pState, PathComputationType pPathComputationType) {
		return false;
	}

}
