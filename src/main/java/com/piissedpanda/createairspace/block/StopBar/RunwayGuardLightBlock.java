package com.piissedpanda.createairspace.block.StopBar;

import javax.annotation.Nullable;

import com.piissedpanda.createairspace.CABlockEntities;
import com.piissedpanda.createairspace.CAShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

// Two yellow lamps flashing in turns while powered, the renderer does the flashing
public class RunwayGuardLightBlock extends Block implements IBE<RunwayGuardLightBlockEntity>, IWrenchable {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public RunwayGuardLightBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH)
				.setValue(POWERED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder.add(FACING, POWERED));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		boolean powered = pContext.getLevel()
				.hasNeighborSignal(pContext.getClickedPos());
		return defaultBlockState().setValue(FACING, pContext.getHorizontalDirection()
				.getOpposite())
				.setValue(POWERED, powered);
	}

	@Override
	protected void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock,
			BlockPos pNeighborPos, boolean pMovedByPiston) {
		if (pLevel.isClientSide)
			return;
		boolean powered = pLevel.hasNeighborSignal(pPos);
		if (powered == pState.getValue(POWERED))
			return;
		pLevel.setBlock(pPos, pState.setValue(POWERED, powered), 2);
	}

	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos,
			@Nullable Direction direction) {
		return direction != null && direction.getAxis()
				.isHorizontal();
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return CAShapes.RUNWAY_GUARD_LIGHT.get(pState.getValue(FACING));
	}

	@Override
	protected BlockState rotate(BlockState pState, Rotation pRotation) {
		return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
	}

	@Override
	protected BlockState mirror(BlockState pState, Mirror pMirror) {
		return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
	}

	@Override
	protected boolean isPathfindable(BlockState pState, PathComputationType pPathComputationType) {
		return false;
	}

	@Override
	public Class<RunwayGuardLightBlockEntity> getBlockEntityClass() {
		return RunwayGuardLightBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends RunwayGuardLightBlockEntity> getBlockEntityType() {
		return CABlockEntities.RUNWAY_GUARD_LIGHT.get();
	}

}
