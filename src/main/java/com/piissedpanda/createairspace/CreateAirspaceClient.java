package com.piissedpanda.createairspace;

import com.piissedpanda.createairspace.block.AirportBeacon.AirportBeaconGlare;
import com.piissedpanda.createairspace.block.GroundCrew.MarshallingWandClient;
import com.piissedpanda.createairspace.block.RunwayMarking.PaintPailItem;
import com.piissedpanda.createairspace.foundation.ponder.CAPonderPlugin;

import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.client.renderer.item.ItemProperties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = CreateAirspace.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods
// in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = CreateAirspace.MODID, value = Dist.CLIENT)
public class CreateAirspaceClient {
	public CreateAirspaceClient(ModContainer container) {
		// Allows NeoForge to create a config screen for this mod's configs.
		// The config screen is accessed by going to the Mods screen > clicking on your
		// mod > clicking on config.
		// Do not forget to add translations for your config options to the en_us.json
		// file.
		container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
		CAPartialModels.initLuggage();
	}

	@SubscribeEvent
	static void onClientSetup(FMLClientSetupEvent event) {
		PonderIndex.addPlugin(new CAPonderPlugin());
		event.enqueueWork(MarshallingWandClient::registerItemProperties);
		event.enqueueWork(() -> ItemProperties.register(CAItems.PAINT_PAIL.get(), CreateAirspace.asResource("paint"),
				(stack, level, entity, seed) -> PaintPailItem.getPaint(stack)
						.isEmpty() ? 0 : 1));
	}

	// The paint layer of the pail takes the colour of its paint
	@SubscribeEvent
	static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
		event.register((stack, tintIndex) -> tintIndex != 1 ? -1
				: PaintPailItem.isYellow(PaintPailItem.getPaint(stack)) ? 0xFFECB81A : 0xFFF0F0EA,
				CAItems.PAINT_PAIL.get());
	}

	@SubscribeEvent
	static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
		MarshallingWandClient.register(event);
	}

	@SubscribeEvent
	static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
		event.registerAbove(VanillaGuiLayers.CAMERA_OVERLAYS, AirportBeaconGlare.LAYER, AirportBeaconGlare::render);
	}
}
