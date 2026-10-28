package com.piissedpanda.createairspace.block.Hangar;

import java.util.Map;

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

// Corrugated hangar door panel. Panels next to each other look like one big door and are moved
// with Create's gantries, pistons or rope pulleys; the frame only shows on the outer sides.
public class HangarDoorBlock extends Block implements IWrenchable {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty UP = BlockStateProperties.UP, DOWN = BlockStateProperties.DOWN,
			LEFT = BooleanProperty.create("left"), RIGHT = BooleanProperty.create("right");
	public static final Map<String, BooleanProperty> SIDES = Map.of("up", UP, "down", DOWN, "left", LEFT, "right",
			RIGHT);

	public HangarDoorBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH)
				.setValue(UP, false)
				.setValue(DOWN, false)
				.setValue(LEFT, false)
				.setValue(RIGHT, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder.add(FACING, UP, DOWN, LEFT, RIGHT));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		BlockState state = defaultBlockState().setValue(FACING, pContext.getHorizontalDirection()
				.getOpposite());
		return connectAll(state, pContext.getLevel(), pContext.getClickedPos());
	}

	@Override
	public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState,
			LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
		return connectAll(pState, pLevel, pPos);
	}

	private static BlockState connectAll(BlockState state, BlockGetter level, BlockPos pos) {
		Direction facing = state.getValue(FACING);
		return state.setValue(UP, connects(state, level, pos.above()))
				.setValue(DOWN, connects(state, level, pos.below()))
				.setValue(LEFT, connects(state, level, pos.relative(facing.getCounterClockWise())))
				.setValue(RIGHT, connects(state, level, pos.relative(facing.getClockWise())));
	}

	// Plain panels and window panels join each other
	private static boolean connects(BlockState state, BlockGetter level, BlockPos pos) {
		BlockState other = level.getBlockState(pos);
		return other.getBlock() instanceof HangarDoorBlock && other.getValue(FACING)
				.getAxis() == state.getValue(FACING)
						.getAxis();
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return CAShapes.HANGAR_DOOR.get(pState.getValue(FACING));
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
