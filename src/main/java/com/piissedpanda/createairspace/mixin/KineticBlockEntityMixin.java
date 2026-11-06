package com.piissedpanda.createairspace.mixin;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.piissedpanda.createairspace.foundation.kinetics.MultiKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

// A kinetic source that is a MultiKineticBlockEntity means the part facing this block.
// Adapted from Create Big Cannons (KineticBlockEntityMixin), MIT licensed,
// https://github.com/Cannoneers-of-Create/CreateBigCannons
@Mixin(KineticBlockEntity.class)
public abstract class KineticBlockEntityMixin extends SmartBlockEntity {

	@Shadow
	@Nullable
	public BlockPos source;

	KineticBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@ModifyExpressionValue(method = "validateKinetics", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"))
	private BlockEntity createairspace$validateKinetics(BlockEntity original) {
		return MultiKineticBlockEntity.replace(original, getBlockPos().subtract(source));
	}

	@ModifyExpressionValue(method = "setSource", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"))
	private BlockEntity createairspace$setSource(BlockEntity original) {
		return MultiKineticBlockEntity.replace(original, getBlockPos().subtract(source));
	}

}
