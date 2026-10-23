package com.piissedpanda.createaviation.foundation.data.recipe;

import java.util.concurrent.CompletableFuture;

import com.piissedpanda.createaviation.CABlocks;
import com.piissedpanda.createaviation.CreateAviation;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.ItemApplicationRecipeGen;
import com.tterrag.registrate.util.entry.BlockEntry;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

@SuppressWarnings("unused")
public class CAItemApplicationRecipeGen extends ItemApplicationRecipeGen {

	GeneratedRecipe

	BARRIER_POST = barrierPost(CABlocks.BARRIER_POST, AllBlocks.INDUSTRIAL_IRON_BLOCK),
			ANDESITE_BARRIER_POST = barrierPost(CABlocks.ANDESITE_BARRIER_POST, AllBlocks.ANDESITE_CASING),
			BRASS_BARRIER_POST = barrierPost(CABlocks.BRASS_BARRIER_POST, AllBlocks.BRASS_CASING),
			COPPER_BARRIER_POST = barrierPost(CABlocks.COPPER_BARRIER_POST, AllBlocks.COPPER_CASING),
			TRAIN_BARRIER_POST = barrierPost(CABlocks.TRAIN_BARRIER_POST, AllBlocks.RAILWAY_CASING),

			// Orange wool onto a placed lightning rod
			WINDSOCK = create("windsock", b -> b.require(Items.LIGHTNING_ROD)
					.require(Items.ORANGE_WOOL)
					.output(CABlocks.WINDSOCK.get()));

	// Apply a belt to the casing block, by hand or with a Deployer
	private GeneratedRecipe barrierPost(BlockEntry<?> post, BlockEntry<?> casing) {
		String name = post.getId()
				.getPath();
		return create(name, b -> b.require((ItemLike) casing.get())
				.require(AllItems.BELT_CONNECTOR)
				.output(post.get()));
	}

	public CAItemApplicationRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries, CreateAviation.MODID);

		for (DyeColor colour : DyeColor.values())
			terminalSeat(colour);
	}

	// Apply an iron sheet to a Create seat, the colour stays the same
	private GeneratedRecipe terminalSeat(DyeColor colour) {
		BlockEntry<?> seat = CABlocks.TERMINAL_SEATS.get(colour);
		String name = seat.getId()
				.getPath();
		return create(name, b -> b.require((ItemLike) AllBlocks.SEATS.get(colour)
				.get())
				.require(AllItems.IRON_SHEET)
				.output(seat.get()));
	}

}
