package com.piissedpanda.createairspace.block.Security;

import javax.annotation.Nullable;

import com.piissedpanda.createairspace.CABlockEntities;
import com.piissedpanda.createairspace.CAShapes;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

// Walk-through frame, two blocks high like a door, the lower half does the scanning
public class MetalDetectorBlock extends Block implements IBE<MetalDetectorBlockEntity> {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

	public MetalDetectorBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH)
				.setValue(HALF, DoubleBlockHalf.LOWER)
				.setValue(SecurityScan.ALARM, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder.add(FACING, HALF, SecurityScan.ALARM));
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		BlockPos pos = pContext.getClickedPos();
		Level level = pContext.getLevel();
		if (pos.getY() >= level.getMaxBuildHeight() - 1 || !level.getBlockState(pos.above())
				.canBeReplaced(pContext))
			return null;
		return defaultBlockState().setValue(FACING, pContext.getHorizontalDirection()
				.getOpposite());
	}

	@Override
	public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer,
			ItemStack pStack) {
		pLevel.setBlock(pPos.above(), pState.setValue(HALF, DoubleBlockHalf.UPPER), 3);
	}

	@Override
	protected boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
		if (pState.getValue(HALF) == DoubleBlockHalf.LOWER)
			return true;
		BlockState below = pLevel.getBlockState(pPos.below());
		return below.is(this) && below.getValue(HALF) == DoubleBlockHalf.LOWER;
	}

	@Override
	protected BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState,
			LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
		DoubleBlockHalf half = pState.getValue(HALF);
		if (pDirection.getAxis() != Direction.Axis.Y || (half == DoubleBlockHalf.LOWER) != (pDirection == Direction.UP))
			return pState;
		if (pNeighborState.is(this) && pNeighborState.getValue(HALF) != half)
			return pState;
		return Blocks.AIR.defaultBlockState();
	}

	// In creative the lower half would drop the item when the upper half is broken
	@Override
	public BlockState playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
		if (!pLevel.isClientSide && pPlayer.isCreative() && pState.getValue(HALF) == DoubleBlockHalf.UPPER) {
			BlockPos below = pPos.below();
			if (pLevel.getBlockState(below)
					.is(this))
				pLevel.setBlock(below, Blocks.AIR.defaultBlockState(), 35);
		}
		return super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
	}

	public static void setAlarm(Level level, BlockPos lower, boolean alarm) {
		for (BlockPos pos : new BlockPos[] { lower, lower.above() }) {
			BlockState state = level.getBlockState(pos);
			if (state.getBlock() instanceof MetalDetectorBlock && state.getValue(SecurityScan.ALARM) != alarm)
				level.setBlock(pos, state.setValue(SecurityScan.ALARM, alarm), 3);
		}
	}

	@Override
	protected boolean isSignalSource(BlockState pState) {
		return true;
	}

	@Override
	protected int getSignal(BlockState pState, BlockGetter pLevel, BlockPos pPos, Direction pDirection) {
		return pState.getValue(SecurityScan.ALARM) ? 15 : 0;
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return (pState.getValue(HALF) == DoubleBlockHalf.LOWER ? CAShapes.METAL_DETECTOR_LOWER
				: CAShapes.METAL_DETECTOR_UPPER).get(pState.getValue(FACING));
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

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		if (state.getValue(HALF) != DoubleBlockHalf.LOWER)
			return null;
		return IBE.super.newBlockEntity(pos, state);
	}

	@Override
	public Class<MetalDetectorBlockEntity> getBlockEntityClass() {
		return MetalDetectorBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends MetalDetectorBlockEntity> getBlockEntityType() {
		return CABlockEntities.METAL_DETECTOR.get();
	}

}
