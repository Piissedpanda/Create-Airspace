package com.piissedpanda.createairspace;

import com.piissedpanda.createairspace.block.AirfieldSign.AirfieldSignDisplayTarget;
import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class CADisplayTargets {
	private static final CreateRegistrate REGISTRATE = CreateAirspace.REGISTRATE;

	public static final RegistryEntry<DisplayTarget, AirfieldSignDisplayTarget> AIRFIELD_SIGN = REGISTRATE
			.displayTarget("airfield_sign", AirfieldSignDisplayTarget::new)
			.register();

	public static void register() {
	}
}
