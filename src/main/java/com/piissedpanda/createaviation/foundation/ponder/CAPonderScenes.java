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

		HELPER.addStoryBoard(CABlocks.BARRIER_POST, "barrier_post", AirportAccessoriesScenes::barrierPost,
				CAPonderTags.AIRPORT_ACCESSORIES);
	}

}
