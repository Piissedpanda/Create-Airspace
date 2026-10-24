package com.piissedpanda.createairspace.block.Luggage;

import com.simibubi.create.content.logistics.box.PackageStyles.PackageStyle;

import net.minecraft.world.item.DyeColor;

// Width and height in px, rigging = hook distance from the top of the handle grip + 4
public enum LuggageType {
	HARDSHELL_SUITCASE("hardshell_suitcase", 10, 14, 18.5f, true),
	TROLLEY_SUITCASE("trolley_suitcase", 10, 15, 19.5f, false),
	DUFFEL_BAG("duffel_bag", 12, 10, 14f, false),
	BACKPACK("backpack", 8, 13, 17f, false);

	public final String id;
	public final PackageStyle style;
	public final boolean hardShell;

	LuggageType(String id, int width, int height, float rigging, boolean hardShell) {
		this.id = id;
		this.style = new PackageStyle(id, width, height, rigging, false);
		this.hardShell = hardShell;
	}

	public String itemName(DyeColor color) {
		return color.getSerializedName() + "_" + id;
	}

	public String bodyTexture(DyeColor color) {
		return "minecraft:block/" + color.getSerializedName() + (hardShell ? "_concrete" : "_wool");
	}

}
