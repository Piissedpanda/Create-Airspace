package com.piissedpanda.createairspace.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.piissedpanda.createairspace.foundation.kinetics.MultiKineticBlockEntity;
import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import net.minecraft.world.level.block.entity.BlockEntity;

// The network drops members that are not the block entity in the level, which would drop the inner parts
// of a MultiKineticBlockEntity and with them their stress
@Mixin(KineticNetwork.class)
public class KineticNetworkMixin {

	@ModifyExpressionValue(method = { "calculateStress",
			"calculateCapacity" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"))
	private BlockEntity createairspace$keepParts(BlockEntity original, @Local KineticBlockEntity be) {
		return MultiKineticBlockEntity.owns(original, be) ? be : original;
	}

}
