package com.piissedpanda.createairspace.foundation.ponder;

import com.piissedpanda.createairspace.CABlocks;
import com.piissedpanda.createairspace.CAItems;
import com.piissedpanda.createairspace.foundation.ponder.scenes.AirportAccessoriesScenes;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class CAPonderScenes {

	public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
		PonderSceneRegistrationHelper<ItemProviderEntry<?, ?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

		HELPER.forComponents(CABlocks.BARRIER_POSTS)
				.addStoryBoard("barrier_post", AirportAccessoriesScenes::barrierPost,
						CAPonderTags.AIRPORT_ACCESSORIES);
		HELPER.addStoryBoard(CABlocks.AIRFIELD_SIGN, "airfield_sign", AirportAccessoriesScenes::airfieldSign,
				CAPonderTags.AIRPORT_ACCESSORIES);
		HELPER.addStoryBoard(CABlocks.EDGE_LIGHT, "edge_light", AirportAccessoriesScenes::edgeLight,
				CAPonderTags.AIRPORT_ACCESSORIES);
		HELPER.forComponents(CAItems.PAINT_PAIL)
				.addStoryBoard("runway_marking", AirportAccessoriesScenes::runwayMarking,
						CAPonderTags.AIRPORT_ACCESSORIES);
		HELPER.addStoryBoard(CABlocks.WINDSOCK, "windsock", AirportAccessoriesScenes::windsock,
				CAPonderTags.AIRPORT_ACCESSORIES);
		HELPER.addStoryBoard(CABlocks.AIRPORT_BEACON, "airport_beacon", AirportAccessoriesScenes::airportBeacon,
				CAPonderTags.AIRPORT_ACCESSORIES);
	}

}
