package com.piissedpanda.createairspace;

import com.piissedpanda.createairspace.block.Luggage.LuggageType;
import com.simibubi.create.AllPartialModels;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

public class CAPartialModels {

	public static final PartialModel

	BARRIER_POST = block("barrier_post/block"), BARRIER_POST_GUARD = block("barrier_post/guard"),
			WINDSOCK_SOCK = block("windsock/sock"),
			AIRPORT_BEACON_HEAD = block("airport_beacon/head"),
			AIRPORT_BEACON_LENS = block("airport_beacon/lens");

	private static PartialModel block(String path) {
		return PartialModel.of(CreateAirspace.asResource("block/" + path));
	}

	public static void init() {
		// init static fields
	}

	// Create renders packages on chain conveyors and as entities from these maps
	public static void initLuggage() {
		for (LuggageType type : LuggageType.values()) {
			PartialModel rigging = item("luggage/" + type.id + "_rigging");
			for (DyeColor color : DyeColor.values()) {
				ResourceLocation id = CreateAirspace.asResource(type.itemName(color));
				AllPartialModels.PACKAGES.put(id, item(type.itemName(color)));
				AllPartialModels.PACKAGE_RIGGING.put(id, rigging);
			}
		}
	}

	private static PartialModel item(String path) {
		return PartialModel.of(CreateAirspace.asResource("item/" + path));
	}

}
