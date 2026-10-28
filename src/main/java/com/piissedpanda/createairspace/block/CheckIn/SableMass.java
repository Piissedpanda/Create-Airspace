package com.piissedpanda.createairspace.block.CheckIn;

import dev.ryanhcode.sable.mixinterface.block_properties.BlockStateExtension;
import dev.ryanhcode.sable.physics.config.block_properties.PhysicsBlockPropertyTypes;
import net.minecraft.world.level.block.state.BlockState;

// Only loaded when Sable is installed
public class SableMass {

	public static double of(BlockState state) {
		Double mass = ((BlockStateExtension) state).sable$getProperty(PhysicsBlockPropertyTypes.MASS.get());
		return mass == null ? 1 : mass;
	}

}
