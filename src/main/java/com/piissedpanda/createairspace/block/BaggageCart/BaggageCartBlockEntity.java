package com.piissedpanda.createairspace.block.BaggageCart;

import java.util.ArrayList;
import java.util.List;

import com.piissedpanda.createairspace.CABlockEntities;
import com.piissedpanda.createairspace.CATags;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

public class BaggageCartBlockEntity extends SmartBlockEntity {

	public static final int LAYERS = 3;
	public static final int MAX_CART_SIZE = 6;

	public final LuggageStack inventory = new LuggageStack();

	public BaggageCartBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CABlockEntities.BAGGAGE_CART.get(),
				(be, context) -> be.cartInventory());
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
	}

	// All carts connected to this one share their luggage, this one comes first
	public IItemHandler cartInventory() {
		List<BaggageCartBlockEntity> cart = getCart(level, worldPosition);
		IItemHandlerModifiable[] stacks = new IItemHandlerModifiable[cart.size()];
		for (int i = 0; i < stacks.length; i++)
			stacks[i] = cart.get(i).inventory;
		return new CombinedInvWrapper(stacks);
	}

	public static List<BaggageCartBlockEntity> getCart(Level level, BlockPos start) {
		List<BaggageCartBlockEntity> cart = new ArrayList<>();
		List<BlockPos> frontier = new ArrayList<>(List.of(start));
		List<BlockPos> visited = new ArrayList<>();
		while (!frontier.isEmpty() && cart.size() < MAX_CART_SIZE) {
			BlockPos pos = frontier.removeFirst();
			if (visited.contains(pos))
				continue;
			visited.add(pos);
			if (!(level.getBlockEntity(pos) instanceof BaggageCartBlockEntity be))
				continue;
			cart.add(be);
			for (Direction side : Direction.Plane.HORIZONTAL)
				frontier.add(pos.relative(side));
		}
		return cart;
	}

	public static void invalidateCart(Level level, BlockPos pos) {
		for (BaggageCartBlockEntity be : getCart(level, pos))
			level.invalidateCapabilities(be.getBlockPos());
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		inventory.deserializeNBT(registries, tag.getCompound("Luggage"));
	}

	@Override
	protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		tag.put("Luggage", inventory.serializeNBT(registries));
	}

	@Override
	protected AABB createRenderBoundingBox() {
		return new AABB(worldPosition).expandTowards(0, 1, 0);
	}

	// One piece of luggage per layer, stacked from the bottom without gaps
	public class LuggageStack extends ItemStackHandler {

		public LuggageStack() {
			super(LAYERS);
		}

		public int firstEmpty() {
			for (int i = 0; i < getSlots(); i++)
				if (getStackInSlot(i).isEmpty())
					return i;
			return -1;
		}

		public int top() {
			for (int i = getSlots() - 1; i >= 0; i--)
				if (!getStackInSlot(i).isEmpty())
					return i;
			return -1;
		}

		@Override
		public int getSlotLimit(int slot) {
			return 1;
		}

		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			return stack.is(CATags.LUGGAGE);
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (slot != firstEmpty())
				return stack;
			return super.insertItem(slot, stack, simulate);
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			if (slot != top())
				return ItemStack.EMPTY;
			return super.extractItem(slot, amount, simulate);
		}

		@Override
		protected void onContentsChanged(int slot) {
			notifyUpdate();
		}
	}

}
