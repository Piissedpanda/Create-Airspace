package com.piissedpanda.createairspace.foundation.data.recipe;

import java.util.concurrent.CompletableFuture;

import com.piissedpanda.createairspace.CABlocks;
import com.piissedpanda.createairspace.CreateAirspace;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.DeployingRecipeGen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

@SuppressWarnings("unused")
public class CADeployingRecipeGen extends DeployingRecipeGen {

	GeneratedRecipe

	EDGE_LIGHT = create("edge_light", b -> b.require(AllItems.IRON_SHEET)
			.require(AllItems.ELECTRON_TUBE)
			.output(CABlocks.EDGE_LIGHT.get(), 2));

	public CADeployingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries, CreateAirspace.MODID);
	}

}
