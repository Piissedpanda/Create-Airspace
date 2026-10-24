package com.piissedpanda.createairspace;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.piissedpanda.createairspace.block.Luggage.LuggageType;
import com.simibubi.create.AllCreativeModeTabs;
import com.tterrag.registrate.util.entry.ItemProviderEntry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CACreativeModeTabs {

	private static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister
			.create(Registries.CREATIVE_MODE_TAB, CreateAirspace.MODID);

	public static final List<ItemProviderEntry<?, ?>> ITEMS = new ArrayList<>();

	static {
		ITEMS.addAll(CABlocks.BARRIER_POSTS);
		ITEMS.add(CABlocks.TRAFFIC_CONE);
		CABlocks.TERMINAL_SEATS.forEach(ITEMS::add);
		ITEMS.add(CABlocks.AIRFIELD_SIGN);
		ITEMS.add(CABlocks.EDGE_LIGHT);
		ITEMS.add(CABlocks.WINDSOCK);
		ITEMS.add(CABlocks.AIRPORT_BEACON);
		ITEMS.add(CABlocks.WHITE_RUNWAY_MARKING);
		ITEMS.add(CABlocks.YELLOW_RUNWAY_MARKING);
		ITEMS.add(CABlocks.RUNWAY_NUMBER);
		ITEMS.add(CABlocks.SUITCASE_PACKAGER);
		ITEMS.add(CABlocks.BAGGAGE_CART);
		for (LuggageType type : LuggageType.values())
			ITEMS.add(CAItems.LUGGAGE.get(type)
					.get(DyeColor.BLUE));
	}

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BASE_TAB = REGISTER.register("base",
			() -> CreativeModeTab.builder()
					.title(Component.translatable("itemGroup." + CreateAirspace.MODID + ".base"))
					.withTabsBefore(AllCreativeModeTabs.BASE_CREATIVE_TAB.getId())
					.icon(() -> CABlocks.BARRIER_POST.asStack())
					.displayItems(new DisplayItemsGenerator(CACreativeModeTabs.ITEMS))
					.build());

	public static void register(IEventBus modEventBus) {
		REGISTER.register(modEventBus);
	}

	private record DisplayItemsGenerator(
			List<ItemProviderEntry<?, ?>> items) implements CreativeModeTab.DisplayItemsGenerator {
		@Override
		public void accept(@NotNull CreativeModeTab.ItemDisplayParameters params,
				@NotNull CreativeModeTab.Output output) {
			for (ItemProviderEntry<?, ?> item : items) {
				output.accept(item);
			}
		}
	}

}
