package com.piissedpanda.createairspace.block.Cargo;

import org.joml.Quaterniond;
import org.joml.Vector3d;

import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.content.blocks.redstone_magnet.MagnetPair;
import dev.simulated_team.simulated.content.blocks.redstone_magnet.SimMagnet;
import dev.simulated_team.simulated.service.SimConfigService;
import dev.simulated_team.simulated.util.SimMathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

// A lock (first) pulling in the lock point of a container (second). The forces and
// the alignment are adapted from Simulated's DockingConnectorPair
// (MIT License, Copyright (c) The Simulated Team / The Creators of Aeronautics)
public class CargoDockPair extends MagnetPair<CargoMagnet> {

	private final Vector3d v = new Vector3d();
	private final Quaterniond orientation1 = new Quaterniond();
	private final Quaterniond orientation2 = new Quaterniond();
	private final Quaterniond relativeBlockOrientation = new Quaterniond();
	// Lock and container lined up on the 90 degree grid, set by orientationDifference
	final Quaterniond target = new Quaterniond();

	public CargoDockPair(Level level, BlockPos lock, BlockPos container) {
		super(level, lock, container);
	}

	@Override
	public void tick() {
		super.tick();
		if (level.getBlockEntity(blockPos1) instanceof ContainerLockBlockEntity lock
				&& level.getBlockEntity(blockPos2) instanceof CargoContainerBlockEntity container)
			lock.tryDock(this, container);
	}

	// Close enough and lined up well enough to grab it
	boolean inReach(ContainerLockBlockEntity lock, CargoContainerBlockEntity container) {
		Vector3d turn = orientationDifference(lock, container, new Vector3d());
		Vector3d offset = getRelativePosition(lock, container, new Vector3d());
		double angle = Math.toRadians(SimConfigService.INSTANCE.server().physics.dockingConnectorAngleTolerance.get());
		double distance = SimConfigService.INSTANCE.server().physics.dockingConnectorDistanceTolerance.get();
		return turn.lengthSquared() < angle * angle && offset.lengthSquared() < distance * distance;
	}

	@Override
	protected boolean canConnect(Vector3d relativePosition, Vector3d moment1, Vector3d moment2) {
		return relativePosition.lengthSquared() < ContainerLockBlockEntity.REACH * ContainerLockBlockEntity.REACH
				&& moment1.dot(moment2) < 0;
	}

	@Override
	protected double forceDistanceScale(double distance, CargoMagnet magnet1, CargoMagnet magnet2) {
		distance = 1 + Math.max(0, distance - 0.25);
		distance *= distance;
		distance *= distance;
		return 4 / distance;
	}

	@Override
	protected double torqueDistanceScale(double distance, CargoMagnet magnet1, CargoMagnet magnet2) {
		distance = 1 + Math.max(0, distance - 0.5);
		return 1 / (distance * distance);
	}

	@Override
	protected Vector3d getForce(PairData data, Vector3d f) {
		// Magnet-like force
		f.set(data.moment2())
				.mul(-0.5 * data.moment1()
						.dot(data.relativePosition()));
		f.fma(-0.5 * data.moment2()
				.dot(data.relativePosition()), data.moment1());
		f.fma(data.moment1()
				.dot(data.moment2()), data.relativePosition());
		f.fma(3 * (data.moment1()
				.dot(data.relativePosition())
				* data.moment2()
						.dot(data.relativePosition()))
				/ (data.relativePosition()
						.lengthSquared() + 1E-12),
				data.relativePosition());
		return f.mul(data.forceScale() / 3);
	}

	@Override
	protected Vector3d getTorque(PairData data, Vector3d t) {
		data.moment2()
				.cross(data.moment1(), t);
		return t.mul(data.torqueScale());
	}

	@Override
	protected Vector3d getSymmetricTorque(PairData data, Vector3d t) {
		orientationDifference(data.magnet1(), data.magnet2(), t);
		v.set(data.moment1())
				.sub(data.moment2())
				.div(2);
		v.mul(v.dot(t), t);
		return t.mul(data.torqueScale());
	}

	@Override
	public double getDampingRatio() {
		return 0.8;
	}

	@Override
	public double getAccelerationLimit() {
		return SimConfigService.INSTANCE.server().physics.dockingConnectorLinearAccelerationClamping.get();
	}

	@Override
	public double getAngularAccelerationLimit() {
		return SimConfigService.INSTANCE.server().physics.dockingConnectorAngularAccelerationClamping.get();
	}

	Vector3d orientationDifference(SimMagnet magnet1, SimMagnet magnet2, Vector3d t) {
		SubLevel subLevel1 = magnet1.getLatestSubLevel();
		SubLevel subLevel2 = magnet2.getLatestSubLevel();

		orientation1.set(subLevel1 == null ? orientation1.identity()
				: subLevel1.logicalPose()
						.orientation());
		orientation2.set(subLevel2 == null ? orientation2.identity()
				: subLevel2.logicalPose()
						.orientation());

		Quaterniond blockOrientation1 = new Quaterniond(magnet1.getOrientation());
		Quaterniond blockOrientation2 = new Quaterniond(magnet2.getOrientation());
		blockOrientation2.premul(orientation2)
				.premul(orientation1.conjugate(new Quaterniond()));
		relativeBlockOrientation.set(blockOrientation1)
				.div(blockOrientation2);

		SimMathUtils.clampQuaternionToGrid(relativeBlockOrientation, SimMathUtils.GridQuats.REAL.opposite(), target);
		relativeBlockOrientation.div(target);

		t.set(relativeBlockOrientation.x, relativeBlockOrientation.y, relativeBlockOrientation.z)
				.mul(2);
		orientation1.transform(t);
		return t;
	}

}
