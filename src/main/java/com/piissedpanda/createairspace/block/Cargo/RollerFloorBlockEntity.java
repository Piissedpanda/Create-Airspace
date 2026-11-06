package com.piissedpanda.createairspace.block.Cargo;

import static net.minecraft.ChatFormatting.GRAY;

import java.util.ArrayList;
import java.util.List;

import com.piissedpanda.createairspace.foundation.kinetics.MultiKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencerInstructions;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;

import net.createmod.catnip.data.Iterate;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

// Two independent kinetic networks, one through the shafts on the west and east side and one through
// north and south. Each turns its own rollers, which move physics objects across the floor.
// Structure after Create Big Cannons' cannon mount (MIT)
public class RollerFloorBlockEntity extends KineticBlockEntity implements MultiKineticBlockEntity {

	public final Drive driveX;
	public final Drive driveZ;

	public RollerFloorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		driveX = new Drive(type, pos, state, this, Direction.Axis.X);
		driveZ = new Drive(type, pos, state, this, Direction.Axis.Z);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
	}

	@Override
	public void setLevel(Level level) {
		super.setLevel(level);
		driveX.setLevel(level);
		driveZ.setLevel(level);
	}

	@Override
	public void setBlockState(BlockState state) {
		super.setBlockState(state);
		driveX.setBlockState(state);
		driveZ.setBlockState(state);
	}

	@Override
	public void tick() {
		super.tick();
		driveX.tick();
		driveZ.tick();
	}

	@Override
	public void remove() {
		driveX.remove();
		driveZ.remove();
		super.remove();
	}

	// Blocks per second as fast as a piston, the X network moves things along Z and
	// back
	public Vec3 getSurfaceVelocity() {
		return new Vec3(-driveZ.getLinearStep() * 20, 0, driveX.getLinearStep() * 20);
	}

	public Drive getDrive(Direction.Axis axis) {
		return axis == Direction.Axis.X ? driveX : driveZ;
	}

	@Override
	public KineticBlockEntity getKineticPart(BlockPos from, KineticBlockEntity asking) {
		// Neighbouring floors join drive to drive, whatever side they are on
		if (asking instanceof Drive drive && drive.floor != this)
			return getDrive(drive.axis);
		if (from.getY() != 0)
			return null;
		if (from.getX() != 0 && from.getZ() == 0)
			return driveX;
		if (from.getZ() != 0 && from.getX() == 0)
			return driveZ;
		return null;
	}

	@Override
	public List<KineticBlockEntity> getKineticParts() {
		return List.of(driveX, driveZ);
	}

	@Override
	protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		tag.put("DriveX", driveX.writeDrive(registries, clientPacket));
		tag.put("DriveZ", driveZ.writeDrive(registries, clientPacket));
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		driveX.readDrive(tag.getCompound("DriveX"), registries, clientPacket);
		driveZ.readDrive(tag.getCompound("DriveZ"), registries, clientPacket);
	}

	// Adapted from KineticBlockEntity, the floor itself has no speed of its own
	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		if (!IRotate.StressImpact.isEnabled())
			return false;
		float stress = driveX.calculateStressApplied() * Math.abs(driveX.getTheoreticalSpeed())
				+ driveZ.calculateStressApplied() * Math.abs(driveZ.getTheoreticalSpeed());
		if (stress == 0)
			return false;
		CreateLang.translate("gui.goggles.kinetic_stats")
				.forGoggles(tooltip);
		CreateLang.translate("tooltip.stressImpact")
				.style(GRAY)
				.forGoggles(tooltip);
		CreateLang.number(stress)
				.translate("generic.unit.stress")
				.style(ChatFormatting.AQUA)
				.space()
				.add(CreateLang.translate("gui.goggles.at_current_speed")
						.style(ChatFormatting.DARK_GRAY))
				.forGoggles(tooltip, 1);
		return true;
	}

	// One of the two networks, it is not placed in the level, the floor carries it
	public static class Drive extends KineticBlockEntity {

		private final RollerFloorBlockEntity floor;
		public final Direction.Axis axis;
		private double sequencedLimit = -1;

		public Drive(BlockEntityType<?> type, BlockPos pos, BlockState state, RollerFloorBlockEntity floor,
				Direction.Axis axis) {
			super(type, pos, state);
			this.floor = floor;
			this.axis = axis;
		}

		@Override
		public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		}

		// Shafts on the own axis, floors all around: a floor area is one network
		@Override
		public List<BlockPos> addPropagationLocations(IRotate block, BlockState state, List<BlockPos> neighbours) {
			List<BlockPos> own = new ArrayList<>();
			for (Direction side : Iterate.horizontalDirections) {
				BlockPos pos = worldPosition.relative(side);
				if (side.getAxis() == axis || level != null && level.getBlockState(pos)
						.getBlock() instanceof RollerFloorBlock)
					own.add(pos);
			}
			return own;
		}

		// Blocks per tick, a sequenced gearshift set to a distance stops it after
		// exactly that distance
		public float getLinearStep() {
			float step = convertToLinear(getSpeed());
			if (sequencedLimit < 0)
				return step;
			return (float) Math.copySign(Math.min(Math.abs(step), sequencedLimit), step);
		}

		@Override
		public void tick() {
			super.tick();
			if (sequencedLimit > 0)
				sequencedLimit = Math.max(0, sequencedLimit - Math.abs(convertToLinear(getSpeed())));
		}

		// As in LinearActuatorBlockEntity
		@Override
		public void onSpeedChanged(float previousSpeed) {
			super.onSpeedChanged(previousSpeed);
			sequencedLimit = -1;
			if (sequenceContext != null && sequenceContext.instruction() == SequencerInstructions.TURN_DISTANCE)
				sequencedLimit = sequenceContext.getEffectiveValue(getTheoreticalSpeed());
		}

		@Override
		protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
			super.write(tag, registries, clientPacket);
			tag.putDouble("SequencedLimit", sequencedLimit);
		}

		@Override
		protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
			super.read(tag, registries, clientPacket);
			sequencedLimit = tag.contains("SequencedLimit") ? tag.getDouble("SequencedLimit") : -1;
		}

		@Override
		public void sendData() {
			if (floor != null)
				floor.sendData();
		}

		@Override
		public void setChanged() {
			if (floor != null)
				floor.setChanged();
		}

		private CompoundTag writeDrive(HolderLookup.Provider registries, boolean clientPacket) {
			CompoundTag tag = new CompoundTag();
			write(tag, registries, clientPacket);
			return tag;
		}

		private void readDrive(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
			read(tag, registries, clientPacket);
		}

	}

}
