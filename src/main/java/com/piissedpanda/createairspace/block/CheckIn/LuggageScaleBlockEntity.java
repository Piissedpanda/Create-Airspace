package com.piissedpanda.createairspace.block.CheckIn;

import java.util.List;

import com.piissedpanda.createairspace.CABlockEntities;
import com.piissedpanda.createairspace.CATags;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.ItemStackHandler;

public class LuggageScaleBlockEntity extends SmartBlockEntity {

	public final ItemStackHandler inventory = new ItemStackHandler(1) {
		@Override
		public int getSlotLimit(int slot) {
			return 1;
		}

		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			return stack.is(CATags.LUGGAGE);
		}

		@Override
		protected void onContentsChanged(int slot) {
			weight = -1;
			notifyUpdate();
			if (level != null)
				level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
		}
	};

	private double weight = -1;

	public LuggageScaleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CABlockEntities.LUGGAGE_SCALE.get(),
				(be, context) -> be.inventory);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
	}

	public ItemStack getLuggage() {
		return inventory.getStackInSlot(0);
	}

	public double getWeight() {
		if (weight < 0)
			weight = LuggageWeight.of(getLuggage());
		return weight;
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		inventory.deserializeNBT(registries, tag.getCompound("Luggage"));
		weight = -1;
	}

	@Override
	protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		tag.put("Luggage", inventory.serializeNBT(registries));
	}

}
