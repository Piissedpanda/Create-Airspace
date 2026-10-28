package com.piissedpanda.createairspace.block.TerminalLight;

import com.piissedpanda.createairspace.CAShapes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

// Narrow light strip, strips in a line join and only the outer ends get a cap.
// VERTICAL: runs along the north-south side of the model (facing down), otherwise east-west.
public class LightStripBlock extends TerminalLightBlock {

	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final BooleanProperty VERTICAL = BooleanProperty.create("vertical");
	public static final BooleanProperty START = BooleanProperty.create("start");
	public static final BooleanProperty END = BooleanProperty.create("end");

	public LightStripBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.DOWN)
				.setValue(VERTICAL, false)
				.setValue(START, false)
				.setValue(END, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder.add(FACING, VERTICAL, START, END));
	}

	// Model side (facing down) where the strip starts, it ends on the other side
	public static Direction startSide(BlockState state) {
		return state.getValue(VERTICAL) ? Direction.NORTH : Direction.WEST;
	}

	// Floors and ceilings: along the look direction. Walls: sideways, sneaking: up
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		Direction facing = pContext.getClickedFace();
		Direction along = pContext.getHorizontalDirection();
		if (facing.getAxis()
				.isHorizontal())
			along = pContext.isSecondaryUseActive() ? Direction.UP : facing.getClockWise();

		BlockState state = defaultBlockState().setValue(FACING, facing);
		state = state.setValue(VERTICAL, toWorld(facing, Direction.NORTH).getAxis() == along.getAxis());
		return withLevel(connect(state, pContext.getLevel(), pContext.getClickedPos()), pContext);
	}

	@Override
	public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState,
			LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
		return connect(pState, pLevel, pPos);
	}

	private BlockState connect(BlockState state, BlockGetter level, BlockPos pos) {
		Direction start = toWorld(state.getValue(FACING), startSide(state));
		return state.setValue(START, continues(state, level.getBlockState(pos.relative(start))))
				.setValue(END, continues(state, level.getBlockState(pos.relative(start.getOpposite()))));
	}

	private boolean continues(BlockState state, BlockState neighbour) {
		return neighbour.is(this) && neighbour.getValue(FACING) == state.getValue(FACING)
				&& neighbour.getValue(VERTICAL) == state.getValue(VERTICAL);
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return CAShapes.LIGHT_PANEL.get(pState.getValue(FACING));
	}

}
