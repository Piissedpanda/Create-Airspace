package com.piissedpanda.createaviation;

import static com.simibubi.create.api.behaviour.display.DisplayTarget.displayTarget;
import static com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour.interactionBehaviour;
import static com.simibubi.create.api.behaviour.movement.MovementBehaviour.movementBehaviour;
import static com.simibubi.create.foundation.data.TagGen.axeOnly;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

import java.util.List;

import com.piissedpanda.createaviation.block.AirfieldSign.AirfieldSignBlock;
import com.piissedpanda.createaviation.block.BarrierPost.BarrierPostBlock;
import com.piissedpanda.createaviation.block.EdgeLight.EdgeLightBlock;
import com.piissedpanda.createaviation.block.TerminalSeat.TerminalSeatBlock;
import com.piissedpanda.createaviation.block.TrafficCone.TrafficConeBlock;
import com.simibubi.create.AllTags.AllBlockTags;
import com.simibubi.create.content.contraptions.actors.seat.SeatInteractionBehaviour;
import com.simibubi.create.content.contraptions.actors.seat.SeatMovementBehaviour;
import com.simibubi.create.foundation.block.DyedBlockList;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;

public class CABlocks {
	private static final CreateRegistrate REGISTRATE = CreateAviation.REGISTRATE;

	static {
		REGISTRATE.setCreativeTab(CACreativeModeTabs.BASE_TAB);
	}

	public static final BlockEntry<BarrierPostBlock>

	BARRIER_POST = barrierPost("barrier_post", "create:block/industrial_iron_block", MapColor.PODZOL),
			ANDESITE_BARRIER_POST = barrierPost("andesite_barrier_post", "create:block/andesite_casing",
					MapColor.STONE),
			BRASS_BARRIER_POST = barrierPost("brass_barrier_post", "create:block/brass_casing",
					MapColor.TERRACOTTA_BROWN),
			COPPER_BARRIER_POST = barrierPost("copper_barrier_post", "create:block/copper_casing",
					MapColor.COLOR_LIGHT_GRAY),
			TRAIN_BARRIER_POST = barrierPost("train_barrier_post", "create:block/railway_casing",
					MapColor.TERRACOTTA_CYAN);

	public static final List<BlockEntry<BarrierPostBlock>> BARRIER_POSTS = List.of(BARRIER_POST,
			ANDESITE_BARRIER_POST, BRASS_BARRIER_POST, COPPER_BARRIER_POST, TRAIN_BARRIER_POST);

	public static final BlockEntry<TrafficConeBlock> TRAFFIC_CONE = REGISTRATE
			.block("traffic_cone", TrafficConeBlock::new)
			.properties(p -> p.noOcclusion()
					.mapColor(MapColor.COLOR_ORANGE)
					.strength(0.5f)
					.sound(SoundType.BAMBOO_WOOD))
			.blockstate((c, p) -> p.horizontalBlock(c.get(), AssetLookup.partialBaseModel(c, p)))
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
				.item()
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
						boolean lit = state.getValue(EdgeLightBlock.POWERED);
						String lens = "minecraft:block/" + color + (lit ? "_concrete" : "_terracotta");
						return ConfiguredModel.builder()
								.modelFile(p.models()
										.withExistingParent("block/edge_light/" + color + (lit ? "_on" : "_off"),
												p.modLoc("block/edge_light/" + (lit ? "lit" : "base")))
										.texture("lens", lens))
								.build();
					}, EdgeLightBlock.WATERLOGGED))
			.item()
			.model((c, p) -> p.withExistingParent(c.getName(), p.modLoc("block/edge_light/white_off")))
			.build()
			.register();

	public static boolean isBarrierPost(BlockState state) {
		return state.getBlock() instanceof BarrierPostBlock;
	}

	// All posts share block/barrier_post/*, only the "material" texture changes
	private static BlockEntry<BarrierPostBlock> barrierPost(String name, String material, MapColor color) {
		return REGISTRATE.block(name, BarrierPostBlock::new)
				.initialProperties(SharedProperties::stone)
				.properties(p -> p.noOcclusion()
						.mapColor(color))
				.transform(axeOrPickaxe())
				.blockstate((c, p) -> p.simpleBlock(c.getEntry(), p.models()
						.withExistingParent("block/" + c.getName() + "/block", p.modLoc("block/barrier_post/base"))
						.texture("material", material)
						.texture("particle", material)))
				.item()
				.model((c, p) -> p.withExistingParent(c.getName(), p.modLoc("block/barrier_post/item_base"))
						.texture("material", material)
						.texture("particle", material))
				.build()
				.register();
	}

	public static void register() {
	}
}
