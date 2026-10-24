package com.piissedpanda.createairspace.foundation.config;

import net.createmod.catnip.config.ConfigBase;

public class CAClient extends ConfigBase {

	public final ConfigGroup client = group(0, "client", Comments.client);

	public final ConfigBool beaconGlare = b(true, "beaconGlare", Comments.beaconGlare);

	public final ConfigFloat earDefendersVolume = f(0.3f, 0, 1, "earDefendersVolume", Comments.earDefendersVolume);

	@Override
	public String getName() {
		return "client";
	}

	private static class Comments {
		static String client = "Client-only settings - If you're looking for general settings, look inside your worlds serverconfig folder!";
		static String earDefendersVolume = "Volume of sounds while wearing Ear Defenders. Music, menu sounds and voices stay unchanged.";
		static String beaconGlare = "Airport Beacons dazzle and briefly brighten the screen when a light beam points at you. Also off while \"Hide Lightning Flashes\" is on.";
	}

}
