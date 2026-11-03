package com.piissedpanda.createairspace.block.Cargo;

import java.util.List;

import javax.annotation.Nullable;

import com.piissedpanda.createairspace.CABlockEntities;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

// Hands items through to the container in front, which may be a separate physics
// object, so it is found with a short ray instead of the neighbouring block
public class ContainerPortBlockEntity extends SmartBlockEntity {

	private static final double REACH = 0.5;
	private static final int SEARCH_INTERVAL = 10;

	@Nullable
	private BlockPos target;
	private int searchCooldown;

	private final IItemHandler passage = new Passage();

	public ContainerPortBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CABlockEntities.CONTAINER_PORT.get(),
				(be, side) -> side == be.getBlockState()
						.getValue(HorizontalDirectionalBlock.FACING) ? null : be.passage);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
	}

	@Override
	public void tick() {
		super.tick();
		if (level.isClientSide || searchCooldown-- > 0)
			return;
		searchCooldown = SEARCH_INTERVAL;
		target = findContainer();
	}

	@Nullable
	private BlockPos findContainer() {
		Direction facing = getBlockState().getValue(HorizontalDirectionalBlock.FACING);
		Vec3 start = Vec3.atCenterOf(worldPosition)
				.relative(facing, 0.5);
		Vec3 end = start.relative(facing, REACH);
		BlockHitResult clip = level.clip(
				new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty()));
		if (clip.getType() == HitResult.Type.MISS)
			return null;
		BlockState state = level.getBlockState(clip.getBlockPos());
		if (!(state.getBlock() instanceof CargoContainerBlock) || !state.getValue(CargoContainerBlock.FORMED))
			return null;
		return clip.getBlockPos();
	}

	@Nullable
	private IItemHandler container() {
		if (target == null || !(level.getBlockEntity(target) instanceof CargoContainerBlockEntity be))
			return null;
		return be.getContainerInventory();
	}

	private boolean unloading() {
		return getBlockState().getValue(ContainerPortBlock.UNLOADING);
	}

	// Loading only lets items in, unloading only lets them out
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
			if (container == null || unloading() || slot >= container.getSlots())
				return stack;
			return container.insertItem(slot, stack, simulate);
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			IItemHandler container = container();
			if (container == null || !unloading() || slot >= container.getSlots())
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
			return container != null && !unloading() && slot < container.getSlots()
					&& container.isItemValid(slot, stack);
		}
	}

}
