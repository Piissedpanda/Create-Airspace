package com.piissedpanda.createaviation;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.simibubi.create.AllShapes.Builder;;

public class CAShapes {

	public static final VoxelShape

	BARRIER_POST_INTERACTION = shape(4, 0, 4, 12, 20, 12)
			.build(),

			TRAFFIC_CONE = shape(2, 0, 2, 14, 1, 14)
					.add(5, 1, 5, 11, 8, 11)
					.add(6, 8, 6, 10, 14, 10)
					.build();

	private static Builder shape(VoxelShape shape) {
		return new Builder(shape);
	}

	private static Builder shape(double x1, double y1, double z1, double x2, double y2, double z2) {
		return shape(cuboid(x1, y1, z1, x2, y2, z2));
	}

	private static VoxelShape cuboid(double x1, double y1, double z1, double x2, double y2, double z2) {
		return Block.box(x1, y1, z1, x2, y2, z2);
	}
}
