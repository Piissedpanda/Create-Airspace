package com.piissedpanda.createairspace.block.Papi;

import javax.annotation.Nullable;

import com.piissedpanda.createairspace.CABlockEntities;
import com.piissedpanda.createairspace.CAShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
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

// One light of a PAPI, the lens faces the approaching aircraft
public class PapiLightBlock extends Block implements IBE<PapiLightBlockEntity>, IWrenchable {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	// Tenths of a degree, the light next to the runway first
	public static final int[] DEFAULT_ANGLES = { 35, 32, 28, 25 };
	private static final int MAX_ROW = 8;

	public PapiLightBlock(Properties properties) {
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

	// Each light added to a row gets the next lower angle
	@Override
	public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer,
			ItemStack pStack) {
		super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
		if (pLevel.isClientSide)
			return;
		Direction facing = pState.getValue(FACING);
		int index = Math.max(countRow(pLevel, pPos, facing, facing.getClockWise()),
				countRow(pLevel, pPos, facing, facing.getCounterClockWise()));
		int angle = DEFAULT_ANGLES[Math.min(index, DEFAULT_ANGLES.length - 1)];
		withBlockEntityDo(pLevel, pPos, be -> be.setAngle(angle));
	}

	private int countRow(Level level, BlockPos pos, Direction facing, Direction side) {
		int count = 0;
		for (int i = 1; i <= MAX_ROW; i++) {
			BlockState state = level.getBlockState(pos.relative(side, i));
			if (!state.is(this) || state.getValue(FACING) != facing)
				break;
			count++;
		}
		return count;
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
		return CAShapes.PAPI_LIGHT.get(pState.getValue(FACING));
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
	public Class<PapiLightBlockEntity> getBlockEntityClass() {
		return PapiLightBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends PapiLightBlockEntity> getBlockEntityType() {
		return CABlockEntities.PAPI_LIGHT.get();
	}

}
