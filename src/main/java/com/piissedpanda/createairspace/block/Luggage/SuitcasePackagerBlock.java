package com.piissedpanda.createairspace.block.Luggage;

import com.piissedpanda.createairspace.CABlockEntities;
import com.piissedpanda.createairspace.CALang;
import com.simibubi.create.content.logistics.packager.PackagerBlock;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class SuitcasePackagerBlock extends PackagerBlock {

	public SuitcasePackagerBlock(Properties properties) {
		super(properties);
	}

	// Dye fixes the luggage colour, the same dye again makes it random
	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
			Player player, InteractionHand hand, BlockHitResult hitResult) {
		DyeColor dye = DyeColor.getColor(stack);
		if (dye == null)
			return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
		if (!(level.getBlockEntity(pos) instanceof SuitcasePackagerBlockEntity be))
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

		DyeColor color = be.color == dye ? null : dye;
		if (!level.isClientSide) {
			be.color = color;
			be.notifyUpdate();
			return ItemInteractionResult.SUCCESS;
		}
		if (color == null)
			CALang.translate("suitcase_packager.random_color")
					.sendStatus(player);
		else
			CALang.translate("suitcase_packager.color",
					Component.translatable("color.minecraft." + color.getSerializedName()))
					.sendStatus(player);
		return ItemInteractionResult.SUCCESS;
	}

	@Override
	public BlockEntityType<? extends PackagerBlockEntity> getBlockEntityType() {
		return CABlockEntities.SUITCASE_PACKAGER.get();
	}

}
