package com.piissedpanda.createairspace;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class CATags {

	public static final TagKey<Item> TERMINAL_SEATS = ItemTags.create(CreateAirspace.asResource("terminal_seats"));
	public static final TagKey<Item> LIGHT_PANELS = ItemTags.create(CreateAirspace.asResource("light_panels"));
	public static final TagKey<Item> LIGHT_STRIPS = ItemTags.create(CreateAirspace.asResource("light_strips"));
	public static final TagKey<Item> FLOOR_LIGHTS = ItemTags.create(CreateAirspace.asResource("floor_lights"));
	public static final TagKey<Item> LUGGAGE = ItemTags.create(CreateAirspace.asResource("luggage"));

}
