package com.piissedpanda.createairspace;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.FluidEntry;

import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

public class CAFluids {
	private static final CreateRegistrate REGISTRATE = CreateAirspace.REGISTRATE;

	// Runway paint, mixed from dye, clay and water
	public static final FluidEntry<BaseFlowingFluid.Flowing>

	WHITE_PAINT = paint("white_paint", MapColor.SNOW), YELLOW_PAINT = paint("yellow_paint", MapColor.COLOR_YELLOW);

	private static FluidEntry<BaseFlowingFluid.Flowing> paint(String name, MapColor color) {
		return REGISTRATE.standardFluid(name)
				.properties(b -> b.viscosity(1500)
						.density(1200))
				.fluidProperties(p -> p.levelDecreasePerBlock(2)
						.tickRate(20)
						.slopeFindDistance(3)
						.explosionResistance(100f))
				.source(BaseFlowingFluid.Source::new)
				.block()
				.properties(p -> p.mapColor(color))
				.build()
				.bucket()
				.tag(Tags.Items.BUCKETS)
				.build()
				.register();
	}

	public static void register() {
	}

}
