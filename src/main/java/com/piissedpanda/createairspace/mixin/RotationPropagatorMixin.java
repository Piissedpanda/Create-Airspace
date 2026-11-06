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
				.subtract(neighbourPos));
	}

	@ModifyExpressionValue(method = "handleRemoved", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"))
	private static BlockEntity createairspace$handleRemoved(BlockEntity original,
			@Local(argsOnly = true) KineticBlockEntity removed, @Local(ordinal = 1) BlockPos neighbourPos) {
		return MultiKineticBlockEntity.replace(original, removed.getBlockPos()
				.subtract(neighbourPos));
	}

	// Remembers from which neighbour the search reached a block with parts
	@WrapMethod(method = "propagateMissingSource")
	private static void createairspace$propagateMissingSource(KineticBlockEntity update, Operation<Void> original,
			@Share("sources") LocalRef<Map<BlockPos, Queue<BlockPos>>> sourcesRef) {
		Map<BlockPos, Queue<BlockPos>> sources = new HashMap<>();
		if (update.hasSource() && update.getLevel()
				.getBlockEntity(update.getBlockPos()) instanceof MultiKineticBlockEntity) {
			Queue<BlockPos> queue = new LinkedList<>();
			queue.add(update.source);
			sources.put(update.getBlockPos(), queue);
		}
		sourcesRef.set(sources);
		original.call(update);
	}

	@ModifyExpressionValue(method = "propagateMissingSource", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"))
	private static BlockEntity createairspace$propagateMissingSourceLookup(BlockEntity original,
			@Share("sources") LocalRef<Map<BlockPos, Queue<BlockPos>>> sourcesRef) {
		if (original == null)
			return null;
		BlockPos pos = original.getBlockPos();
		Map<BlockPos, Queue<BlockPos>> sources = sourcesRef.get();
		Queue<BlockPos> queue = sources.get(pos);
		if (queue == null)
			return original;
		BlockPos from = queue.poll();
		if (queue.isEmpty())
			sources.remove(pos);
		if (from == null)
			return original;
		return MultiKineticBlockEntity.replace(original, from.subtract(pos));
	}

	@WrapOperation(method = "propagateMissingSource", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 3))
	private static boolean createairspace$propagateMissingSourceFrontier(List<Object> instance, Object e,
			Operation<Boolean> original, @Local(name = "world") Level level,
			@Local(name = "currentBE") KineticBlockEntity current,
			@Local(name = "neighbourBE") KineticBlockEntity neighbour,
			@Share("sources") LocalRef<Map<BlockPos, Queue<BlockPos>>> sourcesRef) {
		boolean result = original.call(instance, e);
		if (!(level.getBlockEntity(neighbour.getBlockPos()) instanceof MultiKineticBlockEntity))
			return result;
		sourcesRef.get()
				.computeIfAbsent(neighbour.getBlockPos(), p -> new LinkedList<>())
				.add(current.getBlockPos());
		return result;
	}

}
