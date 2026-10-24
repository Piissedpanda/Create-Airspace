package com.piissedpanda.createairspace;

import java.util.EnumMap;
import java.util.Map;

import com.piissedpanda.createairspace.block.Luggage.LuggageItem;
import com.piissedpanda.createairspace.block.Luggage.LuggageType;
import com.simibubi.create.AllTags.AllItemTags;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.ItemEntry;

import net.minecraft.world.item.DyeColor;

public class CAItems {
	private static final CreateRegistrate REGISTRATE = CreateAirspace.REGISTRATE;

	// Blank sign plate while it moves through the sequenced assembly
	public static final ItemEntry<SequencedAssemblyItem> INCOMPLETE_AIRFIELD_SIGN = REGISTRATE
			.item("incomplete_airfield_sign", SequencedAssemblyItem::new)
			.model((c, p) -> p.withExistingParent(c.getName(), p.modLoc("block/airfield_sign/base"))
					.texture("face", "create:block/industrial_iron_block"))
			.register();

	public static final ItemEntry<SequencedAssemblyItem> INCOMPLETE_SUITCASE_PACKAGER = REGISTRATE
			.item("incomplete_suitcase_packager", SequencedAssemblyItem::new)
			.model((c, p) -> p.withExistingParent(c.getName(), p.modLoc("block/suitcase_packager/item")))
			.register();

	// Every luggage type in every colour, only the suitcase packager makes them
	public static final Map<LuggageType, Map<DyeColor, ItemEntry<LuggageItem>>> LUGGAGE = new EnumMap<>(
			LuggageType.class);

	static {
		for (LuggageType type : LuggageType.values()) {
			Map<DyeColor, ItemEntry<LuggageItem>> colors = new EnumMap<>(DyeColor.class);
			for (DyeColor color : DyeColor.values())
				colors.put(color, REGISTRATE.item(type.itemName(color), p -> new LuggageItem(p, type, color))
						.properties(p -> p.stacksTo(1))
						.tag(AllItemTags.PACKAGES.tag, CATags.LUGGAGE)
						.model((c, p) -> p.withExistingParent(c.getName(), p.modLoc("item/luggage/" + type.id))
								.texture("body", type.bodyTexture(color))
								.texture("particle", type.bodyTexture(color)))
						.register());
			LUGGAGE.put(type, colors);
		}
	}

	public static void register() {
	}
}
