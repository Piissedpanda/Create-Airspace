package com.piissedpanda.createairspace;

import java.util.EnumMap;
import java.util.Map;

import com.piissedpanda.createairspace.block.GroundCrew.EarDefendersItem;
import com.piissedpanda.createairspace.block.GroundCrew.MarshallingWandItem;
import com.piissedpanda.createairspace.block.Luggage.LuggageItem;
import com.piissedpanda.createairspace.block.Luggage.LuggageType;
import com.simibubi.create.AllTags.AllItemTags;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.ItemEntry;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

public class CAItems {
	private static final CreateRegistrate REGISTRATE = CreateAirspace.REGISTRATE;

	// Blank sign plate while it moves through the sequenced assembly
	public static final ItemEntry<SequencedAssemblyItem> INCOMPLETE_AIRFIELD_SIGN = REGISTRATE
			.item("incomplete_airfield_sign", SequencedAssemblyItem::new)
			.model((c, p) -> p.withExistingParent(c.getName(), p.modLoc("block/airfield_sign/base"))
					.texture("face", "create:block/industrial_iron_block"))
			.register();

	public static final ItemEntry<SequencedAssemblyItem> INCOMPLETE_SUITCASE_PACKAGER = REGISTRATE
			.item("incomplete_suitcase_packager", SequencedAssemblyItem::new)
			.model((c, p) -> p.withExistingParent(c.getName(), p.modLoc("block/suitcase_packager/item")))
			.register();

	// Every luggage type in every colour, only the suitcase packager makes them
	public static final Map<LuggageType, Map<DyeColor, ItemEntry<LuggageItem>>> LUGGAGE = new EnumMap<>(
			LuggageType.class);

	static {
		for (LuggageType type : LuggageType.values()) {
			Map<DyeColor, ItemEntry<LuggageItem>> colors = new EnumMap<>(DyeColor.class);
			for (DyeColor color : DyeColor.values())
				colors.put(color, REGISTRATE.item(type.itemName(color), p -> new LuggageItem(p, type, color))
						.properties(p -> p.stacksTo(1))
						.tag(AllItemTags.PACKAGES.tag, CATags.LUGGAGE)
						.model((c, p) -> p.withExistingParent(c.getName(), p.modLoc("item/luggage/" + type.id))
								.texture("body", type.bodyTexture(color))
								.texture("particle", type.bodyTexture(color)))
						.register());
			LUGGAGE.put(type, colors);
		}
	}

	public static final ItemEntry<MarshallingWandItem> MARSHALLING_WAND = REGISTRATE
			.item("marshalling_wand", MarshallingWandItem::new)
			.properties(p -> p.stacksTo(16))
			.model((c, p) -> p.withExistingParent(c.getName(), p.modLoc("item/ground_crew/" + c.getName())))
			.recipe((c, p) -> ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, c.get())
					.define('O', Items.ORANGE_STAINED_GLASS_PANE)
					.define('L', Tags.Items.DUSTS_GLOWSTONE)
					.define('S', Tags.Items.RODS_WOODEN)
					.pattern("O")
					.pattern("L")
					.pattern("S")
					.unlockedBy("has_glowstone_dust", RegistrateRecipeProvider.has(Tags.Items.DUSTS_GLOWSTONE))
					.save(p, CreateAirspace.asResource("crafting/" + c.getName())))
			.register();

	public static final ItemEntry<EarDefendersItem> EAR_DEFENDERS = REGISTRATE
			.item("ear_defenders", EarDefendersItem::new)
			.properties(p -> p.stacksTo(1))
			.tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("curios", "head")))
			.model((c, p) -> p.withExistingParent(c.getName(), p.modLoc("item/ground_crew/" + c.getName())))
			.recipe((c, p) -> ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, c.get())
					.define('I', Tags.Items.NUGGETS_IRON)
					.define('W', Items.YELLOW_WOOL)
					.pattern("III")
					.pattern("W W")
					.unlockedBy("has_yellow_wool", RegistrateRecipeProvider.has(Items.YELLOW_WOOL))
					.save(p, CreateAirspace.asResource("crafting/" + c.getName())))
			.register();

	public static void register() {
	}
}
