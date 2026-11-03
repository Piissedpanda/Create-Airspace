package com.piissedpanda.createairspace.block.Security;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

// Checks everyone who steps into the frame once, until they leave it again
public class MetalDetectorBlockEntity extends SmartBlockEntity {

	private Set<UUID> inside = new HashSet<>();
	private int alarmTicks;

	public MetalDetectorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
	}

	@Override
	public void tick() {
		super.tick();
		if (level.isClientSide)
			return;

		if (alarmTicks > 0) {
			if (alarmTicks % 5 == 0)
				SecurityScan.beep(level, worldPosition);
			if (--alarmTicks == 0)
				MetalDetectorBlock.setAlarm(level, worldPosition, false);
		}
		if (level.getGameTime() % 2 != 0)
			return;

		AABB frame = new AABB(worldPosition).expandTowards(0, 1, 0);
		Set<UUID> now = new HashSet<>();
		boolean found = false;
		for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, frame, e -> !e.isSpectator())) {
			now.add(entity.getUUID());
			if (!inside.contains(entity.getUUID()) && SecurityScan.carriesSuspicious(entity))
				found = true;
		}
		inside = now;
		if (!found)
			return;
		alarmTicks = SecurityScan.ALARM_TICKS;
		MetalDetectorBlock.setAlarm(level, worldPosition, true);
	}

}
