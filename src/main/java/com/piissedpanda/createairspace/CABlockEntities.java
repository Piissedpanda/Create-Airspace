package com.piissedpanda.createairspace;

import com.piissedpanda.createairspace.block.AirfieldSign.AirfieldSignBlockEntity;
import com.piissedpanda.createairspace.block.AirfieldSign.AirfieldSignRenderer;
import com.piissedpanda.createairspace.block.AirportBeacon.AirportBeaconBlockEntity;
import com.piissedpanda.createairspace.block.AirportBeacon.AirportBeaconRenderer;
import com.piissedpanda.createairspace.block.BarrierPost.BarrierPostBlockEntity;
import com.piissedpanda.createairspace.block.BarrierPost.BarrierPostRenderer;
import com.piissedpanda.createairspace.block.BarrierPost.BarrierPostVisual;
import com.piissedpanda.createairspace.block.Windsock.WindsockBlockEntity;
import com.piissedpanda.createairspace.block.Windsock.WindsockRenderer;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class CABlockEntities {
	private static final CreateRegistrate REGISTRATE = CreateAirspace.REGISTRATE;

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

	public static final BlockEntityEntry<AirportBeaconBlockEntity> AIRPORT_BEACON = REGISTRATE
			.blockEntity("airport_beacon", AirportBeaconBlockEntity::new)
			.validBlocks(CABlocks.AIRPORT_BEACON)
			.renderer(() -> AirportBeaconRenderer::new)
			.register();

	public static void register() {
	}
}
