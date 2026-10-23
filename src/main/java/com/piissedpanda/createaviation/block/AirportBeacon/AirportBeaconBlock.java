package com.piissedpanda.createaviation.block.AirportBeacon;

import com.piissedpanda.createaviation.CABlockEntities;
import com.piissedpanda.createaviation.CAShapes;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AirportBeaconBlock extends KineticBlock implements IBE<AirportBeaconBlockEntity> {

	public static final BooleanProperty LIT = BlockStateProperties.LIT;

	public AirportBeaconBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(LIT, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder.add(LIT));
	}

	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return face == Direction.DOWN;
	}

	@Override
	public Axis getRotationAxis(BlockState state) {
		return Axis.Y;
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return CAShapes.AIRPORT_BEACON;
	}

	@Override
	protected boolean isPathfindable(BlockState pState, PathComputationType pPathComputationType) {
		return false;
	}

	@Override
	public Class<AirportBeaconBlockEntity> getBlockEntityClass() {
		return AirportBeaconBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends AirportBeaconBlockEntity> getBlockEntityType() {
		return CABlockEntities.AIRPORT_BEACON.get();
	}

}
