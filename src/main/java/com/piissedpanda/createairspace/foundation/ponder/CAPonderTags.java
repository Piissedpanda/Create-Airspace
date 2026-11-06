package com.piissedpanda.createairspace.foundation.ponder;

import com.piissedpanda.createairspace.CABlocks;
import com.piissedpanda.createairspace.CAItems;
import com.piissedpanda.createairspace.CreateAirspace;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class CAPonderTags {

	public static final ResourceLocation AIRPORT_ACCESSORIES = loc("airport_accessories");

	private static ResourceLocation loc(String id) {
		return CreateAirspace.asResource(id);
	}

	public static void register(PonderTagRegistrationHelper<ResourceLocation> helper) {
		PonderTagRegistrationHelper<RegistryEntry<?, ?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

		helper.registerTag(AIRPORT_ACCESSORIES)
				.addToIndex()
				.item(CABlocks.BARRIER_POST.get(), true, false)
				.title("Airport Accessories")
				.description("Blocks to build and decorate your airport")
				.register();

		HELPER.addToTag(AIRPORT_ACCESSORIES)
				.add(CABlocks.BARRIER_POST)
				.add(CABlocks.ANDESITE_BARRIER_POST)
				.add(CABlocks.BRASS_BARRIER_POST)
				.add(CABlocks.COPPER_BARRIER_POST)
				.add(CABlocks.TRAIN_BARRIER_POST)
				.add(CABlocks.AIRFIELD_SIGN)
				.add(CABlocks.GATE_SIGN)
				.add(CABlocks.EDGE_LIGHT)
				.add(CABlocks.WINDSOCK)
				.add(CABlocks.AIRPORT_BEACON)
				.add(CABlocks.LUGGAGE_SCALE)
				.add(CABlocks.PAPI_LIGHT)
				.add(CABlocks.STOP_BAR)
				.add(CABlocks.RUNWAY_GUARD_LIGHT)
				.add(CABlocks.METAL_DETECTOR)
				.add(CABlocks.BAGGAGE_SCANNER)
				.add(CABlocks.ROLLER_FLOOR)
				.add(CAItems.PAINT_PAIL);
	}

}
