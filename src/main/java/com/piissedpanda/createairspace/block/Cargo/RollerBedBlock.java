package com.piissedpanda.createairspace.block.Cargo;

import org.joml.Vector3d;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

// Physics objects slide over it (Sable tag sable:slippery), the balls rattle while
// something rolls across
public class RollerBedBlock extends Block {

	// Slower than this, in blocks per second, and it stays quiet
	private static final double ROLLING_SPEED = 0.3;

	public RollerBedBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		BoundingBox3d above = new BoundingBox3d(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1,
				pos.getY() + 1.25, pos.getZ() + 1);
		Vector3d top = new Vector3d(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
		for (SubLevel subLevel : Sable.HELPER.getAllIntersecting(level, above)) {
			// Sable wants the point in the coordinates of the physics object
			Vector3d local = subLevel.logicalPose()
					.transformPositionInverse(top, new Vector3d());
			Vector3d velocity = Sable.HELPER.getVelocity(level, subLevel, local, new Vector3d());
			double speed = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
			if (speed < ROLLING_SPEED)
				continue;
			float volume = (float) Math.min(0.5, 0.15 + speed * 0.05);
			level.playLocalSound(top.x, top.y, top.z, SoundEvents.CHAIN_STEP, SoundSource.BLOCKS, volume,
					0.5f + random.nextFloat() * 0.3f, false);
			return;
		}
	}

}
