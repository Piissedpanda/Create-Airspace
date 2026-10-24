package com.piissedpanda.createairspace.block.GroundCrew;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.piissedpanda.createairspace.CAItems;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

// Worn on the head like Create's goggles, muffles sounds while worn
public class EarDefendersItem extends Item implements Equipable {

	private static final List<Predicate<Player>> IS_WEARING = new ArrayList<>();

	static {
		IS_WEARING.add(player -> CAItems.EAR_DEFENDERS.isIn(player.getItemBySlot(EquipmentSlot.HEAD)));
	}

	public EarDefendersItem(Properties properties) {
		super(properties);
		DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
	}

	@Override
	public EquipmentSlot getEquipmentSlot() {
		return EquipmentSlot.HEAD;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
		return swapWithEquipmentSlot(this, pLevel, pPlayer, pHand);
	}

	public static boolean isWearing(Player player) {
		for (Predicate<Player> predicate : IS_WEARING)
			if (predicate.test(player))
				return true;
		return false;
	}

	public static void addIsWearingPredicate(Predicate<Player> predicate) {
		IS_WEARING.add(predicate);
	}

}
