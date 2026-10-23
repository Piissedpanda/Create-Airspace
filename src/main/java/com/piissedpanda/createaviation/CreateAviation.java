package com.piissedpanda.createaviation;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.piissedpanda.createaviation.foundation.config.CAConfigs;
import com.piissedpanda.createaviation.foundation.data.CADatagen;
import com.simibubi.create.foundation.data.CreateRegistrate;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(CreateAviation.MODID)
public class CreateAviation {

	public static final String MODID = "createaviation";

	public static final String VERSION = "1.0.0";

	public static final Logger LOGGER = LogUtils.getLogger();

	public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID)
			.defaultCreativeTab((ResourceKey<CreativeModeTab>) null);

	public CreateAviation(IEventBus modEventBus, ModContainer modContainer) {
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
		CABlockEntities.register();

		CAConfigs.register(modLoadingContext, modContainer);

		modEventBus.addListener(EventPriority.HIGHEST, CADatagen::gatherDataHighPriority);
		modEventBus.addListener(EventPriority.LOWEST, CADatagen::gatherData);
	}

	private void commonSetup(FMLCommonSetupEvent event) {

	}

	@SubscribeEvent
	public void onServerStarting(ServerStartingEvent event) {

	}

	public static ResourceLocation asResource(String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}
}
