package com.piissedpanda.createairspace.block.Luggage;

import com.piissedpanda.createairspace.CAItems;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.box.PackageStyles;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

// Luggage works like a Create package: address, contents, frogports, chain conveyors
public class LuggageItem extends PackageItem {

	public final LuggageType type;
	public final DyeColor color;

	public LuggageItem(Properties properties, LuggageType type, DyeColor color) {
		super(properties, type.style);
		this.type = type;
		this.color = color;

		// Only the suitcase packager makes luggage, Create's packagers keep their boxes
		PackageStyles.STANDARD_BOXES.remove(this);
		PackageStyles.RARE_BOXES.remove(this);
	}

	@Override
	public String getDescriptionId() {
		return getOrCreateDescriptionId();
	}

	public static ItemStack convert(ItemStack box, LuggageType type, DyeColor color) {
		return box.transmuteCopy(CAItems.LUGGAGE.get(type)
				.get(color), box.getCount());
	}

}
