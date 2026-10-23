package com.piissedpanda.createairspace.foundation.ponder.scenes;

import java.util.List;

import com.piissedpanda.createairspace.CABlocks;
import com.piissedpanda.createairspace.block.AirfieldSign.AirfieldSignBlock;
import com.piissedpanda.createairspace.block.AirfieldSign.AirfieldSignBlockEntity;
import com.piissedpanda.createairspace.block.AirportBeacon.AirportBeaconBlock;
import com.piissedpanda.createairspace.block.BarrierPost.BarrierPostBlockEntity;
import com.piissedpanda.createairspace.block.EdgeLight.EdgeLightBlock;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class AirportAccessoriesScenes {

	public static void barrierPost(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("barrier_post", "Connecting Barrier Posts");
		scene.configureBasePlate(0, 0, 5);
		scene.world()
				.showSection(util.select()
						.layer(0), Direction.UP);
		scene.idle(10);

		BlockPos post1 = util.grid()
				.at(0, 1, 3);
		BlockPos post2 = util.grid()
				.at(4, 1, 3);
		BlockPos post3 = util.grid()
				.at(4, 1, 0);

		ItemStack wrench = AllItems.WRENCH.asStack();

		scene.world()
				.showSection(util.select()
						.position(post1), Direction.DOWN);
		scene.idle(5);
		scene.world()
				.showSection(util.select()
						.position(post2), Direction.DOWN);
		scene.idle(20);

		selectPost(scene, util, post1, wrench, 100);
		scene.idle(20);
		selectPost(scene, util, post2, wrench, 80);
		scene.idle(10);
		connection(builder, post1, post2, true);

		scene.overlay()
				.showText(80)
				.text("Right-click two Barrier Posts with a Wrench to connect them")
				.attachKeyFrame()
				.placeNearTarget()
				.pointAt(beltMiddle(post1, post2));
		scene.idle(90);

		scene.overlay()
				.showText(60)
				.text("The distance between two posts is limited")
				.placeNearTarget()
				.pointAt(beltMiddle(post1, post2));
		scene.idle(70);

		scene.world()
				.showSection(util.select()
						.position(post3), Direction.DOWN);
		scene.idle(15);
		connection(builder, post2, post3, true);
		scene.overlay()
				.showText(70)
				.text("A post can hold multiple connections, which is great for queue lines")
				.attachKeyFrame()
				.placeNearTarget()
				.pointAt(util.vector()
						.topOf(post2));
		scene.idle(80);

		Vec3 belt1 = beltMiddle(post1, post2);
		scene.overlay()
				.showControls(belt1, Pointing.DOWN, 40)
				.rightClick()
				.withItem(new ItemStack(Items.RED_DYE));
		scene.idle(10);
		color(builder, post1, post2, DyeColor.RED);

		Vec3 belt2 = beltMiddle(post2, post3);
		scene.overlay()
				.showControls(belt2, Pointing.DOWN, 40)
				.rightClick()
				.withItem(new ItemStack(Items.YELLOW_DYE));
		scene.idle(10);
		color(builder, post2, post3, DyeColor.YELLOW);

		scene.overlay()
				.showText(70)
				.text("Right-click a belt with a dye to change its colour")
				.attachKeyFrame()
				.placeNearTarget()
				.pointAt(belt1);
		scene.idle(80);

		scene.overlay()
				.showControls(belt2, Pointing.DOWN, 40)
				.rightClick()
				.whileSneaking()
				.withItem(wrench);
		scene.idle(10);
		connection(builder, post2, post3, false);

		scene.overlay()
				.showText(70)
				.text("Sneak-right-click a belt with a Wrench to remove it")
				.attachKeyFrame()
				.placeNearTarget()
				.pointAt(belt2);
		scene.idle(80);
		scene.markAsFinished();
	}

	public static void airfieldSign(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("airfield_sign", "Writing on Airfield Signs");
		scene.configureBasePlate(0, 0, 5);
		scene.world()
				.showSection(util.select()
						.layer(0), Direction.UP);
		scene.idle(10);
		scene.world()
				.showSection(util.select()
						.fromTo(1, 1, 3, 3, 1, 3), Direction.DOWN);
		scene.idle(20);

		BlockPos first = util.grid()
				.at(1, 1, 3);
		BlockPos middle = util.grid()
				.at(2, 1, 3);

		ItemStack nameTag = new ItemStack(Items.NAME_TAG);
		nameTag.set(DataComponents.CUSTOM_NAME, Component.literal("A3"));
		scene.overlay()
				.showControls(util.vector()
						.blockSurface(middle, Direction.SOUTH), Pointing.DOWN, 40)
				.rightClick()
				.withItem(nameTag);
		scene.idle(10);
		scene.world()
				.modifyBlockEntity(first, AirfieldSignBlockEntity.class, be -> be.setText("A3"));

		scene.overlay()
				.showText(80)
				.text("Right-click with a named Name Tag or a Clipboard to write on a row of signs")
				.attachKeyFrame()
				.placeNearTarget()
				.pointAt(util.vector()
						.topOf(middle));
		scene.idle(90);

		scene.overlay()
				.showText(70)
				.text("Display Links can write on them as well")
				.placeNearTarget()
				.pointAt(util.vector()
						.topOf(middle));
		scene.idle(80);

		scene.overlay()
				.showControls(util.vector()
						.blockSurface(middle, Direction.SOUTH), Pointing.DOWN, 40)
				.rightClick()
				.withItem(AllItems.WRENCH.asStack());
		scene.idle(10);
		for (int x = 1; x <= 3; x++) {
			BlockPos sign = util.grid()
					.at(x, 1, 3);
			scene.world()
					.modifyBlock(sign, s -> s.setValue(AirfieldSignBlock.STYLE, AirfieldSignBlock.Style.DIRECTION),
							false);
		}

		scene.overlay()
				.showText(80)
				.text("Use a Wrench on the front to switch between location, direction and mandatory signs")
				.attachKeyFrame()
				.placeNearTarget()
				.pointAt(util.vector()
						.topOf(middle));
		scene.idle(90);
		scene.markAsFinished();
	}

	public static void edgeLight(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("edge_light", "Powering Edge Lights");
		scene.configureBasePlate(0, 0, 5);
		scene.world()
				.showSection(util.select()
						.layer(0), Direction.UP);
		scene.idle(10);
		scene.world()
				.showSection(util.select()
						.fromTo(0, 1, 1, 4, 1, 1), Direction.DOWN);
		scene.idle(20);
		scene.world()
				.showSection(util.select()
						.fromTo(0, 1, 2, 4, 1, 3), Direction.DOWN);
		scene.idle(20);

		BlockPos lever = util.grid()
				.at(2, 1, 3);
		BlockPos middle = util.grid()
				.at(2, 1, 1);

		scene.overlay()
				.showControls(util.vector()
						.topOf(lever), Pointing.DOWN, 30)
				.rightClick();
		scene.idle(10);
		scene.world()
				.toggleRedstonePower(util.select()
						.fromTo(0, 1, 1, 4, 1, 3));
		scene.effects()
				.indicateRedstone(lever);

		scene.overlay()
				.showText(80)
				.text("Edge Lights light up when they receive a redstone signal")
				.attachKeyFrame()
				.placeNearTarget()
				.pointAt(util.vector()
						.topOf(middle));
		scene.idle(90);

		scene.overlay()
				.showText(70)
				.text("Redstone dust connects to them, so one line can power a whole row")
				.placeNearTarget()
				.pointAt(util.vector()
						.topOf(1, 1, 2));
		scene.idle(80);

		scene.overlay()
				.showControls(util.vector()
						.topOf(middle), Pointing.DOWN, 40)
				.rightClick()
				.withItem(new ItemStack(Items.GREEN_DYE));
		scene.idle(10);
		scene.world()
				.modifyBlock(middle, s -> s.setValue(EdgeLightBlock.COLOR, EdgeLightBlock.LightColor.GREEN), false);

		scene.overlay()
				.showText(90)
				.text("Use dye to change the colour: white, blue, green, red or yellow")
				.attachKeyFrame()
				.placeNearTarget()
				.pointAt(util.vector()
						.topOf(middle));
		scene.idle(100);
		scene.markAsFinished();
	}

	public static void windsock(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("windsock", "Reading the Wind");
		scene.configureBasePlate(0, 0, 5);
		scene.world()
				.showSection(util.select()
						.layer(0), Direction.UP);
		scene.idle(10);

		BlockPos windsock = util.grid()
				.at(2, 1, 2);
		scene.world()
				.showSection(util.select()
						.position(windsock), Direction.DOWN);
		scene.idle(20);

		scene.overlay()
				.showText(90)
				.text("Windsocks always point in the direction the wind is blowing")
				.attachKeyFrame()
				.placeNearTarget()
				.pointAt(util.vector()
						.of(2.5, 2.7, 2.5));
		scene.idle(100);

		scene.overlay()
				.showText(100)
				.text("The stronger the wind, the higher the sock flies. Rain, thunderstorms and height make it stronger")
				.attachKeyFrame()
				.placeNearTarget()
				.pointAt(util.vector()
						.of(2.5, 2.7, 2.5));
		scene.idle(110);
		scene.markAsFinished();
	}

	public static void airportBeacon(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("airport_beacon", "Running Airport Beacons");
		scene.configureBasePlate(0, 0, 5);
		scene.world()
				.showSection(util.select()
						.layer(0), Direction.UP);
		scene.idle(10);

		BlockPos motor = util.grid()
				.at(2, 1, 2);
		BlockPos beacon = util.grid()
				.at(2, 2, 2);
		BlockPos lever = util.grid()
				.at(3, 2, 2);
		scene.world()
				.showSection(util.select()
						.position(motor), Direction.DOWN);
		scene.idle(10);
		scene.world()
				.showSection(util.select()
						.position(beacon), Direction.DOWN);
		scene.idle(20);

		scene.world()
				.setKineticSpeed(util.select()
						.fromTo(2, 1, 2, 2, 2, 2), 16);
		scene.effects()
				.rotationSpeedIndicator(motor);
		scene.overlay()
				.showText(70)
				.text("Rotational force from below turns the head of the beacon")
				.attachKeyFrame()
				.placeNearTarget()
				.pointAt(util.vector()
						.blockSurface(beacon, Direction.WEST));
		scene.idle(80);

		scene.world()
				.showSection(util.select()
						.fromTo(3, 1, 2, 3, 2, 2), Direction.DOWN);
		scene.idle(15);
		scene.overlay()
				.showControls(util.vector()
						.topOf(lever), Pointing.DOWN, 30)
				.rightClick();
		scene.idle(10);
		scene.world()
				.toggleRedstonePower(util.select()
						.fromTo(2, 2, 2, 3, 2, 2));
		scene.effects()
				.indicateRedstone(lever);
		scene.overlay()
				.showText(90)
				.text("The light is controlled by redstone from the sides. Stronger signals make it brighter and reach further")
				.attachKeyFrame()
				.placeNearTarget()
				.pointAt(util.vector()
						.blockSurface(beacon, Direction.EAST));
		scene.idle(100);

		scene.overlay()
				.showText(110)
				.text("Use a Wrench to switch between the patterns of land airports, water airports, heliports and military airports")
				.attachKeyFrame()
				.placeNearTarget()
				.pointAt(util.vector()
						.topOf(beacon));
		for (AirportBeaconBlock.Mode mode : List.of(AirportBeaconBlock.Mode.WATER, AirportBeaconBlock.Mode.HELIPORT,
				AirportBeaconBlock.Mode.MILITARY, AirportBeaconBlock.Mode.LAND)) {
			scene.overlay()
					.showControls(util.vector()
							.topOf(beacon), Pointing.DOWN, 20)
					.rightClick()
					.withItem(AllItems.WRENCH.asStack());
			scene.idle(5);
			scene.world()
					.modifyBlock(beacon, s -> s.setValue(AirportBeaconBlock.MODE, mode), false);
			scene.idle(35);
		}
		scene.markAsFinished();
	}

	private static void selectPost(CreateSceneBuilder scene, SceneBuildingUtil util, BlockPos pos, ItemStack wrench,
			int duration) {
		scene.overlay()
				.showControls(util.vector()
						.topOf(pos), Pointing.DOWN, duration)
				.rightClick()
				.withItem(wrench);

		Vec3 center = util.vector()
				.centerOf(pos);
		AABB bb = new AABB(center, center);
		scene.overlay()
				.chaseBoundingBoxOutline(PonderPalette.GREEN, pos, bb, 10);
		scene.idle(1);
		scene.overlay()
				.chaseBoundingBoxOutline(PonderPalette.GREEN, pos, bb.inflate(.5, .5, .5), duration);
	}

	private static Vec3 beltMiddle(BlockPos p1, BlockPos p2) {
		return Vec3.atBottomCenterOf(p1)
				.add(Vec3.atBottomCenterOf(p2))
				.scale(.5)
				.add(0, 18 / 16f, 0);
	}

	private static void connection(SceneBuilder builder, BlockPos p1, BlockPos p2, boolean connect) {
		modifyConnection(builder, p1, p2.subtract(p1), connect);
		modifyConnection(builder, p2, p1.subtract(p2), connect);
	}

	private static void modifyConnection(SceneBuilder builder, BlockPos pos, BlockPos target, boolean connect) {
		builder.world()
				.modifyBlockEntity(pos, BarrierPostBlockEntity.class, be -> {
					if (connect)
						be.connections.add(target);
					else {
						be.connections.remove(target);
						be.beltColors.remove(target);
					}
					be.connectionStats = null;
					be.prepareStats();
					be.markVisualDirty();
				});
	}

	private static void color(SceneBuilder builder, BlockPos p1, BlockPos p2, DyeColor color) {
		builder.world()
				.modifyBlockEntity(p1, BarrierPostBlockEntity.class, be -> be.beltColors.put(p2.subtract(p1), color));
		builder.world()
				.modifyBlockEntity(p2, BarrierPostBlockEntity.class, be -> be.beltColors.put(p1.subtract(p2), color));
	}

}
