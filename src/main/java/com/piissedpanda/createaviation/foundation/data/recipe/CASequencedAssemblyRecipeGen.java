package com.piissedpanda.createaviation.foundation.data.recipe;

import java.util.concurrent.CompletableFuture;

import com.piissedpanda.createaviation.CABlocks;
import com.piissedpanda.createaviation.CAItems;
import com.piissedpanda.createaviation.CreateAviation;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.SequencedAssemblyRecipeGen;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

@SuppressWarnings("unused")
public class CASequencedAssemblyRecipeGen extends SequencedAssemblyRecipeGen {

	GeneratedRecipe

	// Glowstone makes the text light up, so the sign needs no power
	AIRFIELD_SIGN = create("airfield_sign", b -> b.require(AllItems.IRON_SHEET)
			.transitionTo(CAItems.INCOMPLETE_AIRFIELD_SIGN.get())
			.addOutput(CABlocks.AIRFIELD_SIGN.get(), 1)
			.loops(1)
			.addStep(DeployerApplicationRecipe::new, rb -> rb.require(ItemTags.SIGNS))
			.addStep(DeployerApplicationRecipe::new, rb -> rb.require(Items.GLOWSTONE_DUST))
			.addStep(DeployerApplicationRecipe::new, rb -> rb.require(Tags.Items.DYES_YELLOW))
			.addStep(DeployerApplicationRecipe::new, rb -> rb.require(Tags.Items.DYES_BLACK))
			.addStep(PressingRecipe::new, rb -> rb));

	public CASequencedAssemblyRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries, CreateAviation.MODID);
	}

}
