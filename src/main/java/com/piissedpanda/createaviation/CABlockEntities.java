package com.piissedpanda.createaviation;

import com.piissedpanda.createaviation.block.AirfieldSign.AirfieldSignBlockEntity;
import com.piissedpanda.createaviation.block.AirfieldSign.AirfieldSignRenderer;
import com.piissedpanda.createaviation.block.BarrierPost.BarrierPostBlockEntity;
import com.piissedpanda.createaviation.block.BarrierPost.BarrierPostRenderer;
import com.piissedpanda.createaviation.block.BarrierPost.BarrierPostVisual;
import com.piissedpanda.createaviation.block.Windsock.WindsockBlockEntity;
import com.piissedpanda.createaviation.block.Windsock.WindsockRenderer;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class CABlockEntities {
	private static final CreateRegistrate REGISTRATE = CreateAviation.REGISTRATE;

	public static final BlockEntityEntry<BarrierPostBlockEntity> BARRIER_POST = REGISTRATE
			.blockEntity("barrier_post", BarrierPostBlockEntity::new)
			.visual(() -> BarrierPostVisual::new)
			.validBlocks(CABlocks.BARRIER_POST, CABlocks.ANDESITE_BARRIER_POST,
					CABlocks.BRASS_BARRIER_POST, CABlocks.COPPER_BARRIER_POST,
					CABlocks.TRAIN_BARRIER_POST)
			.renderer(() -> BarrierPostRenderer::new)
			.register();

	public static final BlockEntityEntry<AirfieldSignBlockEntity> AIRFIELD_SIGN = REGISTRATE
			.blockEntity("airfield_sign", AirfieldSignBlockEntity::new)
			.validBlocks(CABlocks.AIRFIELD_SIGN)
			.renderer(() -> AirfieldSignRenderer::new)
			.register();

	public static final BlockEntityEntry<WindsockBlockEntity> WINDSOCK = REGISTRATE
			.blockEntity("windsock", WindsockBlockEntity::new)
			.validBlocks(CABlocks.WINDSOCK)
			.renderer(() -> WindsockRenderer::new)
			.register();

	public static void register() {
	}
}
