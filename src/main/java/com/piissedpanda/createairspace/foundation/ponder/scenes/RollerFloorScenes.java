package com.piissedpanda.createairspace.foundation.ponder.scenes;

import com.piissedpanda.createairspace.block.Cargo.CargoContainers;
import com.piissedpanda.createairspace.block.Cargo.RollerFloorBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;

import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class RollerFloorScenes {

	public static void rollerFloor(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("roller_floor", "Moving Cargo with Motorized Roller Floors");
		scene.configureBasePlate(0, 0, 5);
		scene.world()
				.showSection(util.select()
						.layer(0), Direction.UP);
		scene.idle(10);

		Selection floors = util.select()
				.fromTo(1, 1, 1, 3, 1, 3);
		BlockPos motorX = util.grid()
				.at(0, 1, 2);
		BlockPos motorZ = util.grid()
				.at(2, 1, 4);
		BlockPos container = util.grid()
				.at(2, 2, 2);

		scene.world()
				.showSection(floors, Direction.DOWN);
		scene.idle(15);
		scene.overlay()
				.showText(100)
				.text("Motorized Roller Floors have shafts on all four sides. West and east belong to one network, north and south to a second, independent one")
				.attachKeyFrame()
				.placeNearTarget()
				.pointAt(util.vector()
						.topOf(2, 1, 2));
		scene.idle(110);

		scene.world()
				.showSection(util.select()
						.position(motorX), Direction.EAST);
		scene.idle(10);
		setSpeed(scene, util, Direction.Axis.X, 32);
		scene.effects()
				.rotationDirectionIndicator(motorX.east());
		scene.overlay()
				.showText(80)
				.text("Joined floors turn together and pass the rotation on, just like shafts")
				.placeNearTarget()
				.pointAt(util.vector()
						.blockSurface(motorX, Direction.EAST));
		scene.idle(90);

		ElementLink<WorldSectionElement> cargo = scene.world()
				.showIndependentSection(util.select()
						.fromTo(2, 2, 2, 2, 3, 2), Direction.DOWN);
		scene.idle(10);
		scene.addInstruction(ps -> {
			CargoContainers.tryForm(ps.getWorld(), container);
			ps.forEach(WorldSectionElement.class, WorldSectionElement::queueRedraw);
		});
		scene.idle(10);
		scene.overlay()
				.showText(90)
				.text("Physics objects like cargo containers are pushed along by the spinning discs. Players, mobs and items stay where they are")
				.attachKeyFrame()
				.placeNearTarget()
				.pointAt(util.vector()
						.topOf(container.above()));
		scene.idle(20);
		scene.world()
				.moveSection(cargo, util.vector()
						.of(0, 0, 1), 20);
		scene.idle(80);

		scene.world()
				.showSection(util.select()
						.position(motorZ), Direction.NORTH);
		scene.idle(10);
		setSpeed(scene, util, Direction.Axis.X, 0);
		setSpeed(scene, util, Direction.Axis.Z, 32);
		scene.overlay()
				.showText(90)
				.text("The second network moves them the other way across. With both turning they travel diagonally")
				.placeNearTarget()
				.pointAt(util.vector()
						.blockSurface(motorZ, Direction.NORTH));
		scene.idle(20);
		scene.world()
				.moveSection(cargo, util.vector()
						.of(-1, 0, 0), 20);
		scene.idle(80);

		scene.overlay()
				.showText(80)
				.text("Unlike Roller Beds they are not slippery, a container stops as soon as the shafts stop")
				.placeNearTarget()
				.pointAt(util.vector()
						.topOf(1, 1, 3));
		setSpeed(scene, util, Direction.Axis.Z, 0);
		scene.idle(90);
	}

	private static void setSpeed(CreateSceneBuilder scene, SceneBuildingUtil util, Direction.Axis axis, float speed) {
		for (int x = 1; x <= 3; x++)
			for (int z = 1; z <= 3; z++)
				scene.world()
						.modifyBlockEntity(util.grid()
								.at(x, 1, z), RollerFloorBlockEntity.class,
								be -> be.getDrive(axis)
										.setSpeed(speed));
	}

}
