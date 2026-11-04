package com.piissedpanda.createairspace.foundation.data.recipe;

import java.util.concurrent.CompletableFuture;

import com.piissedpanda.createairspace.CAItems;
import com.piissedpanda.createairspace.CreateAirspace;
import com.simibubi.create.api.data.recipe.CuttingRecipeGen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.Tags;

@SuppressWarnings("unused")
public class CACuttingRecipeGen extends CuttingRecipeGen {

	// The saw cuts iron ingots into balls, like the stonecutter does
	GeneratedRecipe

	STEEL_BALL = create("steel_ball", b -> b.require(Tags.Items.INGOTS_IRON)
			.duration(50)
			.output(CAItems.STEEL_BALL.get(), 4));

	public CACuttingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries, CreateAirspace.MODID);
	}

}
