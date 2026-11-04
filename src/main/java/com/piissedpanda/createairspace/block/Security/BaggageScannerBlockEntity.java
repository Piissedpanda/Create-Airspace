package com.piissedpanda.createairspace.block.Security;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour.TransportedResult;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.animation.LerpedFloat.Chaser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

// Reads the items on the belt below without holding them up, each stack is screened once
public class BaggageScannerBlockEntity extends SmartBlockEntity {

	private static final double CURTAIN = 0.3; // distance from the centre where items touch a curtain

	private Set<TransportedItemStack> seen = newIdentitySet();
	private int alarmTicks;

	// Client only, the curtains at the negative and the positive end of the axis
	public final LerpedFloat[] curtains = { curtain(), curtain() };
	private Set<TransportedItemStack> atCurtain = newIdentitySet();

	public BaggageScannerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
	}

	@Override
	public void tick() {
		super.tick();
		if (level.isClientSide) {
			tickCurtains();
			return;
		}

		if (alarmTicks > 0) {
			if (alarmTicks % 5 == 0)
				SecurityScan.beep(level, worldPosition);
			if (--alarmTicks == 0)
				BaggageScannerBlock.setAlarm(level, worldPosition, false);
		}
		if (level.getGameTime() % 20 == 0)
			BaggageScannerBlock.removeCreateCasing(level, worldPosition);
		if (level.getGameTime() % 2 != 0)
			return;

		TransportedItemStackHandlerBehaviour belt = getBelt();
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

	// An item reaching a curtain pushes it in the direction the belt runs
	private void tickCurtains() {
		for (LerpedFloat curtain : curtains)
			curtain.tickChaser();
		TransportedItemStackHandlerBehaviour belt = getBelt();
		if (belt == null || !(level.getBlockEntity(worldPosition.below()) instanceof BeltBlockEntity beltBE))
			return;

		Direction.Axis axis = getBlockState().getValue(BaggageScannerBlock.AXIS);
		Direction positive = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
		boolean forward = beltBE.getMovementFacing() == positive;
		Vec3 centre = Vec3.atCenterOf(worldPosition);

		Set<TransportedItemStack> now = newIdentitySet();
		belt.handleCenteredProcessingOnAllItems(.5f, transported -> {
			double offset = belt.getWorldPositionOf(transported)
					.subtract(centre)
					.get(axis);
			if (Math.abs(offset) < CURTAIN)
				return TransportedResult.doNothing();
			now.add(transported);
			if (!atCurtain.contains(transported))
				curtains[offset > 0 ? 1 : 0].setValue(forward == offset > 0 ? 1 : -1);
			return TransportedResult.doNothing();
		});
		atCurtain = now;
	}

	private TransportedItemStackHandlerBehaviour getBelt() {
		return BlockEntityBehaviour.get(level, worldPosition.below(), TransportedItemStackHandlerBehaviour.TYPE);
	}

	private static LerpedFloat curtain() {
		return LerpedFloat.linear()
				.startWithValue(0)
				.chase(0, .05f, Chaser.EXP);
	}

	private static Set<TransportedItemStack> newIdentitySet() {
		return Collections.newSetFromMap(new IdentityHashMap<>());
	}

}
