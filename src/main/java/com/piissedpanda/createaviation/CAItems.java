package com.piissedpanda.createaviation;

import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.ItemEntry;

public class CAItems {
	private static final CreateRegistrate REGISTRATE = CreateAviation.REGISTRATE;

	// Blank sign plate while it moves through the sequenced assembly
	public static final ItemEntry<SequencedAssemblyItem> INCOMPLETE_AIRFIELD_SIGN = REGISTRATE
			.item("incomplete_airfield_sign", SequencedAssemblyItem::new)
			.model((c, p) -> p.withExistingParent(c.getName(), p.modLoc("block/airfield_sign/base"))
					.texture("face", "create:block/industrial_iron_block"))
			.register();

	public static void register() {
	}
}
