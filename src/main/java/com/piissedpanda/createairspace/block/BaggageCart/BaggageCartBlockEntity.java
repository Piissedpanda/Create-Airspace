package com.piissedpanda.createairspace.block.BaggageCart;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;

import com.piissedpanda.createairspace.CABlockEntities;
import com.piissedpanda.createairspace.CATags;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

// Carts join into groups of up to 3 x 3 blocks around a controller, a group shares its luggage
public class BaggageCartBlockEntity extends SmartBlockEntity {

	public static final int LAYERS = 2;
	public static final int MAX_SIZE = 3;

	public final LuggageStack inventory = new LuggageStack();

	// Relative, so the cart still works after moving on a contraption
	@Nullable
	private BlockPos controllerOffset;

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

	@Override
	public void initialize() {
		super.initialize();
		if (!level.isClientSide && controllerOffset == null)
			join(this);
	}

	@Nullable
	public BlockPos getController() {
		return controllerOffset == null ? null : worldPosition.offset(controllerOffset);
	}

	private void setController(@Nullable BlockPos controller) {
		controllerOffset = controller == null ? null : controller.subtract(worldPosition);
		notifyUpdate();
	}

	// All carts of this group share their luggage, this one comes first
	public IItemHandler cartInventory() {
		List<BaggageCartBlockEntity> cart = getCart(level, worldPosition);
		cart.remove(this);
		cart.addFirst(this);
		IItemHandlerModifiable[] stacks = new IItemHandlerModifiable[cart.size()];
		for (int i = 0; i < stacks.length; i++)
			stacks[i] = cart.get(i).inventory;
		return new CombinedInvWrapper(stacks);
	}

	public static List<BaggageCartBlockEntity> getCart(Level level, BlockPos pos) {
		if (!(level.getBlockEntity(pos) instanceof BaggageCartBlockEntity be) || be.getController() == null)
			return new ArrayList<>(List.of());
		return members(level, be.getController());
	}

	private static List<BaggageCartBlockEntity> members(Level level, BlockPos controller) {
		List<BaggageCartBlockEntity> cart = new ArrayList<>();
		int range = MAX_SIZE - 1;
		for (BlockPos pos : BlockPos.betweenClosed(controller.offset(-range, 0, -range),
				controller.offset(range, 0, range)))
			if (level.getBlockEntity(pos) instanceof BaggageCartBlockEntity be && controller.equals(be.getController()))
				cart.add(be);
		return cart;
	}

	// Join a neighbouring cart if the group still fits into 3 x 3, else start a new
	// one
	private static void join(BaggageCartBlockEntity be) {
		Level level = be.level;
		BlockPos pos = be.worldPosition;
		BlockPos controller = pos;
		for (Direction side : Direction.Plane.HORIZONTAL) {
			if (!(level.getBlockEntity(pos.relative(side)) instanceof BaggageCartBlockEntity neighbour))
				continue;
			BlockPos other = neighbour.getController();
			if (other == null || !fits(members(level, other), pos))
				continue;
			controller = other;
			break;
		}
		be.setController(controller);
		refreshAround(level, pos);
	}

	private static boolean fits(List<BaggageCartBlockEntity> cart, BlockPos added) {
		int minX = added.getX(), maxX = added.getX(), minZ = added.getZ(), maxZ = added.getZ();
		for (BaggageCartBlockEntity be : cart) {
			BlockPos pos = be.getBlockPos();
			minX = Math.min(minX, pos.getX());
			maxX = Math.max(maxX, pos.getX());
			minZ = Math.min(minZ, pos.getZ());
			maxZ = Math.max(maxZ, pos.getZ());
		}
		return maxX - minX < MAX_SIZE && maxZ - minZ < MAX_SIZE;
	}

	// After a cart was removed, the rest of its group may split up
	public static void reform(Level level, BlockPos controller) {
		List<BaggageCartBlockEntity> cart = members(level, controller);
		cart.sort(Comparator.comparing(BaggageCartBlockEntity::getBlockPos));
		for (BaggageCartBlockEntity be : cart)
			be.setController(null);
		for (BaggageCartBlockEntity be : cart)
			join(be);
	}

	// The net shows on every side that does not belong to the same group
	public static void refreshAround(Level level, BlockPos pos) {
		refresh(level, pos);
		for (Direction side : Direction.Plane.HORIZONTAL)
			refresh(level, pos.relative(side));
		for (BaggageCartBlockEntity be : getCart(level, pos))
			level.invalidateCapabilities(be.getBlockPos());
	}

	private static void refresh(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		if (!(state.getBlock() instanceof BaggageCartBlock)
				|| !(level.getBlockEntity(pos) instanceof BaggageCartBlockEntity be))
			return;
		BlockState updated = state;
		for (Direction side : Direction.Plane.HORIZONTAL) {
			boolean connected = level.getBlockEntity(pos.relative(side)) instanceof BaggageCartBlockEntity other
					&& be.getController() != null && be.getController()
							.equals(other.getController());
			updated = updated.setValue(BaggageCartBlock.CONNECTED.get(side), connected);
		}
		if (updated != state)
			level.setBlock(pos, updated, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		inventory.deserializeNBT(registries, tag.getCompound("Luggage"));
		controllerOffset = NbtUtils.readBlockPos(tag, "Controller")
				.orElse(null);
	}

	@Override
	protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		tag.put("Luggage", inventory.serializeNBT(registries));
		if (controllerOffset != null)
			tag.put("Controller", NbtUtils.writeBlockPos(controllerOffset));
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
