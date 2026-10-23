package com.piissedpanda.createairspace.block.AirportBeacon;

import com.piissedpanda.createairspace.CreateAirspace;
import com.piissedpanda.createairspace.foundation.config.CAConfigs;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

// Briefly brightens the screen when a beacon beam points straight at the camera
public class AirportBeaconGlare {

	public static final ResourceLocation LAYER = CreateAirspace.asResource("airport_beacon_glare");
	private static final float MAX_ALPHA = 0.22f;

	private static float strongest;
	private static int strongestColor;
	private static float shown;
	private static int shownColor;

	public static boolean isEnabled() {
		if (!CAConfigs.client().beaconGlare.get())
			return false;
		return !Minecraft.getInstance().options.hideLightningFlash()
				.get();
	}

	// Called by the renderer for every visible beam that points at the camera
	public static void offer(float intensity, int rgb) {
		if (intensity <= strongest)
			return;
		strongest = intensity;
		strongestColor = rgb;
	}

	public static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
		if (strongest >= shown) {
			shown = strongest;
			shownColor = strongestColor;
		} else
			shown *= (float) Math.pow(0.5, deltaTracker.getRealtimeDeltaTicks());
		strongest = 0;

		if (shown < 0.01f || !isEnabled())
			return;
		int alpha = (int) (shown * MAX_ALPHA * 255);
		graphics.fill(0, 0, graphics.guiWidth(), graphics.guiHeight(), alpha << 24 | shownColor);
	}

}
