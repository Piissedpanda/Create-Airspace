package com.piissedpanda.createairspace.block.Cargo;

import dev.ryanhcode.sable.platform.SableEventPlatform;
import dev.simulated_team.simulated.content.blocks.redstone_magnet.MagnetMap;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

// Container locks and lock points find each other here, Simulated runs its own
// map for the docking connectors the same way
public class CargoDock {

	public static final MagnetMap<CargoMagnet> MAGNETS = new MagnetMap<>();

	public static void register() {
		NeoForge.EVENT_BUS.addListener(LevelTickEvent.Post.class, event -> {
			if (event.getLevel() instanceof ServerLevel level)
				MAGNETS.tick(level);
		});
		SableEventPlatform.INSTANCE
				.onPhysicsTick((physicsSystem, timeStep) -> MAGNETS.physicsTick(timeStep, physicsSystem.getLevel()));
	}

}
