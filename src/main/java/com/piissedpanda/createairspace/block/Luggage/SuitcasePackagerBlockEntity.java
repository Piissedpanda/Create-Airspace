package com.piissedpanda.createairspace.block.Luggage;

import java.util.List;

import javax.annotation.Nullable;

import com.piissedpanda.createairspace.CABlockEntities;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.box.PackageStyles;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import com.simibubi.create.content.logistics.packager.PackagingRequest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

// A packager that wraps items into luggage instead of cardboard boxes
public class SuitcasePackagerBlockEntity extends PackagerBlockEntity {

	// Fixed luggage colour, random if null
	@Nullable
	public DyeColor color;

	public SuitcasePackagerBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
		super(typeIn, pos, state);
	}

	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CABlockEntities.SUITCASE_PACKAGER.get(),
				(be, context) -> be.inventory);
	}

	@Override
	public void attemptToSend(List<PackagingRequest> queuedRequests) {
		ItemStack previousBox = heldBox;
		int previousQueued = queuedExitingPackages.size();
		super.attemptToSend(queuedRequests);

		// Swap the boxes that were just packed for luggage, address and contents stay
		if (heldBox != previousBox)
			heldBox = toLuggage(heldBox);
		for (int i = previousQueued; i < queuedExitingPackages.size(); i++) {
			BigItemStack queued = queuedExitingPackages.get(i);
			queuedExitingPackages.set(i, new BigItemStack(toLuggage(queued.stack), queued.count));
		}
	}

	private ItemStack toLuggage(ItemStack box) {
		if (!PackageStyles.STANDARD_BOXES.contains(box.getItem())
				&& !PackageStyles.RARE_BOXES.contains(box.getItem()))
			return box;
		LuggageType[] types = LuggageType.values();
		DyeColor[] colors = DyeColor.values();
		LuggageType type = types[level.random.nextInt(types.length)];
		DyeColor luggageColor = color != null ? color : colors[level.random.nextInt(colors.length)];
		return LuggageItem.convert(box, type, luggageColor);
	}

	@Override
	protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(compound, registries, clientPacket);
		color = compound.contains("LuggageColor") ? DyeColor.byName(compound.getString("LuggageColor"), null)
				: null;
	}

	@Override
	protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(compound, registries, clientPacket);
		if (color != null)
			compound.putString("LuggageColor", color.getSerializedName());
	}

}
