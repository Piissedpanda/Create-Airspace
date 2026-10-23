package com.piissedpanda.createaviation.block.AirportBeacon;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class AirportBeaconBlockEntity extends KineticBlockEntity {

	public AirportBeaconBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void onSpeedChanged(float previousSpeed) {
		super.onSpeedChanged(previousSpeed);
		if (level == null || level.isClientSide)
			return;

		boolean lit = getSpeed() != 0;
		BlockState state = getBlockState();
		if (state.getValue(AirportBeaconBlock.LIT) == lit)
			return;
		level.setBlock(worldPosition, state.setValue(AirportBeaconBlock.LIT, lit), 3);
	}

	// The light beams reach far beyond the block
	@Override
	protected AABB createRenderBoundingBox() {
		return new AABB(worldPosition).inflate(AirportBeaconRenderer.BEAM_LENGTH);
	}

}
