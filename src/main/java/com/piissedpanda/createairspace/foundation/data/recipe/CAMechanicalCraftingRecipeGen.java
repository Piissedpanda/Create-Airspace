package com.piissedpanda.createairspace.foundation.data.recipe;

import java.util.concurrent.CompletableFuture;

import com.piissedpanda.createairspace.CABlocks;
import com.piissedpanda.createairspace.CreateAirspace;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.MechanicalCraftingRecipeGen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;

@SuppressWarnings("unused")
public class CAMechanicalCraftingRecipeGen extends MechanicalCraftingRecipeGen {

	GeneratedRecipe

	// One glass per lens colour of the beacon patterns
	AIRPORT_BEACON = create(CABlocks.AIRPORT_BEACON::get).recipe(b -> b.key('G', Items.LIME_STAINED_GLASS)
			.key('Y', Items.YELLOW_STAINED_GLASS)
			.key('W', Items.WHITE_STAINED_GLASS)
			.key('T', AllItems.ELECTRON_TUBE)
			.key('P', AllItems.PRECISION_MECHANISM)
			.key('C', AllBlocks.BRASS_CASING)
			.key('S', AllBlocks.SHAFT)
			.patternLine("GYW")
			.patternLine("TPT")
			.patternLine(" C ")
			.patternLine(" S "));

	public CAMechanicalCraftingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries, CreateAirspace.MODID);
	}

}
