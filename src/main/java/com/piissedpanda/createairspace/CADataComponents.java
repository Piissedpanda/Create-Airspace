package com.piissedpanda.createairspace;

import java.util.function.UnaryOperator;

import com.mojang.serialization.Codec;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponentType.Builder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CADataComponents {

	private static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister
			.createDataComponents(Registries.DATA_COMPONENT_TYPE, CreateAirspace.MODID);

	// Paint in a paint pail
	public static final DataComponentType<SimpleFluidContent> PAINT = register("paint",
			builder -> builder.persistent(SimpleFluidContent.CODEC)
					.networkSynchronized(SimpleFluidContent.STREAM_CODEC));

	// What a paint pail paints, index of PaintPailItem.Mode
	public static final DataComponentType<Integer> PAINT_MODE = register("paint_mode",
			builder -> builder.persistent(Codec.INT)
					.networkSynchronized(ByteBufCodecs.INT));

	private static <T> DataComponentType<T> register(String name, UnaryOperator<Builder<T>> builder) {
		DataComponentType<T> type = builder.apply(DataComponentType.builder())
				.build();
		DATA_COMPONENTS.register(name, () -> type);
		return type;
	}

	public static void register(IEventBus modEventBus) {
		DATA_COMPONENTS.register(modEventBus);
	}

}
