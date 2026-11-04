package com.piissedpanda.createairspace.block.Security;

import javax.annotation.Nullable;

import com.piissedpanda.createairspace.CABlockEntities;
import com.piissedpanda.createairspace.CAShapes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity.CasingType;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.BeltPart;
import com.simibubi.create.content.kinetics.belt.BeltSlope;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

// Placed like a Create tunnel on a straight belt, its model encases the belt in industrial iron
public class BaggageScannerBlock extends Block implements IBE<BaggageScannerBlockEntity>, IWrenchable {

	public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

	public BaggageScannerBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(AXIS, Direction.Axis.Z)
				.setValue(SecurityScan.ALARM, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder.add(AXIS, SecurityScan.ALARM));
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		BlockState belt = pContext.getLevel()
				.getBlockState(pContext.getClickedPos()
						.below());
		if (!isValidBelt(belt))
			return null;
		return defaultBlockState().setValue(AXIS, belt.getValue(BeltBlock.HORIZONTAL_FACING)
				.getAxis());
	}

	// Straight middle segments only, the pulleys at the ends would stick out of the
	// casing
	public static boolean isValidBelt(BlockState state) {
		return AllBlocks.BELT.has(state) && state.getValue(BeltBlock.SLOPE) == BeltSlope.HORIZONTAL
				&& state.getValue(BeltBlock.PART) == BeltPart.MIDDLE;
	}

	@Override
	protected boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
		return isValidBelt(pLevel.getBlockState(pPos.below()));
	}

	@Override
	protected BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState,
			LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
		if (pDirection == Direction.DOWN && !canSurvive(pState, pLevel, pPos))
			return Blocks.AIR.defaultBlockState();
		return pState;
	}

	@Override
	protected void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState,
			boolean pMovedByPiston) {
		if (!pOldState.is(this))
			removeCreateCasing(pLevel, pPos);
	}

	// The scanner brings its own casing, Create's would overlap it. The casing
	// block goes back to the player
	public static void removeCreateCasing(Level level, BlockPos pos) {
		if (level.isClientSide)
			return;
		BeltBlockEntity belt = BeltHelper.getSegmentBE(level, pos.below());
		if (belt == null || belt.casing == CasingType.NONE)
			return;
		ItemStack casing = belt.casing == CasingType.ANDESITE ? AllBlocks.ANDESITE_CASING.asStack()
				: AllBlocks.BRASS_CASING.asStack();
		belt.setCasingType(CasingType.NONE);
		Block.popResource(level, pos, casing);
	}

	public static void setAlarm(Level level, BlockPos pos, boolean alarm) {
		BlockState state = level.getBlockState(pos);
		if (state.getBlock() instanceof BaggageScannerBlock && state.getValue(SecurityScan.ALARM) != alarm)
			level.setBlock(pos, state.setValue(SecurityScan.ALARM, alarm), 3);
	}

	// Turning it would leave it across the belt
	@Override
	public InteractionResult onWrenched(BlockState state, UseOnContext context) {
		return InteractionResult.PASS;
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
		return CAShapes.BAGGAGE_SCANNER.get(pState.getValue(AXIS) == Direction.Axis.Z ? Direction.NORTH
				: Direction.EAST);
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

	@Override
	public Class<BaggageScannerBlockEntity> getBlockEntityClass() {
		return BaggageScannerBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends BaggageScannerBlockEntity> getBlockEntityType() {
		return CABlockEntities.BAGGAGE_SCANNER.get();
	}

}
