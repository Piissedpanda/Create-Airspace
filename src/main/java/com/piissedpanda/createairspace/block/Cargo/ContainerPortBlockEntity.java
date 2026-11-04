package com.piissedpanda.createairspace.block.Cargo;

import java.util.List;

import javax.annotation.Nullable;

import org.joml.Vector3d;

import com.piissedpanda.createairspace.CABlockEntities;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;

// Works like a Portable Storage Interface: once a docked container shows one of its
// port points right in front, the plate slides out and every side reaches the
// container inventory, in and out
public class ContainerPortBlockEntity extends SmartBlockEntity {

	private static final double REACH = 0.5;
	private static final int SEARCH_INTERVAL = 5;
	// Port and port point must face each other this well (cosine)
	private static final double ALIGNMENT = 0.95;

	@Nullable
	private BlockPos target;
	private boolean connected;
	private int searchCooldown;

	public final LerpedFloat plate = LerpedFloat.linear()
			.startWithValue(0)
			.chase(0, 0.05, LerpedFloat.Chaser.LINEAR);

	private final IItemHandler passage = new Passage();

	public ContainerPortBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CABlockEntities.CONTAINER_PORT.get(),
				(be, side) -> side == be.facing() ? null : be.passage);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
	}

	private Direction facing() {
		return getBlockState().getValue(HorizontalDirectionalBlock.FACING);
	}

	// Ponder has no physics, it only shows the plate
	public void showConnected(boolean connected) {
		this.connected = connected;
	}

	public boolean isConnected() {
		return connected;
	}

	@Override
	public void tick() {
		super.tick();
		if (level.isClientSide) {
			plate.chase(connected ? 1 : 0, 0.05, LerpedFloat.Chaser.LINEAR);
			plate.tickChaser();
			return;
		}
		if (searchCooldown-- > 0)
			return;
		searchCooldown = SEARCH_INTERVAL;

		target = findPortPoint();
		if (connected != (target != null)) {
			connected = target != null;
			AllSoundEvents.CONTRAPTION_ASSEMBLE.playOnServer(level, worldPosition, 0.4f, connected ? 1.6f : 1.2f);
			notifyUpdate();
		}
	}

	@Nullable
	private BlockPos findPortPoint() {
		Direction facing = facing();
		Vec3 start = Vec3.atCenterOf(worldPosition)
				.relative(facing, 0.5);
		BlockHitResult clip = level.clip(new ClipContext(start, start.relative(facing, REACH), ClipContext.Block.COLLIDER,
				ClipContext.Fluid.NONE, CollisionContext.empty()));
		if (clip.getType() == HitResult.Type.MISS)
			return null;
		BlockPos pos = clip.getBlockPos();
		BlockState state = level.getBlockState(pos);
		if (!(state.getBlock() instanceof CargoContainerBlock) || !state.getValue(CargoContainerBlock.FORMED)
				|| state.getValue(CargoContainerBlock.POINT) != CargoContainerBlock.Point.PORT)
			return null;
		if (!(level.getBlockEntity(pos) instanceof CargoContainerBlockEntity point) || !point.isContainerDocked())
			return null;

		// The port point has to look straight back at the port
		Vector3d own = global(Sable.HELPER.getContaining(level, worldPosition), facing);
		Vector3d other = global(Sable.HELPER.getContaining(level, pos), state.getValue(CargoContainerBlock.FACING));
		return own.dot(other) < -ALIGNMENT ? pos : null;
	}

	private static Vector3d global(@Nullable SubLevel subLevel, Direction face) {
		Vector3d normal = new Vector3d(face.getStepX(), face.getStepY(), face.getStepZ());
		return subLevel == null ? normal
				: subLevel.logicalPose()
						.orientation()
						.transform(normal);
	}

	@Nullable
	private IItemHandler container() {
		if (!connected || target == null || !(level.getBlockEntity(target) instanceof CargoContainerBlockEntity be))
			return null;
		return be.getContainerInventory();
	}

	@Override
	protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		if (clientPacket)
			tag.putBoolean("Connected", connected);
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		if (clientPacket)
			connected = tag.getBoolean("Connected");
	}

	private class Passage implements IItemHandler {

		@Override
		public int getSlots() {
			IItemHandler container = container();
			return container == null ? 0 : container.getSlots();
		}

		@Override
		public ItemStack getStackInSlot(int slot) {
			IItemHandler container = container();
			return container == null || slot >= container.getSlots() ? ItemStack.EMPTY
					: container.getStackInSlot(slot);
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			IItemHandler container = container();
			if (container == null || slot >= container.getSlots())
				return stack;
			return container.insertItem(slot, stack, simulate);
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			IItemHandler container = container();
			if (container == null || slot >= container.getSlots())
				return ItemStack.EMPTY;
			return container.extractItem(slot, amount, simulate);
		}

		@Override
		public int getSlotLimit(int slot) {
			IItemHandler container = container();
			return container == null || slot >= container.getSlots() ? 0 : container.getSlotLimit(slot);
		}

		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			IItemHandler container = container();
			return container != null && slot < container.getSlots() && container.isItemValid(slot, stack);
		}
	}

}
