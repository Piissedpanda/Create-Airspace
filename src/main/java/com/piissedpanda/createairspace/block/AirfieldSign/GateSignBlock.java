package com.piissedpanda.createairspace.block.AirfieldSign;

import com.piissedpanda.createairspace.CAShapes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

// Airfield sign hanging from the ceiling, for gates and terminals
public class GateSignBlock extends AirfieldSignBlock {

	public GateSignBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected Style nextStyle(Style style) {
		return style.next();
	}

	@Override
	public float textCenter() {
		return 5.5f;
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return CAShapes.GATE_SIGN.get(pState.getValue(FACING));
	}

}
