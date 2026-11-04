package com.piissedpanda.createairspace.block.Cargo;

import java.util.List;

import org.joml.Quaternionfc;
import org.joml.Vector3d;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.content.blocks.redstone_magnet.MagnetBehaviour;
import dev.simulated_team.simulated.content.blocks.redstone_magnet.SimMagnet;
import dev.simulated_team.simulated.service.SimConfigService;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

// Both ends of a container dock: the lock and the lock point of a container. They
// pull each other in with Simulated's magnet physics, like two docking connectors
public abstract class CargoMagnet extends SmartBlockEntity implements SimMagnet {

	public CargoMagnet(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		behaviours.add(new MagnetBehaviour(this, CargoDock.MAGNETS));
	}

	// The face that docks
	public abstract Direction getMagnetFace();

	@Override
	public Quaternionfc getOrientation() {
		return getMagnetFace().getRotation();
	}

	@Override
	public SubLevel getLatestSubLevel() {
		return Sable.HELPER.getContaining(this);
	}

	@Override
	public Vec3 getMagnetPosition() {
		return getFaceCentre();
	}

	public Vec3 getFaceCentre() {
		return Vec3.atCenterOf(worldPosition)
				.add(Vec3.atLowerCornerOf(getMagnetFace().getNormal())
						.scale(0.5));
	}

	// Same strength as Simulated's docking connector
	@Override
	public Vector3d setMagneticMoment(Vector3d v) {
		Direction face = getMagnetFace();
		return v.set(face.getStepX(), face.getStepY(), face.getStepZ())
				.mul(Math.sqrt(SimConfigService.INSTANCE.server().physics.dockingConnectorStrength.get()));
	}

}
