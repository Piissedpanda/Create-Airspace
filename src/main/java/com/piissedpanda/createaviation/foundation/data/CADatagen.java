package com.piissedpanda.createaviation.foundation.data;

import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.piissedpanda.createaviation.CreateAviation;
import com.piissedpanda.createaviation.foundation.data.recipe.CACompactingRecipeGen;
import com.piissedpanda.createaviation.foundation.data.recipe.CAItemApplicationRecipeGen;
import com.piissedpanda.createaviation.foundation.ponder.CAPonderPlugin;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.tterrag.registrate.providers.ProviderType;

import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class CADatagen {

	public static void gatherDataHighPriority(GatherDataEvent event) {
		if (event.getMods().contains(CreateAviation.MODID))
			addExtraRegistrateData();
	}

	public static void gatherData(GatherDataEvent event) {
		if (!event.getMods().contains(CreateAviation.MODID))
			return;

		DataGenerator generator = event.getGenerator();
		PackOutput output = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		generator.addProvider(event.includeServer(), new CAItemApplicationRecipeGen(output, lookupProvider));
		generator.addProvider(event.includeServer(), new CACompactingRecipeGen(output, lookupProvider));
	}

	private static void addExtraRegistrateData() {
		CreateAviation.REGISTRATE.addDataGenerator(ProviderType.LANG, provider -> {
			BiConsumer<String, String> langConsumer = provider::add;

			provideDefaultLang("default", langConsumer);
			providePonderLang(langConsumer);
		});
	}

	private static void providePonderLang(BiConsumer<String, String> consumer) {
		// FMLClientSetupEvent does not run during datagen
		PonderIndex.addPlugin(new CAPonderPlugin());

		PonderIndex.getLangAccess().provideLang(CreateAviation.MODID, consumer);
	}

	private static void provideDefaultLang(String fileName, BiConsumer<String, String> consumer) {
		String path = "assets/createaviation/lang/default/" + fileName + ".json";
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
