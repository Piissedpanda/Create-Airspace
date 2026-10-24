package com.piissedpanda.createairspace.block.GroundCrew;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

// Right-click waves the wand, and the one in the other hand too
public class MarshallingWandItem extends Item {

	public MarshallingWandItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
		InteractionHand other = pHand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND
				: InteractionHand.MAIN_HAND;
		if (pPlayer.getItemInHand(other)
				.is(this))
			pPlayer.swing(other);
		return InteractionResultHolder.sidedSuccess(pPlayer.getItemInHand(pHand), pLevel.isClientSide);
	}

}
