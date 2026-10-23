package com.piissedpanda.createaviation.block.Windsock;

import com.piissedpanda.createaviation.CABlockEntities;
import com.piissedpanda.createaviation.CAShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.IHaveBigOutline;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WindsockBlock extends Block implements IWrenchable, IBE<WindsockBlockEntity>, IHaveBigOutline {

	public WindsockBlock(Properties properties) {
		super(properties);
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return CAShapes.WINDSOCK;
	}

	@Override
	protected boolean isPathfindable(BlockState pState, PathComputationType pPathComputationType) {
		return false;
	}

	@Override
	public Class<WindsockBlockEntity> getBlockEntityClass() {
		return WindsockBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends WindsockBlockEntity> getBlockEntityType() {
		return CABlockEntities.WINDSOCK.get();
	}

}
