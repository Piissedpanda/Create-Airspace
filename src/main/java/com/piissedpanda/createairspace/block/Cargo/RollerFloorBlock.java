package com.piissedpanda.createairspace.block.Cargo;

import javax.annotation.Nullable;

import org.joml.Vector3d;

import com.piissedpanda.createairspace.CABlockEntities;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;

import dev.ryanhcode.sable.api.block.BlockWithSubLevelCollisionCallback;
import dev.ryanhcode.sable.api.physics.callback.BlockSubLevelCollisionCallback;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

// Shafts on all four sides, opposite ones belong together. Physics objects on top move the way the
// rollers turn, like on a belt (Sable passes the surface speed to the collision)
public class RollerFloorBlock extends KineticBlock
		implements IBE<RollerFloorBlockEntity>, BlockWithSubLevelCollisionCallback {

	public RollerFloorBlock(Properties properties) {
		super(properties);
	}

	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return face.getAxis()
				.isHorizontal();
	}

	// Only used by Create for display, the floor turns around both axes
	@Override
	public Direction.Axis getRotationAxis(BlockState state) {
		return Direction.Axis.X;
	}

	@Override
	public BlockSubLevelCollisionCallback sable$getCallback() {
		return Callback.INSTANCE;
	}

	@Override
	public Class<RollerFloorBlockEntity> getBlockEntityClass() {
		return RollerFloorBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends RollerFloorBlockEntity> getBlockEntityType() {
		return CABlockEntities.ROLLER_FLOOR.get();
	}

	// Runs on the physics thread, like Sable's own callback for Create belts
	private static class Callback implements BlockSubLevelCollisionCallback {

		private static final Callback INSTANCE = new Callback();

		@Override
		public CollisionResult sable$onCollision(BlockPos hitBlockPos, @Nullable BlockPos otherHitBlockPos,
				Vector3d impactPosition, double impactVelocity) {
			SubLevelPhysicsSystem system = SubLevelPhysicsSystem.getCurrentlySteppingSystem();
			if (system == null)
				return CollisionResult.NONE;
			// Only the top moves things
			if (impactPosition.y - hitBlockPos.getY() < 0.5)
				return CollisionResult.NONE;
			if (!(system.getLevel()
					.getBlockEntity(hitBlockPos) instanceof RollerFloorBlockEntity be))
				return CollisionResult.NONE;
			Vec3 velocity = be.getSurfaceVelocity();
			return new CollisionResult(new Vector3d(velocity.x, 0, velocity.z), false);
		}

	}

}
