package com.piissedpanda.createairspace.block.Hangar;

import java.util.HashMap;
import java.util.Map;

import com.simibubi.create.content.equipment.wrench.IWrenchable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

// Grate platform to stand on. Platforms and stairs next to it leave that side open,
// rails on the open sides, legs on the outer corners.
public class MaintenancePlatformBlock extends Block implements IWrenchable {

	public static final Map<Direction, BooleanProperty> CONNECTED = Map.of(Direction.NORTH, PipeBlock.NORTH,
			Direction.EAST, PipeBlock.EAST, Direction.SOUTH, PipeBlock.SOUTH, Direction.WEST, PipeBlock.WEST);

	private static final VoxelShape DECK = Block.box(0, 14, 0, 16, 16, 16);
	private static final Map<Direction, VoxelShape> RAILS = Map.of(Direction.NORTH, Block.box(0, 16, 0, 16, 24, 1),
			Direction.SOUTH, Block.box(0, 16, 15, 16, 24, 16), Direction.WEST, Block.box(0, 16, 0, 1, 24, 16),
			Direction.EAST, Block.box(15, 16, 0, 16, 24, 16));
	private final Map<BlockState, VoxelShape> shapes = new HashMap<>();

	public MaintenancePlatformBlock(Properties properties) {
		super(properties);
		BlockState state = defaultBlockState();
		for (BooleanProperty property : CONNECTED.values())
			state = state.setValue(property, false);
		registerDefaultState(state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder.add(CONNECTED.values()
				.toArray(BooleanProperty[]::new)));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		BlockState state = defaultBlockState();
		for (Direction side : Direction.Plane.HORIZONTAL)
			state = state.setValue(CONNECTED.get(side), opens(pContext.getLevel()
					.getBlockState(pContext.getClickedPos()
							.relative(side))));
		return state;
	}

	@Override
	public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState,
			LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
		if (!CONNECTED.containsKey(pDirection))
			return pState;
		return pState.setValue(CONNECTED.get(pDirection), opens(pNeighborState));
	}

	private static boolean opens(BlockState neighbour) {
		return neighbour.getBlock() instanceof MaintenancePlatformBlock
				|| neighbour.getBlock() instanceof MaintenanceStairsBlock;
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return shapes.computeIfAbsent(pState, state -> {
			VoxelShape shape = DECK;
			for (Direction side : Direction.Plane.HORIZONTAL)
				if (!state.getValue(CONNECTED.get(side)))
					shape = Shapes.or(shape, RAILS.get(side));
			return shape;
		});
	}

}
