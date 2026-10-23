package com.piissedpanda.createaviation.foundation.data.recipe;

import java.util.concurrent.CompletableFuture;

import com.piissedpanda.createaviation.CABlocks;
import com.piissedpanda.createaviation.CreateAviation;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.DeployingRecipeGen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;

@SuppressWarnings("unused")
public class CADeployingRecipeGen extends DeployingRecipeGen {

	GeneratedRecipe

	AIRFIELD_SIGN = create("airfield_sign", b -> b.require(AllItems.IRON_SHEET)
			.require(ItemTags.SIGNS)
			.output(CABlocks.AIRFIELD_SIGN.get())),

			EDGE_LIGHT = create("edge_light", b -> b.require(AllItems.IRON_SHEET)
					.require(AllItems.ELECTRON_TUBE)
					.output(CABlocks.EDGE_LIGHT.get(), 2));

	public CADeployingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries, CreateAviation.MODID);
	}

}
