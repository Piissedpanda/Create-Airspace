package com.piissedpanda.createairspace;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class CATags {

	public static final TagKey<Item> TERMINAL_SEATS = ItemTags.create(CreateAirspace.asResource("terminal_seats"));
	public static final TagKey<Item> LUGGAGE = ItemTags.create(CreateAirspace.asResource("luggage"));
	// Weapons and metal, set off the metal detector and the baggage scanner
	public static final TagKey<Item> SECURITY_ALARM = ItemTags.create(CreateAirspace.asResource("security_alarm"));

	// Sable gives these blocks no friction
	public static final TagKey<Block> SABLE_SLIPPERY = BlockTags
			.create(ResourceLocation.fromNamespaceAndPath("sable", "slippery"));

}
