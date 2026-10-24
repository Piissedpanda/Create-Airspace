package com.piissedpanda.createairspace;

import com.simibubi.create.AllShapes.Builder;

import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CAShapes {

	public static final VoxelShape

	BARRIER_POST_INTERACTION = shape(4, 0, 4, 12, 20, 12)
			.build(),

			EDGE_LIGHT = cuboid(5, 0, 5, 11, 11, 11),

			RUNWAY_MARKING = cuboid(0, 0, 0, 16, 1, 16),

			BAGGAGE_CART = cuboid(0, 0, 0, 16, 16, 16),

			AIRPORT_BEACON = shape(1, 0, 1, 15, 3, 15)
					.add(5, 3, 5, 11, 8, 11)
					.add(4, 8, 4, 12, 15, 12)
					.build(),

			WINDSOCK = shape(5, 0, 5, 11, 1, 11)
					.add(7, 1, 7, 9, 24, 9)
					.build(),

			TERMINAL_SEAT_COLLISION = cuboid(0, 0, 0, 16, 6, 16),
			TERMINAL_SEAT_COLLISION_PLAYERS = cuboid(0, 0, 0, 16, 3, 16);

	// Each stacked cone lifts the stack by 1.5 px
	public static final VoxelShape[] TRAFFIC_CONES = { trafficCone(0), trafficCone(1.5), trafficCone(3),
			trafficCone(4.5) };

	// Facing north means sitting towards north, the backrest is in the south
	public static final VoxelShaper

	TERMINAL_SEAT = shape(0, 0, 0, 16, 8, 15)
			.add(1, 8, 13, 15, 18, 15)
			.forHorizontal(Direction.NORTH),

			TERMINAL_SEAT_BACK = shape(1, 8, 13, 15, 18, 15)
					.forHorizontal(Direction.NORTH),

			AIRFIELD_SIGN = shape(0, 0, 6, 16, 15, 10)
					.forHorizontal(Direction.NORTH);

	private static VoxelShape trafficCone(double lift) {
		return shape(2, 0, 2, 14, 1 + lift, 14).add(5, 1 + lift, 5, 11, 8 + lift, 11)
				.add(6, 8 + lift, 6, 10, 14 + lift, 10)
				.build();
	}

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
