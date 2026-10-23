package com.piissedpanda.createaviation.foundation.ponder;

import com.piissedpanda.createaviation.CABlocks;
import com.piissedpanda.createaviation.foundation.ponder.scenes.AirportAccessoriesScenes;
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
		HELPER.forComponents(CABlocks.TERMINAL_SEATS)
				.addStoryBoard("terminal_seat", AirportAccessoriesScenes::terminalSeat,
						CAPonderTags.AIRPORT_ACCESSORIES);
		HELPER.addStoryBoard(CABlocks.AIRFIELD_SIGN, "airfield_sign", AirportAccessoriesScenes::airfieldSign,
				CAPonderTags.AIRPORT_ACCESSORIES);
		HELPER.addStoryBoard(CABlocks.EDGE_LIGHT, "edge_light", AirportAccessoriesScenes::edgeLight,
				CAPonderTags.AIRPORT_ACCESSORIES);
		HELPER.addStoryBoard(CABlocks.WINDSOCK, "windsock", AirportAccessoriesScenes::windsock,
				CAPonderTags.AIRPORT_ACCESSORIES);
		HELPER.addStoryBoard(CABlocks.TRAFFIC_CONE, "traffic_cone", AirportAccessoriesScenes::trafficCone,
				CAPonderTags.AIRPORT_ACCESSORIES);
	}

}
