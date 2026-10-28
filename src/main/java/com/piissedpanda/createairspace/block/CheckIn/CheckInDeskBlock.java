package com.piissedpanda.createairspace.block.CheckIn;

import com.piissedpanda.createairspace.CAShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;

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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

// Check-in counter facing the passengers. Desks side by side form one counter, end panels only outside.
public class CheckInDeskBlock extends Block implements IWrenchable {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty LEFT = BooleanProperty.create("left"),
			RIGHT = BooleanProperty.create("right");

	public CheckInDeskBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH)
				.setValue(LEFT, false)
				.setValue(RIGHT, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder.add(FACING, LEFT, RIGHT));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		BlockState state = defaultBlockState().setValue(FACING, pContext.getHorizontalDirection()
				.getOpposite());
		return connect(state, pContext.getLevel(), pContext.getClickedPos());
	}

	@Override
	public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState,
			LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
		return connect(pState, pLevel, pPos);
	}

	// Left and right as modelled facing north: west and east
	private BlockState connect(BlockState state, BlockGetter level, BlockPos pos) {
		Direction facing = state.getValue(FACING);
		return state.setValue(LEFT, continues(state, level.getBlockState(pos.relative(facing.getCounterClockWise()))))
				.setValue(RIGHT, continues(state, level.getBlockState(pos.relative(facing.getClockWise()))));
	}

	private boolean continues(BlockState state, BlockState neighbour) {
		return neighbour.is(this) && neighbour.getValue(FACING) == state.getValue(FACING);
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return CAShapes.CHECK_IN_DESK.get(pState.getValue(FACING));
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
