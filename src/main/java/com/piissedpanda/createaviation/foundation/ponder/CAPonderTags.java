package com.piissedpanda.createaviation.foundation.ponder;

import com.piissedpanda.createaviation.CABlocks;
import com.piissedpanda.createaviation.CreateAviation;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

public class CAPonderTags {

	public static final ResourceLocation AIRPORT_ACCESSORIES = loc("airport_accessories");

	private static ResourceLocation loc(String id) {
		return CreateAviation.asResource(id);
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
				.add(CABlocks.TRAFFIC_CONE)
				.add(CABlocks.TERMINAL_SEATS.get(DyeColor.WHITE))
				.add(CABlocks.AIRFIELD_SIGN)
				.add(CABlocks.EDGE_LIGHT)
				.add(CABlocks.WINDSOCK);
	}

}
