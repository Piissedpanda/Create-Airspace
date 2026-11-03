package com.piissedpanda.createairspace.block.Security;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour.TransportedResult;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

// Reads the items on the belt below without holding them up, each stack is screened once
public class BaggageScannerBlockEntity extends SmartBlockEntity {

	private Set<TransportedItemStack> seen = newIdentitySet();
	private int alarmTicks;

	public BaggageScannerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
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
				BaggageScannerBlock.setAlarm(level, worldPosition, false);
		}
		if (level.getGameTime() % 2 != 0)
			return;

		TransportedItemStackHandlerBehaviour belt = BlockEntityBehaviour.get(level, worldPosition.below(),
				TransportedItemStackHandlerBehaviour.TYPE);
		if (belt == null) {
			seen.clear();
			return;
		}

		Set<TransportedItemStack> now = newIdentitySet();
		boolean[] found = { false };
		belt.handleCenteredProcessingOnAllItems(.5f, transported -> {
			now.add(transported);
			if (!seen.contains(transported) && SecurityScan.isSuspicious(transported.stack))
				found[0] = true;
			return TransportedResult.doNothing();
		});
		seen = now;
		if (!found[0])
			return;
		alarmTicks = SecurityScan.ALARM_TICKS;
		BaggageScannerBlock.setAlarm(level, worldPosition, true);
	}

	private static Set<TransportedItemStack> newIdentitySet() {
		return Collections.newSetFromMap(new IdentityHashMap<>());
	}

}
