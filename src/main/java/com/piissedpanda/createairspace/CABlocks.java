package com.piissedpanda.createairspace;

import static com.simibubi.create.api.behaviour.display.DisplayTarget.displayTarget;
import static com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour.interactionBehaviour;
import static com.simibubi.create.api.behaviour.movement.MovementBehaviour.movementBehaviour;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOnly;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

import java.util.List;
import java.util.function.BiFunction;

import com.piissedpanda.createairspace.block.AirfieldSign.AirfieldSignBlock;
import com.piissedpanda.createairspace.block.AirfieldSign.GateSignBlock;
import com.piissedpanda.createairspace.block.AirportBeacon.AirportBeaconBlock;
import com.piissedpanda.createairspace.block.BarrierPost.BarrierPostBlock;
import com.piissedpanda.createairspace.block.EdgeLight.EdgeLightBlock;
import com.piissedpanda.createairspace.block.BaggageCart.BaggageCartBlock;
import com.piissedpanda.createairspace.block.CheckIn.CheckInDeskBlock;
import com.piissedpanda.createairspace.block.CheckIn.LuggageScaleBlock;
import com.piissedpanda.createairspace.block.GroundCrew.WheelChockBlock;
import com.piissedpanda.createairspace.block.Hangar.HangarDoorBlock;
import com.piissedpanda.createairspace.block.Hangar.HangarLightBlock;
import com.piissedpanda.createairspace.block.Luggage.SuitcasePackagerBlock;
import com.piissedpanda.createairspace.block.TerminalLight.FloorLightBlock;
import com.piissedpanda.createairspace.block.TerminalLight.LightPanelBlock;
import com.piissedpanda.createairspace.block.TerminalLight.LightStripBlock;
import com.piissedpanda.createairspace.block.TerminalLight.TerminalLightBlock;
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
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.BlockEntry;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Direction;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
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
import net.neoforged.neoforge.client.model.generators.ModelFile;
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
			// Lit signs, Opal Light colours this light under shaders
			.properties(p -> p.noOcclusion()
					.mapColor(MapColor.COLOR_YELLOW)
					.lightLevel(AirfieldSignBlock::lightLevel))
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

	public static final BlockEntry<GateSignBlock> GATE_SIGN = REGISTRATE.block("gate_sign", GateSignBlock::new)
			.initialProperties(SharedProperties::softMetal)
			.properties(p -> p.noOcclusion()
					.mapColor(MapColor.COLOR_BLACK)
					.lightLevel(AirfieldSignBlock::lightLevel))
			.transform(pickaxeOnly())
			.transform(displayTarget(CADisplayTargets.AIRFIELD_SIGN))
			.blockstate((c, p) -> p.getVariantBuilder(c.get())
					.forAllStates(state -> {
						AirfieldSignBlock.Style style = state.getValue(AirfieldSignBlock.STYLE);
						return ConfiguredModel.builder()
								.modelFile(p.models()
										.withExistingParent("block/gate_sign/" + style.getSerializedName(),
												p.modLoc("block/gate_sign/base"))
										.texture("face", style.faceTexture))
								.rotationY(((int) state.getValue(AirfieldSignBlock.FACING)
										.toYRot() + 180) % 360)
								.build();
					}))
			.recipe((c, p) -> ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, c.get())
					.requires(AIRFIELD_SIGN.get())
					.requires(Items.CHAIN)
					.unlockedBy("has_airfield_sign", RegistrateRecipeProvider.has(AIRFIELD_SIGN.get()))
					.save(p, CreateAirspace.asResource("crafting/" + c.getName())))
			.item()
			.model((c, p) -> p.withExistingParent(c.getName(), p.modLoc("block/gate_sign/location")))
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

	// Terminal lights, one block per colour like Create's seats, light face tinted
	public static final DyedBlockList<LightPanelBlock> LIGHT_PANELS = new DyedBlockList<>(
			colour -> terminalLight(colour, "light_panel", LightPanelBlock::new, CATags.LIGHT_PANELS)
					.blockstate((c, p) -> lightPanelState(c.get(), p))
					.item()
					.tag(CATags.LIGHT_PANELS)
					.model((c, p) -> p.withExistingParent(c.getName(), p.modLoc("block/terminal_light/panel_item")))
					.build()
					.register());

	public static final DyedBlockList<LightStripBlock> LIGHT_STRIPS = new DyedBlockList<>(
			colour -> terminalLight(colour, "light_strip", LightStripBlock::new, CATags.LIGHT_STRIPS)
					.blockstate((c, p) -> lightStripState(c.get(), p))
					.item()
					.tag(CATags.LIGHT_STRIPS)
					.model((c, p) -> p.withExistingParent(c.getName(), p.modLoc("block/terminal_light/strip_item")))
					.build()
					.register());

	public static final DyedBlockList<FloorLightBlock> FLOOR_LIGHTS = new DyedBlockList<>(
			colour -> terminalLight(colour, "floor_light", FloorLightBlock::new, CATags.FLOOR_LIGHTS)
					.blockstate((c, p) -> p.getVariantBuilder(c.get())
							.forAllStates(state -> ConfiguredModel.builder()
									.modelFile(lightModel(p, state.getValue(TerminalLightBlock.LEVEL) > 0 ? "floor_on"
											: "floor"))
									.build()))
					.item()
					.tag(CATags.FLOOR_LIGHTS)
					.model((c, p) -> p.withExistingParent(c.getName(), p.modLoc("block/terminal_light/floor_on")))
					.build()
					.register());

	public static final BlockEntry<HangarDoorBlock>

	HANGAR_DOOR_PANEL = hangarDoor("hangar_door_panel", "panel"),
			HANGAR_DOOR_WINDOW = hangarDoor("hangar_door_window", "window");

	public static final BlockEntry<HangarLightBlock> HANGAR_LIGHT = REGISTRATE.block("hangar_light",
			HangarLightBlock::new)
			.initialProperties(SharedProperties::softMetal)
			.properties(p -> p.noOcclusion()
					.mapColor(MapColor.METAL)
					.lightLevel(s -> s.getValue(TerminalLightBlock.LEVEL)))
			.transform(pickaxeOnly())
			.blockstate((c, p) -> p.getVariantBuilder(c.get())
					.forAllStates(state -> ConfiguredModel.builder()
							.modelFile(p.models()
									.getExistingFile(p.modLoc("block/hangar_light/"
											+ (state.getValue(TerminalLightBlock.LEVEL) > 0 ? "on" : "off"))))
							.build()))
			.recipe((c, p) -> ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, c.get(), 4)
					.define('S', AllItems.IRON_SHEET)
					.define('L', Items.GLOWSTONE)
					.define('G', Tags.Items.GLASS_PANES)
					.pattern("SSS")
					.pattern("SLS")
					.pattern(" G ")
					.unlockedBy("has_glowstone", RegistrateRecipeProvider.has(Items.GLOWSTONE))
					.save(p, CreateAirspace.asResource("crafting/" + c.getName())))
			.item()
			.model((c, p) -> p.withExistingParent(c.getName(), p.modLoc("block/hangar_light/on")))
			.build()
			.register();

	public static final BlockEntry<CheckInDeskBlock> CHECK_IN_DESK = REGISTRATE
			.block("check_in_desk", CheckInDeskBlock::new)
			.initialProperties(SharedProperties::stone)
			.properties(p -> p.noOcclusion()
					.mapColor(MapColor.SNOW))
			.transform(pickaxeOnly())
			.blockstate((c, p) -> {
				MultiPartBlockStateBuilder builder = p.getMultipartBuilder(c.get());
				for (Direction facing : Direction.Plane.HORIZONTAL) {
					int y = ((int) facing.toYRot() + 180) % 360;
					deskPart(builder, p, "body", y).condition(CheckInDeskBlock.FACING, facing)
							.end();
					deskPart(builder, p, "end_left", y).condition(CheckInDeskBlock.FACING, facing)
							.condition(CheckInDeskBlock.LEFT, false)
							.end();
					deskPart(builder, p, "end_right", y).condition(CheckInDeskBlock.FACING, facing)
							.condition(CheckInDeskBlock.RIGHT, false)
							.end();
				}
			})
			.recipe((c, p) -> ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, c.get(), 2)
					.define('S', Items.SMOOTH_STONE_SLAB)
					.define('W', Items.WHITE_CONCRETE)
					.pattern("SSS")
					.pattern("W W")
					.unlockedBy("has_white_concrete", RegistrateRecipeProvider.has(Items.WHITE_CONCRETE))
					.save(p, CreateAirspace.asResource("crafting/" + c.getName())))
			.item()
			.model((c, p) -> p.withExistingParent(c.getName(), p.modLoc("block/check_in_desk/item")))
			.build()
			.register();

	public static final BlockEntry<LuggageScaleBlock> LUGGAGE_SCALE = REGISTRATE
			.block("luggage_scale", LuggageScaleBlock::new)
			.initialProperties(SharedProperties::softMetal)
			.properties(p -> p.noOcclusion()
					.mapColor(MapColor.METAL))
			.transform(pickaxeOnly())
			.blockstate((c, p) -> p.horizontalBlock(c.get(), p.models()
					.getExistingFile(p.modLoc("block/luggage_scale/block"))))
			.recipe((c, p) -> ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, c.get())
					.define('E', AllItems.ELECTRON_TUBE)
					.define('S', AllItems.IRON_SHEET)
					.define('P', Items.HEAVY_WEIGHTED_PRESSURE_PLATE)
					.pattern("E")
					.pattern("S")
					.pattern("P")
					.unlockedBy("has_electron_tube", RegistrateRecipeProvider.has(AllItems.ELECTRON_TUBE))
					.save(p, CreateAirspace.asResource("crafting/" + c.getName())))
			.item()
			.model((c, p) -> p.withExistingParent(c.getName(), p.modLoc("block/luggage_scale/block")))
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

	// Body turned by facing, a frame edge on every open side
	private static BlockEntry<HangarDoorBlock> hangarDoor(String name, String body) {
		return REGISTRATE.block(name, HangarDoorBlock::new)
				.initialProperties(SharedProperties::softMetal)
				.properties(p -> p.noOcclusion()
						.mapColor(MapColor.COLOR_LIGHT_GRAY))
				.transform(pickaxeOnly())
				.blockstate((c, p) -> {
					MultiPartBlockStateBuilder builder = p.getMultipartBuilder(c.get());
					for (Direction facing : Direction.Plane.HORIZONTAL) {
						int y = ((int) facing.toYRot() + 180) % 360;
						builder.part()
								.modelFile(hangarModel(p, body))
								.rotationY(y)
								.addModel()
								.condition(HangarDoorBlock.FACING, facing)
								.end();
						HangarDoorBlock.SIDES.forEach((side, property) -> builder.part()
								.modelFile(hangarModel(p, "edge_" + side))
								.rotationY(y)
								.addModel()
								.condition(HangarDoorBlock.FACING, facing)
								.condition(property, false)
								.end());
					}
				})
				.recipe((c, p) -> {
					if (body.equals("panel"))
						ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, c.get(), 4)
								.define('S', AllItems.IRON_SHEET)
								.pattern("SS")
								.pattern("SS")
								.unlockedBy("has_iron_sheet", RegistrateRecipeProvider.has(AllItems.IRON_SHEET))
								.save(p, CreateAirspace.asResource("crafting/" + c.getName()));
					else
						ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, c.get())
								.requires(HANGAR_DOOR_PANEL.get())
								.requires(Tags.Items.GLASS_PANES)
								.unlockedBy("has_hangar_door_panel",
										RegistrateRecipeProvider.has(HANGAR_DOOR_PANEL.get()))
								.save(p, CreateAirspace.asResource("crafting/" + c.getName()));
				})
				.item()
				.model((c, p) -> p.withExistingParent(c.getName(), p.modLoc("block/hangar_door/" + body + "_item")))
				.build()
				.register();
	}

	private static MultiPartBlockStateBuilder.PartBuilder deskPart(MultiPartBlockStateBuilder builder,
			RegistrateBlockstateProvider p, String name, int y) {
		return builder.part()
				.modelFile(p.models()
						.getExistingFile(p.modLoc("block/check_in_desk/" + name)))
				.rotationY(y)
				.addModel();
	}

	private static ModelFile hangarModel(RegistrateBlockstateProvider p, String name) {
		return p.models()
				.getExistingFile(p.modLoc("block/hangar_door/" + name));
	}

	private static <T extends TerminalLightBlock> BlockBuilder<T, CreateRegistrate> terminalLight(DyeColor colour,
			String name, BiFunction<BlockBehaviour.Properties, DyeColor, T> factory, TagKey<Item> tag) {
		return REGISTRATE.block(colour.getSerializedName() + "_" + name, p -> factory.apply(p, colour))
				.initialProperties(SharedProperties::softMetal)
				.properties(p -> p.noOcclusion()
						.mapColor(colour)
						.strength(0.3f)
						.sound(SoundType.GLASS)
						.lightLevel(s -> s.getValue(TerminalLightBlock.LEVEL)))
				.transform(pickaxeOnly())
				.recipe((c, p) -> {
					// White ones are crafted, the others dyed like Create's seats
					if (colour == DyeColor.WHITE)
						terminalLightRecipe(name, c.get(), p);
					ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, c.get())
							.requires(colour.getTag())
							.requires(tag)
							.unlockedBy("has_" + name, RegistrateRecipeProvider.has(tag))
							.save(p, CreateAirspace.asResource("crafting/" + c.getName() + "_from_other"));
				});
	}

	private static void terminalLightRecipe(String name, ItemLike light, RegistrateRecipeProvider p) {
		ShapedRecipeBuilder builder = switch (name) {
		case "light_panel" -> ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, light, 4)
				.pattern("GGG")
				.pattern("SLS")
				.define('S', AllItems.IRON_SHEET);
		case "light_strip" -> ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, light, 6)
				.pattern("GGG")
				.pattern("NLN")
				.define('N', Tags.Items.NUGGETS_IRON);
		default -> ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, light, 2)
				.pattern(" G ")
				.pattern("NLN")
				.define('N', Tags.Items.NUGGETS_IRON);
		};
		builder.define('G', Tags.Items.GLASS_PANES)
				.define('L', Tags.Items.DUSTS_GLOWSTONE)
				.unlockedBy("has_glowstone_dust", RegistrateRecipeProvider.has(Tags.Items.DUSTS_GLOWSTONE))
				.save(p, CreateAirspace.asResource("crafting/" + name));
	}

	private static ModelFile lightModel(RegistrateBlockstateProvider p, String name) {
		return p.models()
				.getExistingFile(p.modLoc("block/terminal_light/" + name));
	}

	private static final Integer[] LIT = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };

	// Body by brightness, a frame edge on every side without a panel next to it
	private static void lightPanelState(Block block, RegistrateBlockstateProvider p) {
		MultiPartBlockStateBuilder builder = p.getMultipartBuilder(block);
		for (Direction facing : Direction.values()) {
			int x = TerminalLightBlock.rotationX(facing), y = TerminalLightBlock.rotationY(facing);
			lightPart(builder, lightModel(p, "panel"), x, y).condition(LightPanelBlock.FACING, facing)
					.condition(TerminalLightBlock.LEVEL, 0)
					.end();
			lightPart(builder, lightModel(p, "panel_on"), x, y).condition(LightPanelBlock.FACING, facing)
					.condition(TerminalLightBlock.LEVEL, LIT)
					.end();
			for (Direction side : LightPanelBlock.CONNECTED.keySet())
				lightPart(builder, lightModel(p, "panel_edge_" + side.getSerializedName()), x, y)
						.condition(LightPanelBlock.FACING, facing)
						.condition(LightPanelBlock.CONNECTED.get(side), false)
						.end();
		}
	}

	// Body by direction and brightness, caps on open ends
	private static void lightStripState(Block block, RegistrateBlockstateProvider p) {
		MultiPartBlockStateBuilder builder = p.getMultipartBuilder(block);
		for (Direction facing : Direction.values()) {
			int x = TerminalLightBlock.rotationX(facing), y = TerminalLightBlock.rotationY(facing);
			for (boolean vertical : new boolean[] { false, true }) {
				String body = vertical ? "strip_z" : "strip";
				Direction start = vertical ? Direction.NORTH : Direction.WEST;
				lightPart(builder, lightModel(p, body), x, y).condition(LightStripBlock.FACING, facing)
						.condition(LightStripBlock.VERTICAL, vertical)
						.condition(TerminalLightBlock.LEVEL, 0)
						.end();
				lightPart(builder, lightModel(p, body + "_on"), x, y).condition(LightStripBlock.FACING, facing)
						.condition(LightStripBlock.VERTICAL, vertical)
						.condition(TerminalLightBlock.LEVEL, LIT)
						.end();
				lightPart(builder, lightModel(p, "strip_cap_" + start.getSerializedName()), x, y)
						.condition(LightStripBlock.FACING, facing)
						.condition(LightStripBlock.VERTICAL, vertical)
						.condition(LightStripBlock.START, false)
						.end();
				lightPart(builder, lightModel(p, "strip_cap_" + start.getOpposite()
						.getSerializedName()), x, y).condition(LightStripBlock.FACING, facing)
						.condition(LightStripBlock.VERTICAL, vertical)
						.condition(LightStripBlock.END, false)
						.end();
			}
		}
	}

	private static MultiPartBlockStateBuilder.PartBuilder lightPart(MultiPartBlockStateBuilder builder,
			ModelFile model, int x, int y) {
		return builder.part()
				.modelFile(model)
				.rotationX(x)
				.rotationY(y)
				.addModel();
	}

	public static void register() {
	}
}
