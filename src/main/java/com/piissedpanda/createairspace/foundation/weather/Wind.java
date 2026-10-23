package com.piissedpanda.createairspace.foundation.weather;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

// Only depends on game time and weather, so server and all clients agree without syncing
public class Wind {

	// The wind blows towards this yaw in degrees (0 = south, 90 = west)
	public static float getDirection(double time) {
		double drift = time / 24000 * 90;
		double swing = 70 * Math.sin(time / 3000) + 25 * Math.sin(time / 700 + 2) + 6 * Math.sin(time / 90);
		return (float) Mth.wrapDegrees(drift + swing);
	}

	// 0 = calm, 1 = storm
	public static float getStrength(Level level, BlockPos pos, double time, float partialTicks) {
		double strength = 0.35 + 0.2 * Math.sin(time / 2000 + 1);
		strength += 0.08 * Math.sin(time / 170) * Math.sin(time / 53);
		strength += 0.25 * level.getRainLevel(partialTicks);
		strength += 0.3 * level.getThunderLevel(partialTicks);
		strength += Mth.clamp((pos.getY() - 63) / 256.0, 0, 0.2);
		return (float) Mth.clamp(strength, 0, 1);
	}

}
