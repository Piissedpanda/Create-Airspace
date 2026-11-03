package com.piissedpanda.createairspace.block.Cargo;

import java.util.List;

import javax.annotation.Nullable;

import com.piissedpanda.createairspace.CABlockEntities;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.infrastructure.config.AllConfigs;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

// Every block holds as many stacks as an item vault block, a finished container
// offers all of them as one inventory
public class CargoContainerBlockEntity extends SmartBlockEntity {

	public final ItemStackHandler inventory;

	// Only used while forming, the LD3 contour faces the player
	public Direction placedFacing = Direction.NORTH;

	@Nullable
	private IItemHandler containerInventory;

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

	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CABlockEntities.CARGO_CONTAINER.get(),
				(be, context) -> be.getContainerInventory());
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
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
		if (!clientPacket)
			inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
	}

	@Override
	protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		if (!clientPacket)
			tag.put("Inventory", inventory.serializeNBT(registries));
	}

}
