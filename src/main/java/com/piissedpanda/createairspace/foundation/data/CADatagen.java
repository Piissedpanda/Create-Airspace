package com.piissedpanda.createairspace.foundation.data;

import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.piissedpanda.createairspace.CATags;
import com.piissedpanda.createairspace.CreateAirspace;
import com.piissedpanda.createairspace.foundation.data.recipe.CADeployingRecipeGen;
import com.piissedpanda.createairspace.foundation.data.recipe.CAItemApplicationRecipeGen;
import com.piissedpanda.createairspace.foundation.data.recipe.CAMechanicalCraftingRecipeGen;
import com.piissedpanda.createairspace.foundation.data.recipe.CAMixingRecipeGen;
import com.piissedpanda.createairspace.foundation.data.recipe.CASequencedAssemblyRecipeGen;
import com.piissedpanda.createairspace.foundation.ponder.CAPonderPlugin;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateItemTagsProvider;

import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
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
		CreateAirspace.REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, CADatagen::provideItemTags);
	}

	// Datapacks can add to the tag, everything else is left to them
	private static void provideItemTags(RegistrateItemTagsProvider provider) {
		provider.addTag(CATags.SECURITY_ALARM)
				.addTag(ItemTags.SWORDS)
				.addTag(ItemTags.AXES)
				.addTag(ItemTags.PICKAXES)
				.addTag(ItemTags.SHOVELS)
				.addTag(ItemTags.HOES)
				.addTag(Tags.Items.MELEE_WEAPON_TOOLS)
				.addTag(Tags.Items.RANGED_WEAPON_TOOLS)
				.addTag(Tags.Items.INGOTS)
				.addTag(Tags.Items.NUGGETS)
				.add(Items.BOW, Items.CROSSBOW, Items.TRIDENT, Items.MACE, Items.SHEARS, Items.FLINT_AND_STEEL,
						Items.TNT, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS,
						Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS,
						Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_LEGGINGS,
						Items.CHAINMAIL_BOOTS, Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE,
						Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS)
				.add(AllItems.POTATO_CANNON.get(), AllItems.WRENCH.get())
				.addOptionalTag(ResourceLocation.fromNamespaceAndPath("c", "plates"));
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
