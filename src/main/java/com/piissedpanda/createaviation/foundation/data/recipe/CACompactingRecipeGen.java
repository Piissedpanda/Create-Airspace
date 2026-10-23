package com.piissedpanda.createaviation.foundation.data.recipe;

import java.util.concurrent.CompletableFuture;

import com.piissedpanda.createaviation.CABlocks;
import com.piissedpanda.createaviation.CreateAviation;
import com.simibubi.create.api.data.recipe.CompactingRecipeGen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

@SuppressWarnings("unused")
public class CACompactingRecipeGen extends CompactingRecipeGen {

	GeneratedRecipe

	// Dried kelp works as rubber, like in Create's belts
	TRAFFIC_CONE = create("traffic_cone", b -> b.require(Items.DRIED_KELP)
			.require(Items.DRIED_KELP)
			.require(Tags.Items.DYES_ORANGE)
			.require(Tags.Items.DYES_WHITE)
			.output(CABlocks.TRAFFIC_CONE.get(), 2));

	public CACompactingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries, CreateAviation.MODID);
	}

}
