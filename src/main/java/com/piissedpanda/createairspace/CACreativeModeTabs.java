package com.piissedpanda.createairspace;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.piissedpanda.createairspace.block.Luggage.LuggageType;
import com.piissedpanda.createairspace.block.RunwayMarking.PaintPailItem;
import com.simibubi.create.AllCreativeModeTabs;
import com.tterrag.registrate.util.entry.FluidEntry;
import com.tterrag.registrate.util.entry.ItemProviderEntry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidStack;
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
		ITEMS.add(CAItems.PAINT_PAIL);
		ITEMS.add(CABlocks.SUITCASE_PACKAGER);
		ITEMS.add(CABlocks.BAGGAGE_CART);
		ITEMS.add(CABlocks.WHEEL_CHOCK);
		ITEMS.add(CAItems.MARSHALLING_WAND);
		ITEMS.add(CAItems.EAR_DEFENDERS);
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
				if (item == CAItems.PAINT_PAIL)
					addPaint(output);
			}
		}

		// Full pails and paint buckets next to the empty pail
		private static void addPaint(CreativeModeTab.Output output) {
			for (FluidEntry<?> paint : List.of(CAFluids.WHITE_PAINT, CAFluids.YELLOW_PAINT)) {
				output.accept(PaintPailItem.filled(new FluidStack(paint.get().getSource(), PaintPailItem.CAPACITY)));
				output.accept(paint.get()
						.getBucket());
			}
		}
	}

}
