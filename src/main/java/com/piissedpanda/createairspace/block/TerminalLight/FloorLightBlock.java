package com.piissedpanda.createairspace.block.TerminalLight;

import com.piissedpanda.createairspace.CABlocks;
import com.piissedpanda.createairspace.CAShapes;
import com.simibubi.create.foundation.block.DyedBlockList;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

// Small light set into the floor, can be walked over
public class FloorLightBlock extends TerminalLightBlock {

	public FloorLightBlock(Properties properties, DyeColor color) {
		super(properties, color);
	}

	@Override
	protected DyedBlockList<FloorLightBlock> variants() {
		return CABlocks.FLOOR_LIGHTS;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return withLevel(defaultBlockState(), pContext);
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return CAShapes.FLOOR_LIGHT;
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos,
			CollisionContext pContext) {
		return Shapes.empty();
	}

}
