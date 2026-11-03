package com.piissedpanda.createairspace.block.Security;

import java.util.ArrayList;
import java.util.List;

import com.piissedpanda.createairspace.CATags;
import com.simibubi.create.AllDataComponents;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

// Shared by the metal detector and the baggage scanner, what counts is the item tag
public class SecurityScan {

	public static final BooleanProperty ALARM = BooleanProperty.create("alarm");
	public static final int ALARM_TICKS = 40;
	private static final int MAX_DEPTH = 3; // luggage in a package in a shulker box

	public static boolean carriesSuspicious(LivingEntity entity) {
		if (entity instanceof Player player) {
			Inventory inventory = player.getInventory();
			for (int i = 0; i < inventory.getContainerSize(); i++)
				if (isSuspicious(inventory.getItem(i)))
					return true;
			return false;
		}
		for (ItemStack stack : entity.getAllSlots())
			if (isSuspicious(stack))
				return true;
		return false;
	}

	public static boolean isSuspicious(ItemStack stack) {
		return isSuspicious(stack, 0);
	}

	private static boolean isSuspicious(ItemStack stack, int depth) {
		if (stack.isEmpty())
			return false;
		if (stack.is(CATags.SECURITY_ALARM))
			return true;
		if (depth >= MAX_DEPTH)
			return false;
		for (ItemStack inner : getContents(stack))
			if (isSuspicious(inner, depth + 1))
				return true;
		return false;
	}

	// Shulker boxes, bundles, Create packages and luggage
	private static List<ItemStack> getContents(ItemStack stack) {
		List<ItemStack> contents = new ArrayList<>();
		ItemContainerContents container = stack.get(DataComponents.CONTAINER);
		if (container != null)
			container.nonEmptyItems()
					.forEach(contents::add);
		ItemContainerContents box = stack.get(AllDataComponents.PACKAGE_CONTENTS);
		if (box != null)
			box.nonEmptyItems()
					.forEach(contents::add);
		BundleContents bundle = stack.get(DataComponents.BUNDLE_CONTENTS);
		if (bundle != null)
			bundle.items()
					.forEach(contents::add);
		return contents;
	}

	public static void beep(Level level, BlockPos pos) {
		level.playSound(null, pos, SoundEvents.NOTE_BLOCK_BIT.value(), SoundSource.BLOCKS, 1, 2);
	}

}
