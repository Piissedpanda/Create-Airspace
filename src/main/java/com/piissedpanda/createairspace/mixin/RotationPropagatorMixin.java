package com.piissedpanda.createairspace.mixin;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.piissedpanda.createairspace.foundation.kinetics.MultiKineticBlockEntity;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

// Lets Create's rotation propagation see the inner kinetic parts of a MultiKineticBlockEntity.
// Adapted from Create Big Cannons (RotationPropagatorMixin), MIT licensed,
// https://github.com/Cannoneers-of-Create/CreateBigCannons
@Mixin(RotationPropagator.class)
public class RotationPropagatorMixin {

	@WrapOperation(method = "handleAdded", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/RotationPropagator;propagateNewSource(Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;)V"))
	private static void createairspace$handleAdded(KineticBlockEntity added, Operation<Void> original) {
		if (!(added instanceof MultiKineticBlockEntity multi)) {
			original.call(added);
			return;
		}
		for (KineticBlockEntity part : multi.getKineticParts()) {
			original.call(part);
			if (added.isRemoved())
				return;
		}
	}

	@ModifyExpressionValue(method = "findConnectedNeighbour", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"))
	private static BlockEntity createairspace$findConnectedNeighbour(BlockEntity original,
			@Local(argsOnly = true) KineticBlockEntity current, @Local(argsOnly = true) BlockPos neighbourPos) {
		return MultiKineticBlockEntity.replace(original, current.getBlockPos()
				.subtract(neighbourPos), current);
	}

	@ModifyExpressionValue(method = "handleRemoved", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"))
	private static BlockEntity createairspace$handleRemoved(BlockEntity original,
			@Local(argsOnly = true) KineticBlockEntity removed, @Local(ordinal = 1) BlockPos neighbourPos) {
		return MultiKineticBlockEntity.replace(original, removed.getBlockPos()
				.subtract(neighbourPos), removed);
	}

	// The search walks positions, so it remembers which part it reached
	@WrapMethod(method = "propagateMissingSource")
	private static void createairspace$propagateMissingSource(KineticBlockEntity update, Operation<Void> original,
			@Share("parts") LocalRef<Map<BlockPos, Queue<KineticBlockEntity>>> partsRef) {
		Map<BlockPos, Queue<KineticBlockEntity>> parts = new HashMap<>();
		if (update.getLevel()
				.getBlockEntity(update.getBlockPos()) instanceof MultiKineticBlockEntity)
			parts.computeIfAbsent(update.getBlockPos(), p -> new LinkedList<>())
					.add(update);
		partsRef.set(parts);
		original.call(update);
	}

	@ModifyExpressionValue(method = "propagateMissingSource", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"))
	private static BlockEntity createairspace$propagateMissingSourceLookup(BlockEntity original,
			@Share("parts") LocalRef<Map<BlockPos, Queue<KineticBlockEntity>>> partsRef) {
		if (original == null)
			return null;
		Map<BlockPos, Queue<KineticBlockEntity>> parts = partsRef.get();
		Queue<KineticBlockEntity> queue = parts.get(original.getBlockPos());
		if (queue == null)
			return original;
		KineticBlockEntity part = queue.poll();
		if (queue.isEmpty())
			parts.remove(original.getBlockPos());
		return part == null ? original : part;
	}

	@WrapOperation(method = "propagateMissingSource", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 3))
	private static boolean createairspace$propagateMissingSourceFrontier(List<Object> instance, Object e,
			Operation<Boolean> original, @Local(name = "world") Level level,
			@Local(name = "neighbourBE") KineticBlockEntity neighbour,
			@Share("parts") LocalRef<Map<BlockPos, Queue<KineticBlockEntity>>> partsRef) {
		boolean result = original.call(instance, e);
		if (level.getBlockEntity(neighbour.getBlockPos()) instanceof MultiKineticBlockEntity)
			partsRef.get()
					.computeIfAbsent(neighbour.getBlockPos(), p -> new LinkedList<>())
					.add(neighbour);
		return result;
	}

}
