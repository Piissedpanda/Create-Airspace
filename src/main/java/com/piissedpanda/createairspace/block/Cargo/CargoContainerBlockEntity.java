package com.piissedpanda.createairspace.block.Cargo;

import java.util.List;

import javax.annotation.Nullable;

import org.joml.Vector3d;

import com.piissedpanda.createairspace.block.Cargo.CargoContainerBlock.Point;
import com.simibubi.create.infrastructure.config.AllConfigs;

import dev.ryanhcode.sable.sublevel.SubLevel;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

// Every block holds as many stacks as an item vault block, a finished container
// offers all of them as one inventory, but only to container ports: funnels and
// chutes cannot reach it. Its lock point is the magnet a lock pulls in
public class CargoContainerBlockEntity extends CargoMagnet {

	public final ItemStackHandler inventory;

	// Only used while forming, the dock side faces the player
	public Direction placedFacing = Direction.NORTH;

	@Nullable
	private IItemHandler containerInventory;

	// The lock holding this lock point
	@Nullable
	private BlockPos dockedBy;
	// A lock point under and on the side of the block docks with either face
	private Direction aimedFace = Direction.DOWN;

	public CargoContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		inventory = new ItemStackHandler(AllConfigs.server().logistics.vaultCapacity.get()) {
			@Override
			protected void onContentsChanged(int slot) {
				setChanged();
				if (level == null || level.isClientSide)
					return;
				// Comparators may sit at any block of the container
				for (BlockPos member : CargoContainers.members(level, worldPosition))
					level.updateNeighbourForOutputSignal(member, getBlockState().getBlock());
			}
		};
	}

	// Null while the block is not part of a finished container
	@Nullable
	public IItemHandler getContainerInventory() {
		if (level == null || !getBlockState().getValue(CargoContainerBlock.FORMED))
			return null;
		if (containerInventory == null) {
			List<BlockPos> members = CargoContainers.members(level, worldPosition);
			IItemHandlerModifiable[] parts = new IItemHandlerModifiable[members.size()];
			for (int i = 0; i < parts.length; i++)
				parts[i] = level.getBlockEntity(members.get(i)) instanceof CargoContainerBlockEntity be ? be.inventory
						: new ItemStackHandler(0);
			containerInventory = new CombinedInvWrapper(parts);
		}
		return containerInventory;
	}

	public boolean isLockPoint() {
		BlockState state = getBlockState();
		return state.getValue(CargoContainerBlock.FORMED) && state.getValue(CargoContainerBlock.POINT)
				.lock();
	}

	@Override
	public boolean magnetActive() {
		return isLockPoint() && dockedBy == null;
	}

	@Override
	public Direction getMagnetFace() {
		Point point = getBlockState().getValue(CargoContainerBlock.POINT);
		if (point == Point.LOCK)
			return aimedFace;
		return point == Point.LOCK_BOTTOM ? Direction.DOWN
				: getBlockState().getValue(CargoContainerBlock.FACING);
	}

	// Picks the face that looks most towards the lock
	public void aimAt(CargoMagnet lock) {
		if (getBlockState().getValue(CargoContainerBlock.POINT) != Point.LOCK || dockedBy != null)
			return;
		Vector3d lockFace = global(lock.getLatestSubLevel(), lock.getMagnetFace());
		Direction best = Direction.DOWN;
		double bestDot = Double.MAX_VALUE;
		for (Direction face : new Direction[] { Direction.DOWN, getBlockState().getValue(CargoContainerBlock.FACING) }) {
			double dot = global(getLatestSubLevel(), face).dot(lockFace);
			if (dot < bestDot) {
				bestDot = dot;
				best = face;
			}
		}
		aimedFace = best;
	}

	private static Vector3d global(@Nullable SubLevel subLevel, Direction face) {
		Vector3d normal = new Vector3d(face.getStepX(), face.getStepY(), face.getStepZ());
		return subLevel == null ? normal
				: subLevel.logicalPose()
						.orientation()
						.transform(normal);
	}

	// Forget a lock that moved away or let go while this was unloaded
	@Override
	public void tick() {
		super.tick();
		if (level.isClientSide || dockedBy == null || level.getGameTime() % 20 != 0)
			return;
		if (!(level.getBlockEntity(dockedBy) instanceof ContainerLockBlockEntity lock) || !lock.holds(worldPosition))
			setDockedBy(null);
	}

	public boolean isDocked() {
		return dockedBy != null;
	}

	public void setDockedBy(@Nullable BlockPos lock) {
		dockedBy = lock;
		notifyUpdate();
	}

	// Whether a lock holds the container this block belongs to
	public boolean isContainerDocked() {
		for (BlockPos member : CargoContainers.members(level, worldPosition))
			if (level.getBlockEntity(member) instanceof CargoContainerBlockEntity be && be.isDocked())
				return true;
		return false;
	}

	public void invalidateContainer() {
		containerInventory = null;
	}

	@Override
	public void setBlockState(BlockState state) {
		super.setBlockState(state);
		invalidateContainer();
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		dockedBy = NbtUtils.readBlockPos(tag, "DockedBy")
				.orElse(null);
		aimedFace = tag.contains("Aimed") ? Direction.from3DDataValue(tag.getInt("Aimed")) : Direction.DOWN;
		if (!clientPacket)
			inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
	}

	@Override
	protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		if (dockedBy != null)
			tag.put("DockedBy", NbtUtils.writeBlockPos(dockedBy));
		tag.putInt("Aimed", aimedFace.get3DDataValue());
		if (!clientPacket)
			tag.put("Inventory", inventory.serializeNBT(registries));
	}

}
