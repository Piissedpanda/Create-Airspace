package com.piissedpanda.createairspace.foundation.data.recipe;

import java.util.concurrent.CompletableFuture;

import com.piissedpanda.createairspace.CAFluids;
import com.piissedpanda.createairspace.CreateAirspace;
import com.simibubi.create.api.data.recipe.MixingRecipeGen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;

@SuppressWarnings("unused")
public class CAMixingRecipeGen extends MixingRecipeGen {

	// Clay binds the paint, which also keeps it apart from plain dye fluids of
	// other mods
	GeneratedRecipe

	WHITE_PAINT = create("white_paint", b -> b.require(Tags.Items.DYES_WHITE)
			.require(Items.CLAY_BALL)
			.require(Fluids.WATER, 250)
			.output(CAFluids.WHITE_PAINT.get(), 250)),

			YELLOW_PAINT = create("yellow_paint", b -> b.require(Tags.Items.DYES_YELLOW)
					.require(Items.CLAY_BALL)
					.require(Fluids.WATER, 250)
					.output(CAFluids.YELLOW_PAINT.get(), 250));

	public CAMixingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries, CreateAirspace.MODID);
	}

}
