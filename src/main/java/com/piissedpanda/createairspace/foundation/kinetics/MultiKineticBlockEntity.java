package com.piissedpanda.createairspace.foundation.kinetics;

import java.util.List;

import javax.annotation.Nullable;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

// A block entity with several independent kinetic networks, each one an inner kinetic block entity
// that does not exist in the level. The mixins in RotationPropagatorMixin and KineticBlockEntityMixin
// hand Create the inner one facing the asking neighbour.
// Adapted from Create Big Cannons (HasMultipleKineticInterfaces, RotationPropagatorRemix), MIT licensed,
// https://github.com/Cannoneers-of-Create/CreateBigCannons
public interface MultiKineticBlockEntity {

	// from: neighbour position relative to this block, asking: the neighbour
	@Nullable
	KineticBlockEntity getKineticPart(BlockPos from, @Nullable KineticBlockEntity asking);

	List<KineticBlockEntity> getKineticParts();

	static BlockEntity replace(BlockEntity original, BlockPos from, @Nullable KineticBlockEntity asking) {
		if (!(original instanceof MultiKineticBlockEntity multi))
			return original;
		KineticBlockEntity part = multi.getKineticPart(from, asking);
		return part == null ? original : part;
	}

	static boolean owns(@Nullable BlockEntity original, KineticBlockEntity part) {
		return original instanceof MultiKineticBlockEntity multi && multi.getKineticParts()
				.contains(part);
	}

}
