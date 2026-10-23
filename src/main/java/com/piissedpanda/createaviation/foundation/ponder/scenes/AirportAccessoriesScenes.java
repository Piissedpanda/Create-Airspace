package com.piissedpanda.createaviation.foundation.ponder.scenes;

import com.piissedpanda.createaviation.CABlocks;
import com.piissedpanda.createaviation.block.BarrierPost.BarrierPostBlockEntity;
import com.piissedpanda.createaviation.block.TrafficCone.TrafficConeBlock;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import com.simibubi.create.foundation.utility.BlockHelper;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
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

	public static void trafficCone(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("traffic_cone", "Placing Traffic Cones");
		scene.configureBasePlate(0, 0, 5);
		scene.world()
				.showSection(util.select()
						.layer(0), Direction.UP);
		scene.idle(10);

		for (int z = 0; z < 5; z++) {
			scene.world()
					.showSection(util.select()
							.position(3, 1, z), Direction.DOWN);
			scene.idle(4);
		}
		scene.idle(10);

		BlockPos cone = util.grid()
				.at(3, 1, 2);

		scene.overlay()
				.showText(80)
				.text("Traffic Cones mark closed-off parts of taxiways and aprons")
				.attachKeyFrame()
				.placeNearTarget()
				.pointAt(util.vector()
						.topOf(cone));
		scene.idle(90);

		scene.overlay()
				.showControls(util.vector()
						.topOf(cone), Pointing.DOWN, 40)
				.rightClick()
				.withItem(AllItems.WRENCH.asStack());
		scene.idle(10);
		scene.world()
				.modifyBlock(cone, s -> s.setValue(TrafficConeBlock.FACING, s.getValue(TrafficConeBlock.FACING)
						.getClockWise()), false);

		scene.overlay()
				.showText(70)
				.text("Use a Wrench to turn them, or Sneak-click to pick them up")
				.attachKeyFrame()
				.placeNearTarget()
				.pointAt(util.vector()
						.topOf(cone));
		scene.idle(80);
		scene.markAsFinished();
	}

	public static void terminalSeat(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("terminal_seat", "Using Terminal Seats");
		scene.configureBasePlate(0, 0, 5);
		scene.world()
				.showSection(util.select()
						.layer(0), Direction.UP);
		scene.idle(10);
		scene.world()
				.showSection(util.select()
						.fromTo(1, 1, 2, 3, 1, 2), Direction.DOWN);
		scene.idle(20);

		BlockPos middle = util.grid()
				.at(2, 1, 2);

		scene.overlay()
				.showText(70)
				.text("Terminal Seats placed next to each other form a row")
				.attachKeyFrame()
				.placeNearTarget()
				.pointAt(util.vector()
						.topOf(middle));
		scene.idle(80);

		scene.overlay()
				.showControls(util.vector()
						.topOf(middle), Pointing.DOWN, 40)
				.rightClick();
		scene.idle(10);
		scene.overlay()
				.showText(70)
				.text("Right-click a seat to sit down, just like on Create's seats")
				.attachKeyFrame()
				.placeNearTarget()
				.pointAt(util.vector()
						.topOf(middle));
		scene.idle(80);

		scene.overlay()
				.showControls(util.vector()
						.topOf(middle), Pointing.DOWN, 40)
				.rightClick()
				.withItem(new ItemStack(Items.BLUE_DYE));
		scene.idle(10);
		BlockState blueSeat = CABlocks.TERMINAL_SEATS.get(DyeColor.BLUE)
				.getDefaultState();
		for (int x = 1; x <= 3; x++) {
			BlockPos seat = util.grid()
					.at(x, 1, 2);
			scene.world()
					.modifyBlock(seat, s -> BlockHelper.copyProperties(s, blueSeat), false);
		}

		scene.overlay()
				.showText(70)
				.text("Use dye to change their colour")
				.attachKeyFrame()
				.placeNearTarget()
				.pointAt(util.vector()
						.topOf(middle));
		scene.idle(80);
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
