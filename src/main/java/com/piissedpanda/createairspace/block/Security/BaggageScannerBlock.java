package com.piissedpanda.createairspace.block.Security;

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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

// Sits on top of a belt or depot and screens everything passing underneath
public class BaggageScannerBlock extends Block implements IBE<BaggageScannerBlockEntity>, IWrenchable {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	public BaggageScannerBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH)
				.setValue(SecurityScan.ALARM, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder.add(FACING, SecurityScan.ALARM));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return defaultBlockState().setValue(FACING, pContext.getHorizontalDirection()
				.getOpposite());
	}

	public static void setAlarm(Level level, BlockPos pos, boolean alarm) {
		BlockState state = level.getBlockState(pos);
		if (state.getBlock() instanceof BaggageScannerBlock && state.getValue(SecurityScan.ALARM) != alarm)
			level.setBlock(pos, state.setValue(SecurityScan.ALARM, alarm), 3);
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
		return CAShapes.BAGGAGE_SCANNER.get(pState.getValue(FACING));
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
	public Class<BaggageScannerBlockEntity> getBlockEntityClass() {
		return BaggageScannerBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends BaggageScannerBlockEntity> getBlockEntityType() {
		return CABlockEntities.BAGGAGE_SCANNER.get();
	}

}
