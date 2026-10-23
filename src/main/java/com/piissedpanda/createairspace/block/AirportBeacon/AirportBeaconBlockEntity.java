package com.piissedpanda.createairspace.block.AirportBeacon;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class AirportBeaconBlockEntity extends KineticBlockEntity {

	public AirportBeaconBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// The light beams reach far beyond the block
	@Override
	protected AABB createRenderBoundingBox() {
		return new AABB(worldPosition).inflate(AirportBeaconRenderer.MAX_BEAM_LENGTH);
	}

}
