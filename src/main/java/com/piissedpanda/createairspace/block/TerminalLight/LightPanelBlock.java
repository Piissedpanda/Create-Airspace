package com.piissedpanda.createairspace.block.TerminalLight;

import java.util.Map;

import com.piissedpanda.createairspace.CABlocks;
import com.piissedpanda.createairspace.CAShapes;
import com.simibubi.create.foundation.block.DyedBlockList;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
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

// Flat light on a ceiling or wall. Panels side by side join into one light surface,
// the frame only shows where no panel follows. Sides are stored as seen in the model (facing down).
public class LightPanelBlock extends TerminalLightBlock {

	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final Map<Direction, BooleanProperty> CONNECTED = Map.of(Direction.NORTH,
			BooleanProperty.create("top"), Direction.EAST, BooleanProperty.create("right"), Direction.SOUTH,
			BooleanProperty.create("bottom"), Direction.WEST, BooleanProperty.create("left"));

	public LightPanelBlock(Properties properties, DyeColor color) {
		super(properties, color);
		BlockState state = defaultBlockState().setValue(FACING, Direction.DOWN);
		for (BooleanProperty property : CONNECTED.values())
			state = state.setValue(property, false);
		registerDefaultState(state);
	}

	@Override
	protected DyedBlockList<LightPanelBlock> variants() {
		return CABlocks.LIGHT_PANELS;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder.add(FACING)
				.add(CONNECTED.values()
						.toArray(BooleanProperty[]::new)));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		BlockState state = defaultBlockState().setValue(FACING, pContext.getClickedFace());
		for (Direction side : CONNECTED.keySet())
			state = connect(state, pContext.getLevel(), pContext.getClickedPos(), side);
		return withLevel(state, pContext);
	}

	@Override
	public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState,
			LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
		for (Direction side : CONNECTED.keySet())
			if (toWorld(pState.getValue(FACING), side) == pDirection)
				return connect(pState, pLevel, pPos, side);
		return pState;
	}

	private BlockState connect(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
		Direction facing = state.getValue(FACING);
		BlockState neighbour = level.getBlockState(pos.relative(toWorld(facing, side)));
		boolean connected = neighbour.is(this) && neighbour.getValue(FACING) == facing;
		return state.setValue(CONNECTED.get(side), connected);
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return CAShapes.LIGHT_PANEL.get(pState.getValue(FACING));
	}

}
