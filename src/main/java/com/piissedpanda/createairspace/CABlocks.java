package com.piissedpanda.createairspace;

import static com.simibubi.create.api.behaviour.display.DisplayTarget.displayTarget;
import static com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour.interactionBehaviour;
import static com.simibubi.create.api.behaviour.movement.MovementBehaviour.movementBehaviour;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOnly;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

import java.util.List;

import com.piissedpanda.createairspace.block.AirfieldSign.AirfieldSignBlock;
import com.piissedpanda.createairspace.block.AirportBeacon.AirportBeaconBlock;
import com.piissedpanda.createairspace.block.BarrierPost.BarrierPostBlock;
import com.piissedpanda.createairspace.block.EdgeLight.EdgeLightBlock;
import com.piissedpanda.createairspace.block.BaggageCart.BaggageCartBlock;
import com.piissedpanda.createairspace.block.GroundCrew.WheelChockBlock;
import com.piissedpanda.createairspace.block.Luggage.SuitcasePackagerBlock;
import com.piissedpanda.createairspace.block.RunwayMarking.RunwayMarkingBlock;
import com.piissedpanda.createairspace.block.RunwayMarking.RunwayNumberBlock;
import com.piissedpanda.createairspace.block.TerminalSeat.TerminalSeatBlock;
import com.piissedpanda.createairspace.block.TrafficCone.TrafficConeBlock;
import com.piissedpanda.createairspace.block.Windsock.WindsockBlock;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags.AllBlockTags;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.content.contraptions.actors.seat.SeatInteractionBehaviour;
import com.simibubi.create.content.contraptions.actors.seat.SeatMovementBehaviour;
import com.simibubi.create.foundation.block.DyedBlockList;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.BuilderTransformers;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.BlockEntry;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Direction;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
import net.neoforged.neoforge.common.Tags;

public class CABlocks {
	private static final CreateRegistrate REGISTRATE = CreateAirspace.REGISTRATE;

	static {
		REGISTRATE.setCreativeTab(CACreativeModeTabs.BASE_TAB);
	}

	public static final BlockEntry<BarrierPostBlock>

	BARRIER_POST = barrierPost("barrier_post", "create:block/industrial_iron_block", MapColor.PODZOL,
			AllBlocks.INDUSTRIAL_IRON_BLOCK),
			ANDESITE_BARRIER_POST = barrierPost("andesite_barrier_post", "create:block/andesite_casing",
					MapColor.STONE, AllBlocks.ANDESITE_CASING),
			BRASS_BARRIER_POST = barrierPost("brass_barrier_post", "create:block/brass_casing",
					MapColor.TERRACOTTA_BROWN, AllBlocks.BRASS_CASING),
			COPPER_BARRIER_POST = barrierPost("copper_barrier_post", "create:block/copper_casing",
					MapColor.COLOR_LIGHT_GRAY, AllBlocks.COPPER_CASING),
			TRAIN_BARRIER_POST = barrierPost("train_barrier_post", "create:block/railway_casing",
					MapColor.TERRACOTTA_CYAN, AllBlocks.RAILWAY_CASING);

	public static final List<BlockEntry<BarrierPostBlock>> BARRIER_POSTS = List.of(BARRIER_POST,
			ANDESITE_BARRIER_POST, BRASS_BARRIER_POST, COPPER_BARRIER_POST, TRAIN_BARRIER_POST);

	public static final BlockEntry<TrafficConeBlock> TRAFFIC_CONE = REGISTRATE
			.block("traffic_cone", TrafficConeBlock::new)
			.properties(p -> p.noOcclusion()
					.mapColor(MapColor.COLOR_ORANGE)
					.strength(0.5f)
					.sound(SoundType.BAMBOO_WOOD))
			.blockstate((c, p) -> p.horizontalBlock(c.get(), state -> {
				int cones = state.getValue(TrafficConeBlock.CONES);
				return p.models()
						.getExistingFile(p.modLoc("block/traffic_cone/" + (cones == 1 ? "block" : "stack_" + cones)));
			}))
			// A stack drops all of its cones
			.loot((lt, b) -> lt.add(b, LootTable.lootTable()
					.withPool(LootPool.lootPool()
							.setRolls(ConstantValue.exactly(1))
							.add(lt.applyExplosionDecay(b, LootItem.lootTableItem(b)
									.apply(List.of(2, 3, 4), cones -> SetItemCountFunction
											.setCount(ConstantValue.exactly(cones))
											.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(b)
													.setProperties(StatePropertiesPredicate.Builder.properties()
															.hasProperty(TrafficConeBlock.CONES, cones)))))))))
			// Dried kelp works as rubber, like in Create's belts
			.recipe((c, p) -> ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, c.get(), 2)
					.define('O', Tags.Items.DYES_ORANGE)
					.define('W', Tags.Items.DYES_WHITE)
					.define('K', Items.DRIED_KELP)
					.pattern(" O ")
					.pattern("KWK")
					.unlockedBy("has_dried_kelp", RegistrateRecipeProvider.has(Items.DRIED_KELP))
					.save(p, CreateAirspace.asResource("crafting/" + c.getName())))
			.item()
			.model((c, p) -> p.withExistingParent(c.getName(), p.modLoc("block/traffic_cone/block")))
			.build()
			.register();

	public static final DyedBlockList<TerminalSeatBlock> TERMINAL_SEATS = new DyedBlockList<>(colour -> {
		String colourName = colour.getSerializedName();
		return REGISTRATE.block(colourName + "_terminal_seat", p -> new TerminalSeatBlock(p, colour))
				.initialProperties(SharedProperties::wooden)
				.properties(p -> p.noOcclusion()
						.mapColor(colour))
				.transform(axeOnly())
				.onRegister(movementBehaviour(new SeatMovementBehaviour()))
				.onRegister(interactionBehaviour(new SeatInteractionBehaviour()))
				.tag(AllBlockTags.SEATS.tag)
				.blockstate((c, p) -> p.getVariantBuilder(c.get())
						.forAllStatesExcept(state -> {
							String shape = state.getValue(TerminalSeatBlock.SHAPE)
									.getSerializedName();
							return ConfiguredModel.builder()
									.modelFile(p.models()
											.withExistingParent("block/" + c.getName() + "/" + shape,
													p.modLoc("block/terminal_seat/" + shape))
											.texture("cushion_top", "create:block/seat/top_" + colourName)
											.texture("cushion_side", "create:block/seat/side_" + colourName)
											.texture("particle", "create:block/seat/top_" + colourName))
									.rotationY(((int) state.getValue(TerminalSeatBlock.FACING)
											.toYRot() + 180) % 360)
									.build();
						}, TerminalSeatBlock.WATERLOGGED))
				// Recolour any terminal seat, like Create's seats
				.recipe((c, p) -> ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, c.get())
						.requires(colour.getTag())
						.requires(CATags.TERMINAL_SEATS)
						.unlockedBy("has_terminal_seat", RegistrateRecipeProvider.has(CATags.TERMINAL_SEATS))
						.save(p, CreateAirspace.asResource("crafting/" + c.getName() + "_from_other_seat")))
				.item()
				.tag(CATags.TERMINAL_SEATS)
				.model((c, p) -> p.withExistingParent(c.getName(), p.modLoc("block/" + c.getName() + "/single")))
				.build()
				.register();
	});

	public static final BlockEntry<AirfieldSignBlock> AIRFIELD_SIGN = REGISTRATE
			.block("airfield_sign", AirfieldSignBlock::new)
			.initialProperties(SharedProperties::softMetal)
			.properties(p -> p.noOcclusion()
					.mapColor(MapColor.COLOR_YELLOW))
			.transform(pickaxeOnly())
			.transform(displayTarget(CADisplayTargets.AIRFIELD_SIGN))
			.blockstate((c, p) -> p.getVariantBuilder(c.get())
					.forAllStates(state -> {
						AirfieldSignBlock.Style style = state.getValue(AirfieldSignBlock.STYLE);
						return ConfiguredModel.builder()
								.modelFile(p.models()
										.withExistingParent("block/airfield_sign/" + style.getSerializedName(),
												p.modLoc("block/airfield_sign/base"))
										.texture("face", style.faceTexture))
								.rotationY(((int) state.getValue(AirfieldSignBlock.FACING)
										.toYRot() + 180) % 360)
								.build();
					}))
			.item()
			.model((c, p) -> p.withExistingParent(c.getName(), p.modLoc("block/airfield_sign/location")))
			.build()
			.register();

	public static final BlockEntry<EdgeLightBlock> EDGE_LIGHT = REGISTRATE.block("edge_light", EdgeLightBlock::new)
			.initialProperties(SharedProperties::softMetal)
			.properties(p -> p.noOcclusion()
					.mapColor(MapColor.METAL)
					.lightLevel(s -> s.getValue(EdgeLightBlock.POWERED) ? 12 : 0))
			.transform(pickaxeOnly())
			.blockstate((c, p) -> p.getVariantBuilder(c.get())
					.forAllStatesExcept(state -> {
						String color = state.getValue(EdgeLightBlock.COLOR)
								.getSerializedName();
						if (!state.getValue(EdgeLightBlock.POWERED))
							return ConfiguredModel.builder()
									.modelFile(p.models()
											.withExistingParent("block/edge_light/" + color + "_off",
													p.modLoc("block/edge_light/base"))
											.texture("lens", "minecraft:block/" + color + "_terracotta"))
									.build();
						return ConfiguredModel.builder()
								.modelFile(p.models()
										.withExistingParent("block/edge_light/" + color + "_on",
												p.modLoc("block/edge_light/lit"))
										.texture("lens", "minecraft:block/" + color + "_concrete")
										.texture("glow", "createairspace:block/edge_light/glow_" + color))
								.build();
					}, EdgeLightBlock.WATERLOGGED))
			.item()
			.model((c, p) -> p.withExistingParent(c.getName(), p.modLoc("block/edge_light/white_off")))
			.build()
			.register();

	public static final BlockEntry<WindsockBlock> WINDSOCK = REGISTRATE.block("windsock", WindsockBlock::new)
			.initialProperties(SharedProperties::copperMetal)
			.properties(p -> p.noOcclusion()
					.mapColor(MapColor.COLOR_ORANGE))
			.transform(pickaxeOnly())
			.blockstate((c, p) -> p.simpleBlock(c.get(), AssetLookup.partialBaseModel(c, p)))
			.recipe((c, p) -> ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, c.get())
					.define('R', Items.LIGHTNING_ROD)
					.define('W', Items.ORANGE_WOOL)
					.pattern("RW")
					.pattern("R ")
					.unlockedBy("has_lightning_rod", RegistrateRecipeProvider.has(Items.LIGHTNING_ROD))
					.save(p, CreateAirspace.asResource("crafting/" + c.getName())))
			.item()
			.transform(customItemModel())
			.register();

	public static final BlockEntry<AirportBeaconBlock> AIRPORT_BEACON = REGISTRATE
			.block("airport_beacon", AirportBeaconBlock::new)
			.initialProperties(SharedProperties::softMetal)
			.properties(p -> p.noOcclusion()
					.mapColor(MapColor.TERRACOTTA_BROWN)
					.lightLevel(s -> s.getValue(AirportBeaconBlock.POWER)))
			.transform(pickaxeOnly())
			.onRegister(b -> BlockStressValues.IMPACTS.register(b, () -> 2.0))
			.blockstate((c, p) -> p.simpleBlock(c.get(), AssetLookup.partialBaseModel(c, p)))
			.item()
			.transform(customItemModel())
			.register();

	// Painted with the paint pail, they have no item and give nothing back
	public static final BlockEntry<RunwayMarkingBlock>

	WHITE_RUNWAY_MARKING = runwayMarking("white", MapColor.SNOW),
			YELLOW_RUNWAY_MARKING = runwayMarking("yellow", MapColor.COLOR_YELLOW);

	public static final BlockEntry<RunwayNumberBlock>

	WHITE_RUNWAY_NUMBER = runwayNumber("white", MapColor.SNOW),
			YELLOW_RUNWAY_NUMBER = runwayNumber("yellow", MapColor.COLOR_YELLOW);

	public static final BlockEntry<SuitcasePackagerBlock> SUITCASE_PACKAGER = REGISTRATE
			.block("suitcase_packager", SuitcasePackagerBlock::new)
			.transform(BuilderTransformers.packager())
			.register();

	public static final BlockEntry<BaggageCartBlock> BAGGAGE_CART = REGISTRATE
			.block("baggage_cart", BaggageCartBlock::new)
			.initialProperties(SharedProperties::softMetal)
			.properties(p -> p.noOcclusion()
					.mapColor(MapColor.METAL))
			.transform(pickaxeOnly())
			.blockstate((c, p) -> {
				MultiPartBlockStateBuilder builder = p.getMultipartBuilder(c.get());
				builder.part()
						.modelFile(p.models()
								.getExistingFile(p.modLoc("block/baggage_cart/deck")))
						.addModel()
						.end();
				// Net on every side without a cart of the same group
				for (Direction side : Direction.Plane.HORIZONTAL) {
					int rotation = ((int) side.toYRot() + 180) % 360;
					builder.part()
							.modelFile(p.models()
									.getExistingFile(p.modLoc("block/baggage_cart/side")))
							.rotationY(rotation)
							.addModel()
							.condition(BaggageCartBlock.CONNECTED.get(side), false)
							.end();
				}
			})
			.recipe((c, p) -> ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, c.get())
					.define('B', Items.IRON_BARS)
					.define('S', AllItems.IRON_SHEET)
					.define('N', Tags.Items.NUGGETS_IRON)
					.pattern("BBB")
					.pattern("SSS")
					.pattern("N N")
					.unlockedBy("has_iron_sheet", RegistrateRecipeProvider.has(AllItems.IRON_SHEET))
					.save(p, CreateAirspace.asResource("crafting/" + c.getName())))
			.item()
			.model((c, p) -> p.withExistingParent(c.getName(), p.modLoc("block/baggage_cart/item")))
			.build()
			.register();

	public static final BlockEntry<WheelChockBlock> WHEEL_CHOCK = REGISTRATE
			.block("wheel_chock", WheelChockBlock::new)
			.properties(p -> p.noOcclusion()
					.mapColor(MapColor.COLOR_YELLOW)
					.strength(0.5f)
					.sound(SoundType.WOOL))
			.blockstate((c, p) -> p.horizontalBlock(c.get(), AssetLookup.partialBaseModel(c, p)))
			.recipe((c, p) -> ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, c.get())
					.requires(Items.DRIED_KELP, 2)
					.requires(Tags.Items.STRINGS)
					.requires(Tags.Items.DYES_YELLOW)
					.unlockedBy("has_dried_kelp", RegistrateRecipeProvider.has(Items.DRIED_KELP))
					.save(p, CreateAirspace.asResource("crafting/" + c.getName())))
			.item()
			.model((c, p) -> p.withExistingParent(c.getName(), p.modLoc("block/wheel_chock/block")))
			.build()
			.register();

	public static boolean isBarrierPost(BlockState state) {
		return state.getBlock() instanceof BarrierPostBlock;
	}

	// All posts share block/barrier_post/*, only the "material" texture changes
	private static BlockEntry<BarrierPostBlock> barrierPost(String name, String material, MapColor color,
			ItemLike casing) {
		return REGISTRATE.block(name, BarrierPostBlock::new)
				.initialProperties(SharedProperties::stone)
				.properties(p -> p.noOcclusion()
						.mapColor(color))
				.transform(axeOrPickaxe())
				.blockstate((c, p) -> p.simpleBlock(c.getEntry(), p.models()
						.withExistingParent("block/" + c.getName() + "/block", p.modLoc("block/barrier_post/base"))
						.texture("material", material)
						.texture("particle", material)))
				.recipe((c, p) -> ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, c.get(), 4)
						.define('B', AllItems.BELT_CONNECTOR)
						.define('C', casing)
						.pattern("B")
						.pattern("C")
						.pattern("C")
						.unlockedBy("has_casing", RegistrateRecipeProvider.has(casing))
						.save(p, CreateAirspace.asResource("crafting/" + c.getName())))
				.item()
				.model((c, p) -> p.withExistingParent(c.getName(), p.modLoc("block/barrier_post/item_base"))
						.texture("material", material)
						.texture("particle", material))
				.build()
				.register();
	}

	// Flat paint layer, the pattern comes from the blockstate
	private static BlockEntry<RunwayMarkingBlock> runwayMarking(String colour, MapColor mapColor) {
		return REGISTRATE.block(colour + "_runway_marking", RunwayMarkingBlock::new)
				.properties(p -> paint(p, mapColor))
				.blockstate((c, p) -> p.getVariantBuilder(c.get())
						.forAllStates(state -> {
							String name = state.getValue(RunwayMarkingBlock.PATTERN)
									.getSerializedName() + "_" + colour;
							return ConfiguredModel.builder()
									.modelFile(p.models()
											.withExistingParent("block/runway_marking/" + name,
													p.modLoc("block/runway_marking/base"))
											.texture("marking", p.modLoc("block/runway_marking/" + name)))
									.rotationY(((int) state.getValue(RunwayMarkingBlock.FACING)
											.toYRot() + 180) % 360)
									.build();
						}))
				.loot((lt, b) -> lt.add(b, LootTable.lootTable()))
				.register();
	}

	private static BlockEntry<RunwayNumberBlock> runwayNumber(String colour, MapColor mapColor) {
		return REGISTRATE.block(colour + "_runway_number", RunwayNumberBlock::new)
				.properties(p -> paint(p, mapColor))
				.blockstate((c, p) -> p.getVariantBuilder(c.get())
						.forAllStates(state -> {
							String glyph = state.getValue(RunwayNumberBlock.GLYPH)
									.getSerializedName() + "_" + colour;
							int part = state.getValue(RunwayNumberBlock.PART);
							return ConfiguredModel.builder()
									.modelFile(p.models()
											.withExistingParent("block/runway_number/" + glyph + "_" + part,
													p.modLoc("block/runway_number/part_" + part))
											.texture("glyph", p.modLoc("block/runway_number/" + glyph)))
									.rotationY(((int) state.getValue(RunwayNumberBlock.FACING)
											.toYRot() + 180) % 360)
									.build();
						}))
				.loot((lt, b) -> lt.add(b, LootTable.lootTable()))
				.register();
	}

	private static BlockBehaviour.Properties paint(BlockBehaviour.Properties properties, MapColor mapColor) {
		return properties.noCollission()
				.mapColor(mapColor)
				.strength(0.1f)
				.sound(SoundType.STONE)
				.pushReaction(PushReaction.DESTROY);
	}

	public static void register() {
	}
}
