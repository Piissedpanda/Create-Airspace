package com.piissedpanda.createairspace;

import com.piissedpanda.createairspace.block.Cargo.CargoContainers;
import com.piissedpanda.createairspace.block.Cargo.CargoDock;
import com.piissedpanda.createairspace.block.Cargo.ContainerPortBlockEntity;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.piissedpanda.createairspace.block.BaggageCart.BaggageCartBlockEntity;
import com.piissedpanda.createairspace.block.CheckIn.LuggageScaleBlockEntity;
import com.piissedpanda.createairspace.block.GroundCrew.EarDefendersCurios;
import com.piissedpanda.createairspace.block.Luggage.SuitcasePackagerBlockEntity;
import com.piissedpanda.createairspace.block.RunwayMarking.PaintPailItem;
import com.piissedpanda.createairspace.foundation.config.CAConfigs;
import com.piissedpanda.createairspace.foundation.data.CADatagen;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;

import net.createmod.catnip.lang.FontHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(CreateAirspace.MODID)
public class CreateAirspace {

	public static final String MODID = "createairspace";

	public static final String VERSION = "1.2.0";

	public static final Logger LOGGER = LogUtils.getLogger();

	public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID)
			.defaultCreativeTab((ResourceKey<CreativeModeTab>) null)
			.setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE));

	public CreateAirspace(IEventBus modEventBus, ModContainer modContainer) {
		ModLoadingContext modLoadingContext = ModLoadingContext.get();

		modEventBus.addListener(this::commonSetup);

		NeoForge.EVENT_BUS.register(this);

		REGISTRATE.registerEventListeners(modEventBus);

		CAPackets.register();
		CAPartialModels.init();
		CACreativeModeTabs.register(modEventBus);
		CADisplayTargets.register();
		CABlocks.register();
		CAItems.register();
		CAFluids.register();
		CADataComponents.register(modEventBus);
		CABlockEntities.register();

		CAConfigs.register(modLoadingContext, modContainer);

		modEventBus.addListener(SuitcasePackagerBlockEntity::registerCapabilities);
		modEventBus.addListener(BaggageCartBlockEntity::registerCapabilities);
		modEventBus.addListener(PaintPailItem::registerCapabilities);
		modEventBus.addListener(LuggageScaleBlockEntity::registerCapabilities);
		modEventBus.addListener(ContainerPortBlockEntity::registerCapabilities);
		CargoDock.register();
		if (ModList.get()
				.isLoaded("curios"))
			EarDefendersCurios.init(modEventBus);
		modEventBus.addListener(EventPriority.HIGHEST, CADatagen::gatherDataHighPriority);
		modEventBus.addListener(EventPriority.LOWEST, CADatagen::gatherData);
	}

	private void commonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(CargoContainers::registerMovementChecks);
	}

	@SubscribeEvent
	public void onServerStarting(ServerStartingEvent event) {

	}

	public static ResourceLocation asResource(String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}
}
