package com.piissedpanda.createairspace.block.Hangar;

import java.util.List;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

// Only there to draw the light cone
public class HangarLightBlockEntity extends SmartBlockEntity {

	public HangarLightBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
	}

	@Override
	protected AABB createRenderBoundingBox() {
		return new AABB(worldPosition).inflate(5, 0, 5)
				.expandTowards(0, -HangarLightRenderer.MAX_DISTANCE, 0);
	}

}
