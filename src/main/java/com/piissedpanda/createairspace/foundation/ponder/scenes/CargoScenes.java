package com.piissedpanda.createairspace.foundation.ponder.scenes;

import com.piissedpanda.createairspace.block.Cargo.CargoContainers;
import com.piissedpanda.createairspace.block.Cargo.ContainerLockBlockEntity;
import com.piissedpanda.createairspace.block.Cargo.ContainerPortBlockEntity;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class CargoScenes {

	// Container shapes and dock points, then the dock with lock and ports
	public static void cargoContainer(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("cargo_container", "Cargo Containers, Locks and Ports");
		scene.configureBasePlate(0, 0, 9);
		scene.world()
				.showSection(util.select()
						.layer(0), Direction.UP);
		scene.idle(10);

		BlockPos ld2 = util.grid()
				.at(1, 1, 1);
		BlockPos ld3 = util.grid()
				.at(3, 1, 1);
		BlockPos ld11 = util.grid()
				.at(6, 1, 1);
		Selection shapes = util.select()
				.fromTo(1, 1, 1, 8, 2, 2);

		scene.world()
				.showSection(util.select()
						.fromTo(1, 1, 1, 1, 2, 1), Direction.DOWN);
		scene.idle(5);
		scene.world()
				.showSection(util.select()
						.fromTo(3, 1, 1, 4, 2, 2), Direction.DOWN);
		scene.idle(5);
		scene.world()
				.showSection(util.select()
						.fromTo(6, 1, 1, 8, 2, 2), Direction.DOWN);
		scene.idle(15);

		scene.overlay()
				.showText(80)
				.text("Cargo Container blocks join into a container once they form a ULD shape")
				.attachKeyFrame()
				.placeNearTarget()
				.pointAt(util.vector()
						.topOf(ld3.above()));
		scene.idle(40);
		form(scene, ld2);
		form(scene, ld3);
		form(scene, ld11);
		scene.idle(50);

		scene.overlay()
				.showText(90)
				.text("1x1x2 is an LD2, 2x2x2 an LD3 with its contour, 3x2x2 an LD11 and 3x3x3 an AMA. Each block holds as much as an Item Vault")
				.placeNearTarget()
				.pointAt(util.vector()
						.blockSurface(ld11.above(), Direction.NORTH));
		scene.idle(100);

		scene.overlay()
				.showText(90)
				.text("The dock side faces you when you place the last block. It shows the lock point and the port points")
				.attachKeyFrame()
				.placeNearTarget()
				.pointAt(util.vector()
						.blockSurface(ld11.east(), Direction.NORTH));
		scene.idle(100);

		scene.overlay()
				.showControls(util.vector()
						.blockSurface(ld3.above(), Direction.NORTH), Pointing.RIGHT, 40)
				.rightClick()
				.withItem(AllItems.WRENCH.asStack());
		scene.idle(7);
		scene.addInstruction(ps -> {
			CargoContainers.turn(ps.getWorld(), ld3);
			ps.forEach(WorldSectionElement.class, WorldSectionElement::queueRedraw);
		});
		scene.overlay()
				.showText(70)
				.text("A Wrench turns the dock side, and the contour of an LD3 with it")
				.placeNearTarget()
				.pointAt(util.vector()
						.topOf(ld3.above()));
		scene.idle(80);

		scene.overlay()
				.showText(90)
				.text("A Physics Assembler takes the whole container as one piece, no glue needed")
				.attachKeyFrame()
				.placeNearTarget()
				.pointAt(util.vector()
						.topOf(ld11.above()));
		scene.idle(100);

		scene.world()
				.hideSection(shapes, Direction.UP);
		scene.idle(20);

		// The dock
		BlockPos lock = util.grid()
				.at(3, 1, 5);
		BlockPos cargo = util.grid()
				.at(3, 2, 5);
		BlockPos leftPort = util.grid()
				.at(2, 2, 4);
		BlockPos rightPort = util.grid()
				.at(4, 2, 4);

		scene.world()
				.showSection(util.select()
						.fromTo(1, 1, 5, 5, 1, 6), Direction.DOWN);
		scene.idle(15);
		scene.overlay()
				.showText(80)
				.text("Roller Beds let containers and other physics objects slide, players keep their grip")
				.attachKeyFrame()
				.placeNearTarget()
				.pointAt(util.vector()
						.topOf(util.grid()
								.at(1, 1, 6)));
		scene.idle(90);

		scene.overlay()
				.showText(80)
				.text("A Container Lock fits into the floor or the wall")
				.placeNearTarget()
				.pointAt(util.vector()
						.topOf(lock));
		scene.idle(90);

		scene.world()
				.showSection(util.select()
						.fromTo(2, 2, 5, 4, 3, 6), Direction.DOWN);
		scene.idle(5);
		form(scene, cargo);
		scene.idle(20);

		scene.overlay()
				.showText(100)
				.text("It pulls in the lock point of an assembled container like a magnet, lines it up and grips it. Nothing pulls it off again")
				.attachKeyFrame()
				.placeNearTarget()
				.pointAt(util.vector()
						.topOf(lock));
		scene.world()
				.modifyBlockEntity(lock, ContainerLockBlockEntity.class, be -> be.showLocked(true));
		scene.idle(110);

		scene.overlay()
				.showText(80)
				.text("A redstone signal at the lock lets go of the container")
				.placeNearTarget()
				.pointAt(util.vector()
						.topOf(lock));
		scene.idle(90);

		scene.world()
				.showSection(util.select()
						.fromTo(1, 2, 4, 5, 2, 4), Direction.SOUTH);
		scene.idle(15);
		scene.world()
				.modifyBlockEntity(leftPort, ContainerPortBlockEntity.class, be -> be.showConnected(true));
		scene.world()
				.modifyBlockEntity(rightPort, ContainerPortBlockEntity.class, be -> be.showConnected(true));
		scene.overlay()
				.showText(100)
				.text("Container Ports in the wall connect to the port points of a locked container in front of them")
				.attachKeyFrame()
				.placeNearTarget()
				.pointAt(util.vector()
						.blockSurface(leftPort, Direction.NORTH));
		scene.idle(110);

		scene.overlay()
				.showText(100)
				.text("Like a Portable Storage Interface, funnels, chutes and other logistics at the port then fill or empty the container")
				.placeNearTarget()
				.pointAt(util.vector()
						.blockSurface(rightPort, Direction.NORTH));
		scene.idle(110);
	}

	private static void form(CreateSceneBuilder scene, BlockPos pos) {
		scene.addInstruction(ps -> {
			CargoContainers.tryForm(ps.getWorld(), pos);
			ps.forEach(WorldSectionElement.class, WorldSectionElement::queueRedraw);
		});
	}

}
