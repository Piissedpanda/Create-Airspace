package com.piissedpanda.createairspace.foundation.data.recipe;

import java.util.concurrent.CompletableFuture;

import com.piissedpanda.createairspace.CABlocks;
import com.piissedpanda.createairspace.CreateAirspace;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.ItemApplicationRecipeGen;
import com.tterrag.registrate.util.entry.BlockEntry;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.ItemLike;

public class CAItemApplicationRecipeGen extends ItemApplicationRecipeGen {

	public CAItemApplicationRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries, CreateAirspace.MODID);

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
