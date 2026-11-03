package com.piissedpanda.createairspace;

import com.piissedpanda.createairspace.block.Cargo.CargoContainerBlockEntity;
import com.piissedpanda.createairspace.block.Cargo.ContainerLockBlockEntity;
import com.piissedpanda.createairspace.block.Cargo.ContainerPortBlockEntity;
import com.piissedpanda.createairspace.block.AirfieldSign.AirfieldSignBlockEntity;
import com.piissedpanda.createairspace.block.AirfieldSign.AirfieldSignRenderer;
import com.piissedpanda.createairspace.block.AirportBeacon.AirportBeaconBlockEntity;
import com.piissedpanda.createairspace.block.AirportBeacon.AirportBeaconRenderer;
import com.piissedpanda.createairspace.block.BarrierPost.BarrierPostBlockEntity;
import com.piissedpanda.createairspace.block.BarrierPost.BarrierPostRenderer;
import com.piissedpanda.createairspace.block.BarrierPost.BarrierPostVisual;
import com.piissedpanda.createairspace.block.BaggageCart.BaggageCartBlockEntity;
import com.piissedpanda.createairspace.block.BaggageCart.BaggageCartRenderer;
import com.piissedpanda.createairspace.block.CheckIn.LuggageScaleBlockEntity;
import com.piissedpanda.createairspace.block.CheckIn.LuggageScaleRenderer;
import com.piissedpanda.createairspace.block.Hangar.HangarLightBlockEntity;
import com.piissedpanda.createairspace.block.Hangar.HangarLightRenderer;
import com.piissedpanda.createairspace.block.Luggage.SuitcasePackagerBlockEntity;
import com.piissedpanda.createairspace.block.Papi.PapiLightBlockEntity;
import com.piissedpanda.createairspace.block.Papi.PapiLightRenderer;
import com.piissedpanda.createairspace.block.Security.BaggageScannerBlockEntity;
import com.piissedpanda.createairspace.block.Security.MetalDetectorBlockEntity;
import com.piissedpanda.createairspace.block.StopBar.RunwayGuardLightBlockEntity;
import com.piissedpanda.createairspace.block.StopBar.RunwayGuardLightRenderer;
import com.piissedpanda.createairspace.block.Windsock.WindsockBlockEntity;
import com.piissedpanda.createairspace.block.Windsock.WindsockRenderer;
import com.simibubi.create.content.logistics.packager.PackagerRenderer;
import com.simibubi.create.content.logistics.packager.PackagerVisual;
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
			.validBlocks(CABlocks.AIRFIELD_SIGN, CABlocks.GATE_SIGN)
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

	public static final BlockEntityEntry<SuitcasePackagerBlockEntity> SUITCASE_PACKAGER = REGISTRATE
			.blockEntity("suitcase_packager", SuitcasePackagerBlockEntity::new)
			.visual(() -> PackagerVisual::new, true)
			.validBlocks(CABlocks.SUITCASE_PACKAGER)
			.renderer(() -> PackagerRenderer::new)
			.register();

	public static final BlockEntityEntry<BaggageCartBlockEntity> BAGGAGE_CART = REGISTRATE
			.blockEntity("baggage_cart", BaggageCartBlockEntity::new)
			.validBlocks(CABlocks.BAGGAGE_CART)
			.renderer(() -> BaggageCartRenderer::new)
			.register();

	public static final BlockEntityEntry<HangarLightBlockEntity> HANGAR_LIGHT = REGISTRATE
			.blockEntity("hangar_light", HangarLightBlockEntity::new)
			.validBlocks(CABlocks.HANGAR_LIGHT)
			.renderer(() -> HangarLightRenderer::new)
			.register();

	public static final BlockEntityEntry<LuggageScaleBlockEntity> LUGGAGE_SCALE = REGISTRATE
			.blockEntity("luggage_scale", LuggageScaleBlockEntity::new)
			.validBlocks(CABlocks.LUGGAGE_SCALE)
			.renderer(() -> LuggageScaleRenderer::new)
			.register();

	public static final BlockEntityEntry<CargoContainerBlockEntity> CARGO_CONTAINER = REGISTRATE
			.blockEntity("cargo_container", CargoContainerBlockEntity::new)
			.validBlocks(CABlocks.CARGO_CONTAINER)
			.register();

	public static final BlockEntityEntry<ContainerLockBlockEntity> CONTAINER_LOCK = REGISTRATE
			.blockEntity("container_lock", ContainerLockBlockEntity::new)
			.validBlocks(CABlocks.CONTAINER_LOCK)
			.register();

	public static final BlockEntityEntry<ContainerPortBlockEntity> CONTAINER_PORT = REGISTRATE
			.blockEntity("container_port", ContainerPortBlockEntity::new)
			.validBlocks(CABlocks.CONTAINER_PORT)
			.register();

	public static final BlockEntityEntry<PapiLightBlockEntity> PAPI_LIGHT = REGISTRATE
			.blockEntity("papi_light", PapiLightBlockEntity::new)
			.validBlocks(CABlocks.PAPI_LIGHT)
			.renderer(() -> PapiLightRenderer::new)
			.register();

	public static final BlockEntityEntry<RunwayGuardLightBlockEntity> RUNWAY_GUARD_LIGHT = REGISTRATE
			.blockEntity("runway_guard_light", RunwayGuardLightBlockEntity::new)
			.validBlocks(CABlocks.RUNWAY_GUARD_LIGHT)
			.renderer(() -> RunwayGuardLightRenderer::new)
			.register();

	public static final BlockEntityEntry<MetalDetectorBlockEntity> METAL_DETECTOR = REGISTRATE
			.blockEntity("metal_detector", MetalDetectorBlockEntity::new)
			.validBlocks(CABlocks.METAL_DETECTOR)
			.register();

	public static final BlockEntityEntry<BaggageScannerBlockEntity> BAGGAGE_SCANNER = REGISTRATE
			.blockEntity("baggage_scanner", BaggageScannerBlockEntity::new)
			.validBlocks(CABlocks.BAGGAGE_SCANNER)
			.register();

	public static void register() {
	}
}
