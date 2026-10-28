package com.piissedpanda.createairspace.block.CheckIn;

import com.simibubi.create.content.logistics.box.PackageItem;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.items.ItemStackHandler;

// Weight of a piece of luggage in kg: the case, blocks with the mass Aeronautics (Sable) gives them,
// other items by a share of their stack
public class LuggageWeight {

	public static final double CASE = 2.5, ITEM_STACK = 2.0, OVERWEIGHT = 23;
	private static final boolean SABLE = ModList.get()
			.isLoaded("sable");

	public static double of(ItemStack luggage) {
		if (!PackageItem.isPackage(luggage))
			return 0;
		double weight = CASE;
		ItemStackHandler contents = PackageItem.getContents(luggage);
		for (int i = 0; i < contents.getSlots(); i++) {
			ItemStack stack = contents.getStackInSlot(i);
			if (stack.isEmpty())
				continue;
			if (stack.getItem() instanceof BlockItem block)
				weight += mass(block.getBlock()
						.defaultBlockState()) * stack.getCount();
			else
				weight += ITEM_STACK * stack.getCount() / stack.getMaxStackSize();
		}
		return weight;
	}

	private static double mass(BlockState state) {
		return SABLE ? SableMass.of(state) : 1;
	}

}
