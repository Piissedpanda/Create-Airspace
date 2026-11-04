package com.piissedpanda.createairspace.block.Cargo;

import java.util.Objects;

import javax.annotation.Nullable;

import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;

import dev.ryanhcode.sable.api.physics.constraint.FixedConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.FixedConstraintHandle;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.index.SimSoundEvents;
import dev.simulated_team.simulated.util.SimMovementContext;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

// Pulls the lock point of a container in like a magnet, lines it up on the 90 degree
// grid, then closes the grippers: a fixed constraint eases the container onto the
// lock face and holds it there until redstone lets go. Lock and container stay
// separate physics objects
public class ContainerLockBlockEntity extends CargoMagnet {

	// Magnets start pulling from this far, in blocks
	public static final double REACH = 2.5;
	private static final int GRIP_TICKS = 12;

	public enum State {
		FREE, LOCKING, LOCKED
	}

	private State state = State.FREE;
	@Nullable
	private BlockPos docked;
	private float progress;

	// Fixed once the grip starts: who carries the constraint, and how the two end up
	private boolean lockCarries;
	private final Quaterniond targetOrientation = new Quaterniond();
	private final Quaterniond startOrientation = new Quaterniond();
	private final Vector3d startOffset = new Vector3d();
	@Nullable
	private FixedConstraintHandle handle;

	public final LerpedFloat grip = LerpedFloat.linear()
			.startWithValue(0)
			.chase(0, 0.12, LerpedFloat.Chaser.LINEAR);

	public ContainerLockBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public Direction getMagnetFace() {
		return getBlockState().getValue(DirectionalBlock.FACING);
	}

	@Override
	public boolean magnetActive() {
		return state == State.FREE && !getBlockState().getValue(ContainerLockBlock.POWERED);
	}

	// Ponder has no physics, it only shows the claws
	public void showLocked(boolean locked) {
		state = locked ? State.LOCKED : State.FREE;
	}

	public boolean holds(BlockPos point) {
		return state != State.FREE && point.equals(docked);
	}

	public boolean isLocked() {
		return state == State.LOCKED;
	}

	@Override
	public void tick() {
		super.tick();
		if (level.isClientSide) {
			grip.chase(state == State.FREE ? 0 : 1, 0.12, LerpedFloat.Chaser.LINEAR);
			grip.tickChaser();
			return;
		}

		if (getBlockState().getValue(ContainerLockBlock.POWERED)) {
			if (state != State.FREE)
				release(true);
			return;
		}

		if (state != State.FREE && container() == null) {
			release(true);
			return;
		}

		switch (state) {
		case FREE -> searchForContainers();
		case LOCKING -> {
			progress = Math.min(1, progress + 1f / GRIP_TICKS);
			hold(progress * progress);
			if (progress >= 1)
				setState(State.LOCKED);
		}
		case LOCKED -> {
			// The physics objects come back from disk without the constraint
			if (handle == null || !handle.isValid())
				hold(1);
		}
		}
	}

	private void searchForContainers() {
		SubLevel own = getLatestSubLevel();
		SimMovementContext context = SimMovementContext.getMovementContext(level, getFaceCentre());
		for (SimMovementContext other : CargoDock.MAGNETS.findNearby(context)) {
			if (other.subLevel() == own
					|| !(level.getBlockEntity(other.localBlockPos()) instanceof CargoContainerBlockEntity point))
				continue;
			point.aimAt(this);
			if (point.magnetActive())
				CargoDock.MAGNETS.tryAddPair(level, worldPosition, point.getBlockPos(), CargoDockPair::new);
		}
	}

	// Called by the magnet pair while it pulls
	void tryDock(CargoDockPair pair, CargoContainerBlockEntity point) {
		if (state != State.FREE || !magnetActive() || !point.magnetActive() || !pair.inReach(this, point))
			return;

		SubLevel lockLevel = getLatestSubLevel();
		SubLevel containerLevel = point.getLatestSubLevel();
		if (lockLevel == containerLevel)
			return;

		Quaterniond lockOrientation = orientation(lockLevel);
		Quaterniond containerOrientation = orientation(containerLevel);
		Vec3 lockFace = global(lockLevel, getFaceCentre());
		Vec3 containerFace = global(containerLevel, point.getFaceCentre());

		// pair.target was just set by inReach, see DockingConnectorPair.dock
		Quaterniond aligned = new Quaterniond(pair.target).mul(new Quaterniond(getOrientation()))
				.mul(new Quaterniond(point.getOrientation()).conjugate());
		Quaterniond current = new Quaterniond(lockOrientation).conjugate()
				.mul(containerOrientation);

		// The constraint needs a physics object as its first body
		lockCarries = lockLevel != null;
		if (lockCarries) {
			targetOrientation.set(aligned);
			startOrientation.set(current);
			startOffset.set(lockFace.x - containerFace.x, lockFace.y - containerFace.y, lockFace.z - containerFace.z);
			containerOrientation.transformInverse(startOffset);
		} else {
			targetOrientation.set(aligned).conjugate();
			startOrientation.set(current).conjugate();
			startOffset.set(containerFace.x - lockFace.x, containerFace.y - lockFace.y, containerFace.z - lockFace.z);
			lockOrientation.transformInverse(startOffset);
		}

		docked = point.getBlockPos();
		point.setDockedBy(worldPosition);
		progress = 0;
		setState(State.LOCKING);
		hold(0);
		level.playSound(null, worldPosition, SimSoundEvents.DOCKING_CONNECTOR_DOCKS.event(), SoundSource.BLOCKS, 1,
				0.8f + level.random.nextFloat() * 0.2f);
	}

	// Eases the container from where it was caught (0) onto the lock face (1)
	private void hold(float amount) {
		CargoContainerBlockEntity point = container();
		if (point == null || !(level instanceof ServerLevel serverLevel))
			return;
		ServerSubLevel lockLevel = (ServerSubLevel) getLatestSubLevel();
		ServerSubLevel containerLevel = (ServerSubLevel) point.getLatestSubLevel();

		Vector3d anchorFirst = JOMLConversion.toJOML(lockCarries ? getFaceCentre() : point.getFaceCentre());
		Vector3d anchorSecond = JOMLConversion.toJOML(lockCarries ? point.getFaceCentre() : getFaceCentre());
		anchorSecond.fma(1 - amount, startOffset);
		Quaterniond orientation = new Quaterniond(startOrientation).slerp(targetOrientation,
				Mth.clamp(amount * 2, 0, 1));

		if (handle != null)
			handle.remove();
		handle = SubLevelContainer.getContainer(serverLevel)
				.physicsSystem()
				.getPipeline()
				.addConstraint(lockCarries ? lockLevel : containerLevel, lockCarries ? containerLevel : lockLevel,
						new FixedConstraintConfiguration(anchorFirst, anchorSecond, orientation));
	}

	public void release(boolean sound) {
		if (handle != null) {
			handle.remove();
			handle = null;
		}
		CargoContainerBlockEntity point = container();
		if (point != null)
			point.setDockedBy(null);
		docked = null;
		if (state != State.FREE && sound)
			level.playSound(null, worldPosition, SoundEvents.NETHERITE_BLOCK_FALL, SoundSource.BLOCKS, 0.4f, 0.75f);
		setState(State.FREE);
	}

	@Nullable
	private CargoContainerBlockEntity container() {
		if (docked == null || !(level.getBlockEntity(docked) instanceof CargoContainerBlockEntity point)
				|| !point.isLockPoint())
			return null;
		return point;
	}

	private void setState(State state) {
		if (this.state == state)
			return;
		this.state = state;
		BlockState blockState = getBlockState();
		boolean locked = state == State.LOCKED;
		if (blockState.getValue(ContainerLockBlock.LOCKED) != locked) {
			level.setBlock(worldPosition, blockState.setValue(ContainerLockBlock.LOCKED, locked), Block.UPDATE_ALL);
			level.updateNeighbourForOutputSignal(worldPosition, blockState.getBlock());
		}
		notifyUpdate();
	}

	private static Quaterniond orientation(@Nullable SubLevel subLevel) {
		return subLevel == null ? new Quaterniond()
				: new Quaterniond(subLevel.logicalPose()
						.orientation());
	}

	private static Vec3 global(@Nullable SubLevel subLevel, Vec3 local) {
		return subLevel == null ? local
				: subLevel.logicalPose()
						.transformPosition(local);
	}

	// Broken or unloaded: let go without touching the block any more
	@Override
	public void remove() {
		super.remove();
		if (level == null || level.isClientSide)
			return;
		if (handle != null) {
			handle.remove();
			handle = null;
		}
		CargoContainerBlockEntity point = container();
		if (point != null)
			point.setDockedBy(null);
	}

	@Override
	public AABB createRenderBoundingBox() {
		return super.createRenderBoundingBox().inflate(1);
	}

	@Override
	protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		tag.putString("State", state.name());
		if (clientPacket || docked == null)
			return;
		tag.put("Self", NbtUtils.writeBlockPos(worldPosition));
		tag.put("Docked", NbtUtils.writeBlockPos(docked));
		tag.putBoolean("LockCarries", lockCarries);
		tag.putFloat("Progress", progress);
		writeQuaternion(tag, "Target", targetOrientation);
		writeQuaternion(tag, "Start", startOrientation);
		tag.putDouble("OffsetX", startOffset.x);
		tag.putDouble("OffsetY", startOffset.y);
		tag.putDouble("OffsetZ", startOffset.z);
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		state = tag.contains("State") ? State.valueOf(tag.getString("State")) : State.FREE;
		if (clientPacket)
			return;
		docked = null;
		// Moved somewhere else (assembled, disassembled): the old dock no longer fits
		if (!Objects.equals(NbtUtils.readBlockPos(tag, "Self")
				.orElse(null), worldPosition)) {
			state = State.FREE;
			return;
		}
		docked = NbtUtils.readBlockPos(tag, "Docked")
				.orElse(null);
		lockCarries = tag.getBoolean("LockCarries");
		progress = tag.getFloat("Progress");
		readQuaternion(tag, "Target", targetOrientation);
		readQuaternion(tag, "Start", startOrientation);
		startOffset.set(tag.getDouble("OffsetX"), tag.getDouble("OffsetY"), tag.getDouble("OffsetZ"));
	}

	private static void writeQuaternion(CompoundTag tag, String key, Quaterniondc q) {
		tag.putDouble(key + "X", q.x());
		tag.putDouble(key + "Y", q.y());
		tag.putDouble(key + "Z", q.z());
		tag.putDouble(key + "W", q.w());
	}

	private static void readQuaternion(CompoundTag tag, String key, Quaterniond q) {
		q.set(tag.getDouble(key + "X"), tag.getDouble(key + "Y"), tag.getDouble(key + "Z"), tag.getDouble(key + "W"));
	}

}
