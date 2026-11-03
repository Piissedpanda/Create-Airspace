package com.piissedpanda.createairspace.block.Cargo;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.physics.constraint.FixedConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.FixedConstraintHandle;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

// Works like a Create sticker on Sable physics objects (see Sable's
// StickerBlockEntityMixin): a fixed constraint pulls the container face onto the
// lock face. Lock and container stay separate physics objects
public class ContainerLockBlockEntity extends SmartBlockEntity {

	// How far in front of the lock a container is caught, in blocks
	private static final double REACH = 1;
	private static final double ANGLE_TOLERANCE = 30;

	@Nullable
	private FixedConstraintHandle handle;
	@Nullable
	private BlockPos attachedPos;
	@Nullable
	private Vector3d anchorLock;
	@Nullable
	private Vector3d anchorContainer;
	@Nullable
	private Quaterniond orientation;

	public ContainerLockBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
	}

	@Override
	public void tick() {
		super.tick();
		if (!(level instanceof ServerLevel serverLevel))
			return;

		BlockState state = getBlockState();
		if (state.getValue(ContainerLockBlock.POWERED)) {
			release();
			return;
		}

		if (attachedPos != null && !isContainer(attachedPos))
			release();

		if (handle != null && handle.isValid())
			return;

		// Loaded from disk, the physics objects come back without the constraint
		if (attachedPos != null) {
			ServerSubLevel lockLevel = subLevel(worldPosition);
			ServerSubLevel containerLevel = subLevel(attachedPos);
			if (lockLevel != containerLevel)
				apply(serverLevel, lockLevel, containerLevel);
			return;
		}

		Direction facing = state.getValue(DirectionalBlock.FACING);
		Vector3d start = JOMLConversion.atCenterOf(worldPosition)
				.add(facing.getStepX() * 0.5, facing.getStepY() * 0.5, facing.getStepZ() * 0.5);
		tryAttach(serverLevel, start, facing);

		// A few more rays around the centre, the container may sit a bit off
		Direction second = facing.getAxis()
				.isVertical() ? Direction.NORTH : facing.getClockWise();
		Direction third = facing.getAxis()
				.isVertical() ? Direction.EAST : Direction.UP;
		double spread = 0.375;
		for (int a = -1; a <= 1; a += 2)
			for (int b = -1; b <= 1; b += 2) {
				Vector3d offset = new Vector3d(start).add(second.getStepX() * a * spread,
						second.getStepY() * a * spread, second.getStepZ() * a * spread)
						.add(third.getStepX() * b * spread, third.getStepY() * b * spread,
								third.getStepZ() * b * spread);
				tryAttach(serverLevel, offset, facing);
			}
	}

	private boolean isContainer(BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		return state.getBlock() instanceof CargoContainerBlock && state.getValue(CargoContainerBlock.FORMED);
	}

	@Nullable
	private ServerSubLevel subLevel(BlockPos pos) {
		return (ServerSubLevel) Sable.HELPER.getContaining(level, pos);
	}

	private void tryAttach(ServerLevel serverLevel, Vector3dc rayStart, Direction facing) {
		if (handle != null)
			return;

		Vec3 start = JOMLConversion.toMojang(rayStart);
		Vec3 end = start.add(facing.getStepX() * REACH, facing.getStepY() * REACH, facing.getStepZ() * REACH);
		BlockHitResult clip = level.clip(
				new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty()));
		if (clip.getType() == HitResult.Type.MISS || !isContainer(clip.getBlockPos()))
			return;

		BlockPos otherPos = clip.getBlockPos();
		ServerSubLevel lockLevel = subLevel(worldPosition);
		ServerSubLevel containerLevel = subLevel(otherPos);
		// Both still part of the world or of the same aircraft, nothing to hold
		if (lockLevel == containerLevel)
			return;

		Quaterniondc first = lockLevel != null ? lockLevel.logicalPose()
				.orientation() : JOMLConversion.QUAT_IDENTITY;
		Quaterniondc second = containerLevel != null ? containerLevel.logicalPose()
				.orientation() : JOMLConversion.QUAT_IDENTITY;

		// Line up the lock face with the hit container face
		Direction hitSide = clip.getDirection()
				.getOpposite();
		Vector3d lockDirection = first.transform(new Vector3d(facing.getStepX(), facing.getStepY(), facing.getStepZ()));
		Vector3d containerDirection = second
				.transform(new Vector3d(hitSide.getStepX(), hitSide.getStepY(), hitSide.getStepZ()));
		Vector3d axis = new Vector3d(lockDirection).cross(containerDirection)
				.normalize();
		double dot = Math.min(lockDirection.dot(containerDirection), 1);
		if (dot < 1e-6)
			return;
		double angle = Math.acos(dot);
		if (angle > Math.toRadians(ANGLE_TOLERANCE))
			return;
		// Exactly aligned already, any axis will do
		if (!axis.isFinite())
			axis.set(1, 0, 0);

		attachedPos = otherPos;
		anchorLock = JOMLConversion.toJOML(start);
		anchorContainer = JOMLConversion.toJOML(clip.getLocation());
		orientation = new Quaterniond().rotateAxis(-angle, axis)
				.mul(second)
				.premul(first.conjugate(new Quaterniond()));
		apply(serverLevel, lockLevel, containerLevel);
	}

	private void apply(ServerLevel serverLevel, @Nullable ServerSubLevel lockLevel,
			@Nullable ServerSubLevel containerLevel) {
		FixedConstraintConfiguration constraint = new FixedConstraintConfiguration(anchorLock, anchorContainer,
				orientation);
		handle = SubLevelContainer.getContainer(serverLevel)
				.physicsSystem()
				.getPipeline()
				.addConstraint(lockLevel, containerLevel, constraint);
		setLocked(true);
	}

	public void release() {
		if (handle != null) {
			handle.remove();
			handle = null;
		}
		attachedPos = null;
		anchorLock = null;
		anchorContainer = null;
		orientation = null;
		setLocked(false);
	}

	private void setLocked(boolean locked) {
		BlockState state = getBlockState();
		if (state.getValue(ContainerLockBlock.LOCKED) == locked)
			return;
		level.setBlock(worldPosition, state.setValue(ContainerLockBlock.LOCKED, locked), Block.UPDATE_ALL);
		level.updateNeighbourForOutputSignal(worldPosition, state.getBlock());
		(locked ? AllSoundEvents.CONTRAPTION_ASSEMBLE : AllSoundEvents.CONTRAPTION_DISASSEMBLE)
				.playOnServer(level, worldPosition, 0.5f, 1.4f);
	}

	@Override
	public void remove() {
		super.remove();
		if (handle != null) {
			handle.remove();
			handle = null;
		}
	}

	@Override
	protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		if (clientPacket || attachedPos == null || anchorLock == null || anchorContainer == null
				|| orientation == null)
			return;
		CompoundTag lock = new CompoundTag();
		lock.put("Self", NbtUtils.writeBlockPos(worldPosition));
		lock.put("Attached", NbtUtils.writeBlockPos(attachedPos));
		writeVector(lock, "From", anchorLock);
		writeVector(lock, "To", anchorContainer);
		lock.putDouble("QuatX", orientation.x);
		lock.putDouble("QuatY", orientation.y);
		lock.putDouble("QuatZ", orientation.z);
		lock.putDouble("QuatW", orientation.w);
		tag.put("Lock", lock);
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		if (clientPacket)
			return;
		attachedPos = null;
		if (!tag.contains("Lock"))
			return;
		CompoundTag lock = tag.getCompound("Lock");
		// Moved somewhere else (assembled, disassembled), the old anchors no longer fit
		if (!Objects.equals(NbtUtils.readBlockPos(lock, "Self")
				.orElse(null), worldPosition))
			return;
		attachedPos = NbtUtils.readBlockPos(lock, "Attached")
				.orElse(null);
		anchorLock = readVector(lock, "From");
		anchorContainer = readVector(lock, "To");
		orientation = new Quaterniond(lock.getDouble("QuatX"), lock.getDouble("QuatY"), lock.getDouble("QuatZ"),
				lock.getDouble("QuatW"));
	}

	private static void writeVector(CompoundTag tag, String key, Vector3dc vector) {
		tag.putDouble(key + "X", vector.x());
		tag.putDouble(key + "Y", vector.y());
		tag.putDouble(key + "Z", vector.z());
	}

	private static Vector3d readVector(CompoundTag tag, String key) {
		return new Vector3d(tag.getDouble(key + "X"), tag.getDouble(key + "Y"), tag.getDouble(key + "Z"));
	}

}
