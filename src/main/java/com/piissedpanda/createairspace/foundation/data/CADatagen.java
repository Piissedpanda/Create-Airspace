package com.piissedpanda.createairspace.foundation.data;

import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.piissedpanda.createairspace.CreateAirspace;
import com.piissedpanda.createairspace.foundation.data.recipe.CADeployingRecipeGen;
import com.piissedpanda.createairspace.foundation.data.recipe.CAItemApplicationRecipeGen;
import com.piissedpanda.createairspace.foundation.data.recipe.CAMechanicalCraftingRecipeGen;
import com.piissedpanda.createairspace.foundation.data.recipe.CAMixingRecipeGen;
import com.piissedpanda.createairspace.foundation.data.recipe.CASequencedAssemblyRecipeGen;
import com.piissedpanda.createairspace.foundation.ponder.CAPonderPlugin;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.tterrag.registrate.providers.ProviderType;

import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class CADatagen {

	public static void gatherDataHighPriority(GatherDataEvent event) {
		if (event.getMods().contains(CreateAirspace.MODID))
			addExtraRegistrateData();
	}

	public static void gatherData(GatherDataEvent event) {
		if (!event.getMods().contains(CreateAirspace.MODID))
			return;

		DataGenerator generator = event.getGenerator();
		PackOutput output = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		generator.addProvider(event.includeServer(), new CAItemApplicationRecipeGen(output, lookupProvider));
		generator.addProvider(event.includeServer(), new CADeployingRecipeGen(output, lookupProvider));
		generator.addProvider(event.includeServer(), new CAMechanicalCraftingRecipeGen(output, lookupProvider));
		generator.addProvider(event.includeServer(), new CASequencedAssemblyRecipeGen(output, lookupProvider));
		generator.addProvider(event.includeServer(), new CAMixingRecipeGen(output, lookupProvider));
	}

	private static void addExtraRegistrateData() {
		CreateAirspace.REGISTRATE.addDataGenerator(ProviderType.LANG, provider -> {
			BiConsumer<String, String> langConsumer = provider::add;

			provideDefaultLang("default", langConsumer);
			providePonderLang(langConsumer);
		});
	}

	private static void providePonderLang(BiConsumer<String, String> consumer) {
		// FMLClientSetupEvent does not run during datagen
		PonderIndex.addPlugin(new CAPonderPlugin());

		PonderIndex.getLangAccess().provideLang(CreateAirspace.MODID, consumer);
	}

	private static void provideDefaultLang(String fileName, BiConsumer<String, String> consumer) {
		String path = "assets/" + CreateAirspace.MODID + "/lang/default/" + fileName + ".json";
		JsonElement jsonElement = FilesHelper.loadJsonResource(path);
		if (jsonElement == null) {
			throw new IllegalStateException(String.format("Could not find default lang file: %s", path));
		}
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue().getAsString();
			consumer.accept(key, value);
		}
	}

}
