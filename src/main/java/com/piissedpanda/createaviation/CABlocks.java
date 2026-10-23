package com.piissedpanda.createaviation;

import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;

import java.util.List;

import com.piissedpanda.createaviation.block.BarrierPost.BarrierPostBlock;
import com.piissedpanda.createaviation.block.TrafficCone.TrafficConeBlock;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

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
