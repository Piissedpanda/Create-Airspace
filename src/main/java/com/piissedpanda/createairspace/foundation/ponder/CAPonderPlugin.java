package com.piissedpanda.createairspace.foundation.ponder;

import com.piissedpanda.createairspace.CreateAirspace;

import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class CAPonderPlugin implements PonderPlugin {

	@Override
	public String getModId() {
		return CreateAirspace.MODID;
	}

	@Override
	public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
		CAPonderScenes.register(helper);
	}

	@Override
	public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
		CAPonderTags.register(helper);
	}

}
