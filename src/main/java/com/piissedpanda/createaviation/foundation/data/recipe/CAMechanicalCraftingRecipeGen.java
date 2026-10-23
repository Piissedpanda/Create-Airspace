package com.piissedpanda.createaviation.foundation.data.recipe;

import java.util.concurrent.CompletableFuture;

import com.piissedpanda.createaviation.CABlocks;
import com.piissedpanda.createaviation.CreateAviation;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.MechanicalCraftingRecipeGen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;

@SuppressWarnings("unused")
public class CAMechanicalCraftingRecipeGen extends MechanicalCraftingRecipeGen {

	GeneratedRecipe

	AIRPORT_BEACON = create(CABlocks.AIRPORT_BEACON::get).recipe(b -> b.key('G', Items.LIME_STAINED_GLASS)
			.key('T', AllItems.ELECTRON_TUBE)
			.key('W', Items.WHITE_STAINED_GLASS)
			.key('C', AllBlocks.BRASS_CASING)
			.key('S', AllBlocks.SHAFT)
			.patternLine("GTW")
			.patternLine(" C ")
			.patternLine(" S "));

	public CAMechanicalCraftingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries, CreateAviation.MODID);
	}

}
