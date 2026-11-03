package com.piissedpanda.createairspace.block.CheckIn;

import java.util.List;

import javax.annotation.Nullable;

import com.piissedpanda.createairspace.CABlockEntities;
import com.piissedpanda.createairspace.CATags;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;

// Luggage rolls over the scale like over Create's item drain: from a belt, it stops in the middle
// to be weighed and moves on. Luggage put on by hand stays until it is taken off.
public class LuggageScaleBlockEntity extends SmartBlockEntity {

	public static final int WEIGH_TIME = 10;
	public static final int MAX_KG = 100;

	@Nullable
	TransportedItemStack heldItem;
	boolean placedByHand;
	int weighingTicks;
	private double weight = -1;

	// Comparator signal: 0 at the minimum, 15 at the maximum
	public static final int DEFAULT_MIN_KG = 0, DEFAULT_MAX_KG = 30;
	int minKg = DEFAULT_MIN_KG, maxKg = DEFAULT_MAX_KG;

	public LuggageScaleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CABlockEntities.LUGGAGE_SCALE.get(),
				(be, side) -> side != null && side.getAxis()
						.isHorizontal() ? be.new SideHandler(side) : null);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		behaviours.add(new DirectBeltInputBehaviour(this).allowingBeltFunnels()
				.setInsertionHandler(this::insertFromBelt));
	}

	private ItemStack insertFromBelt(TransportedItemStack transported, Direction side, boolean simulate) {
		ItemStack stack = transported.stack;
		if (heldItem != null || !stack.is(CATags.LUGGAGE))
			return stack;
		if (simulate)
			return stack.copyWithCount(stack.getCount() - 1);

		TransportedItemStack held = transported.copy();
		held.stack = stack.copyWithCount(1);
		held.beltPosition = 0;
		held.prevBeltPosition = 0;
		held.prevSideOffset = held.sideOffset;
		held.insertedFrom = side;
		setHeld(held, false);
		return stack.copyWithCount(stack.getCount() - 1);
	}

	public void placeByHand(ItemStack luggage, Direction facing) {
		TransportedItemStack held = new TransportedItemStack(luggage.copyWithCount(1));
		held.beltPosition = .5f;
		held.prevBeltPosition = .5f;
		held.insertedFrom = facing;
		setHeld(held, true);
	}

	public ItemStack takeLuggage() {
		ItemStack luggage = getLuggage();
		setHeld(null, false);
		return luggage;
	}

	private void setHeld(@Nullable TransportedItemStack held, boolean byHand) {
		heldItem = held;
		placedByHand = byHand;
		weighingTicks = 0;
		weight = -1;
		if (level.isClientSide)
			return;
		notifyUpdate();
		level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
	}

	public ItemStack getLuggage() {
		return heldItem == null ? ItemStack.EMPTY : heldItem.stack;
	}

	public double getWeight() {
		if (weight < 0)
			weight = LuggageWeight.of(getLuggage());
		return weight;
	}

	public int getSignal() {
		if (heldItem == null)
			return 0;
		return signal(getWeight(), minKg, maxKg);
	}

	public static int signal(double weight, int minKg, int maxKg) {
		float share = (float) (weight - minKg) / Math.max(1, maxKg - minKg);
		return Mth.clamp(Math.round(share * 15), 0, 15);
	}

	public void setRange(int min, int max) {
		minKg = Mth.clamp(min, 0, MAX_KG - 1);
		maxKg = Mth.clamp(max, minKg + 1, MAX_KG);
		notifyUpdate();
		level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
	}

	@Override
	public void tick() {
		super.tick();
		if (heldItem == null || placedByHand)
			return;

		if (weighingTicks > 0) {
			heldItem.prevBeltPosition = .5f;
			weighingTicks--;
			return;
		}

		heldItem.prevBeltPosition = heldItem.beltPosition;
		heldItem.prevSideOffset = heldItem.sideOffset;
		float before = heldItem.beltPosition;
		heldItem.beltPosition += 1 / 8f;

		// Stop in the middle to weigh
		if (before < .5f && heldItem.beltPosition >= .5f) {
			heldItem.beltPosition = .5f;
			weighingTicks = WEIGH_TIME;
			return;
		}
		if (heldItem.beltPosition <= 1)
			return;
		heldItem.beltPosition = 1;
		if (!level.isClientSide || isVirtual())
			passOn();
	}

	// Same as Create's item drain: belt funnel, next belt or drop in front
	private void passOn() {
		Direction side = heldItem.insertedFrom;
		ItemStack toFunnel = getBehaviour(DirectBeltInputBehaviour.TYPE).tryExportingToBeltFunnel(heldItem.stack,
				side.getOpposite(), false);
		if (toFunnel != null) {
			if (toFunnel.isEmpty())
				setHeld(null, false);
			return;
		}

		BlockPos next = worldPosition.relative(side);
		DirectBeltInputBehaviour nextInput = BlockEntityBehaviour.get(level, next, DirectBeltInputBehaviour.TYPE);
		if (nextInput == null) {
			if (level.getBlockState(next)
					.isFaceSturdy(level, next, side.getOpposite()))
				return;
			Vec3 out = VecHelper.getCenterOf(worldPosition)
					.add(Vec3.atLowerCornerOf(side.getNormal())
							.scale(.75));
			ItemEntity entity = new ItemEntity(level, out.x, out.y + 6 / 16f, out.z, heldItem.stack);
			entity.setDeltaMovement(Vec3.atLowerCornerOf(side.getNormal())
					.scale(1 / 8f)
					.add(0, 1 / 8f, 0));
			entity.setDefaultPickUpDelay();
			level.addFreshEntity(entity);
			setHeld(null, false);
			return;
		}
		if (!nextInput.canInsertFromSide(side))
			return;
		if (nextInput.handleInsertion(heldItem.copy(), side, false)
				.isEmpty())
			setHeld(null, false);
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		heldItem = tag.contains("HeldItem") ? TransportedItemStack.read(tag.getCompound("HeldItem"), registries) : null;
		placedByHand = tag.getBoolean("PlacedByHand");
		weighingTicks = tag.getInt("WeighingTicks");
		minKg = tag.getInt("MinKg");
		maxKg = tag.contains("MaxKg") ? tag.getInt("MaxKg") : 30;
		weight = -1;
	}

	@Override
	protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		if (heldItem != null)
			tag.put("HeldItem", heldItem.serializeNBT(registries));
		tag.putBoolean("PlacedByHand", placedByHand);
		tag.putInt("WeighingTicks", weighingTicks);
		tag.putInt("MinKg", minKg);
		tag.putInt("MaxKg", maxKg);
	}

	// Funnels and chutes: luggage inserted from a side moves across like from a
	// belt
	private class SideHandler implements IItemHandler {

		private final Direction side;

		SideHandler(Direction side) {
			this.side = side;
		}

		@Override
		public int getSlots() {
			return 1;
		}

		@Override
		public ItemStack getStackInSlot(int slot) {
			return getLuggage();
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (heldItem != null || !stack.is(CATags.LUGGAGE))
				return stack;
			if (!simulate) {
				TransportedItemStack held = new TransportedItemStack(stack.copyWithCount(1));
				held.insertedFrom = side.getOpposite();
				setHeld(held, false);
			}
			return stack.copyWithCount(stack.getCount() - 1);
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			if (heldItem == null || amount <= 0)
				return ItemStack.EMPTY;
			ItemStack luggage = getLuggage();
			if (!simulate)
				setHeld(null, false);
			return luggage.copy();
		}

		@Override
		public int getSlotLimit(int slot) {
			return 1;
		}

		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			return stack.is(CATags.LUGGAGE);
		}
	}

}
