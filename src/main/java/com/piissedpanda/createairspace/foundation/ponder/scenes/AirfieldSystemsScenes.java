package com.piissedpanda.createairspace.foundation.ponder.scenes;

import java.util.List;

import com.piissedpanda.createairspace.CAItems;
import com.piissedpanda.createairspace.block.Luggage.LuggageType;
import com.piissedpanda.createairspace.block.Papi.PapiLightBlockEntity;
import com.piissedpanda.createairspace.block.Security.SecurityScan;
import com.piissedpanda.createairspace.block.StopBar.StopBarBlock;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;

public class AirfieldSystemsScenes {

	public static void papiLight(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("papi_light", "Guiding the Approach with PAPI Lights");
		scene.configureBasePlate(0, 0, 7);
		scene.world()
				.showSection(util.select()
						.layer(0), Direction.UP);
		scene.idle(10);

		Selection lights = util.select()
				.fromTo(1, 1, 2, 4, 1, 2);
		BlockPos lever = util.grid()
				.at(0, 1, 3);
		BlockPos outer = util.grid()
				.at(1, 1, 2);

		scene.world()
				.showSection(lights, Direction.DOWN);
		scene.idle(15);
		scene.overlay()
				.showText(90)
				.text("PAPI Lights stand in a row of four next to the runway, their lenses facing the approaching aircraft")
				.attachKeyFrame()
				.placeNearTarget()
				.pointAt(util.vector()
						.topOf(2, 1, 2));
		scene.idle(100);

		scene.world()
				.showSection(util.select()
						.fromTo(0, 1, 3, 4, 1, 3), Direction.DOWN);
		scene.idle(15);
		scene.overlay()
				.showControls(util.vector()
						.topOf(lever), Pointing.DOWN, 30)
				.rightClick();
		scene.idle(10);
		scene.world()
				.toggleRedstonePower(util.select()
						.fromTo(0, 1, 2, 4, 1, 3));
		scene.effects()
				.indicateRedstone(lever);
		scene.overlay()
				.showText(60)
				.text("They light up while powered by redstone")
				.placeNearTarget()
				.pointAt(util.vector()
						.topOf(lever));
		scene.idle(70);

		viewFrom(scene, util, 5);
		scene.overlay()
				.showText(80)
				.text("Coming in too high, a pilot sees all four lights white")
				.attachKeyFrame()
				.placeNearTarget()
				.pointAt(util.vector()
						.blockSurface(util.grid()
								.at(3, 1, 2), Direction.NORTH));
		scene.idle(90);

		viewFrom(scene, util, 3);
		scene.overlay()
				.showText(90)
				.text("On the glide path, the two lights next to the runway turn red and the other two stay white")
				.placeNearTarget()
				.pointAt(util.vector()
						.blockSurface(util.grid()
								.at(3, 1, 2), Direction.NORTH));
		scene.idle(100);

		viewFrom(scene, util, 1.5f);
		scene.overlay()
				.showText(70)
				.text("Too low, and all four are red")
				.placeNearTarget()
				.pointAt(util.vector()
						.blockSurface(util.grid()
								.at(3, 1, 2), Direction.NORTH));
		scene.idle(80);

		scene.overlay()
				.showText(100)
				.text("Each light changes color at its own angle, set on the back of its housing. A light placed next to a row takes the next lower angle, so start next to the runway")
				.attachKeyFrame()
				.placeNearTarget()
				.pointAt(util.vector()
						.blockSurface(outer, Direction.SOUTH));
		scene.idle(110);

		scene.overlay()
				.showText(80)
				.text("Every player sees the colors of their own position")
				.placeNearTarget()
				.pointAt(util.vector()
						.topOf(2, 1, 2));
		scene.idle(90);
	}

	private static void viewFrom(CreateSceneBuilder scene, SceneBuildingUtil util, float angle) {
		for (int x = 1; x <= 4; x++)
			scene.world()
					.modifyBlockEntity(util.grid()
							.at(x, 1, 2), PapiLightBlockEntity.class, be -> be.ponderViewAngle = angle);
		scene.idle(5);
	}

	public static void stopBar(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("stop_bar", "Holding Aircraft with Stop Bars");
		scene.configureBasePlate(0, 0, 5);
		scene.world()
				.showSection(util.select()
						.layer(0), Direction.UP);
		scene.idle(10);

		Selection bar = util.select()
				.fromTo(0, 1, 2, 4, 1, 2);
		Selection barRedstone = util.select()
				.fromTo(2, 1, 3, 2, 1, 4);
		BlockPos lever = util.grid()
				.at(2, 1, 4);
		BlockPos guardLight = util.grid()
				.at(4, 1, 0);
		BlockPos guardLever = util.grid()
				.at(4, 1, 1);

		scene.world()
				.showSection(bar, Direction.DOWN);
		scene.idle(15);
		scene.overlay()
				.showText(90)
				.text("Stop Bars are red inset lights across a taxiway. While they are lit, aircraft have to hold")
				.attachKeyFrame()
				.placeNearTarget()
				.pointAt(util.vector()
						.topOf(2, 1, 2));
		scene.idle(100);

		scene.world()
				.showSection(barRedstone, Direction.DOWN);
		scene.idle(15);
		scene.overlay()
				.showControls(util.vector()
						.topOf(lever), Pointing.DOWN, 30)
				.rightClick();
		scene.idle(10);
		scene.world()
				.toggleRedstonePower(barRedstone);
		scene.world()
				.modifyBlocks(bar, s -> s.setValue(StopBarBlock.LIT, false), false);
		scene.effects()
				.indicateRedstone(lever);
		scene.overlay()
				.showText(90)
				.text("A redstone signal into any of them switches off the whole bar, and the aircraft may cross")
				.attachKeyFrame()
				.placeNearTarget()
				.pointAt(util.vector()
						.topOf(0, 1, 2));
		scene.idle(100);

		scene.overlay()
				.showControls(util.vector()
						.topOf(lever), Pointing.DOWN, 30)
				.rightClick();
		scene.idle(10);
		scene.world()
				.toggleRedstonePower(barRedstone);
		scene.world()
				.modifyBlocks(bar, s -> s.setValue(StopBarBlock.LIT, true), false);
		scene.idle(30);

		scene.world()
				.showSection(util.select()
						.fromTo(4, 1, 0, 4, 1, 1), Direction.DOWN);
		scene.idle(15);
		scene.overlay()
				.showControls(util.vector()
						.topOf(guardLever), Pointing.DOWN, 30)
				.rightClick();
		scene.idle(10);
		scene.world()
				.toggleRedstonePower(util.select()
						.fromTo(4, 1, 0, 4, 1, 1));
		scene.effects()
				.indicateRedstone(guardLever);
		scene.overlay()
				.showText(90)
				.text("Runway Guard Lights flash yellow in turns while powered, a warning in front of a runway")
				.attachKeyFrame()
				.placeNearTarget()
				.pointAt(util.vector()
						.topOf(guardLight));
		scene.idle(100);
	}

	public static void securityScanner(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("security_scanner", "Screening Luggage and Passengers");
		scene.configureBasePlate(0, 0, 5);
		scene.world()
				.showSection(util.select()
						.layer(0), Direction.UP);
		scene.idle(10);

		BlockPos inputBelt = util.grid()
				.at(0, 1, 2);
		BlockPos scanner = util.grid()
				.at(1, 2, 2);
		Selection line = util.select()
				.fromTo(0, 1, 1, 2, 1, 2);
		Selection detector = util.select()
				.fromTo(3, 1, 4, 3, 2, 4);

		scene.world()
				.setKineticSpeed(line, -32);
		scene.world()
				.showSection(line, Direction.DOWN);
		scene.idle(15);
		scene.world()
				.showSection(util.select()
						.position(scanner), Direction.DOWN);
		scene.idle(15);
		scene.overlay()
				.showText(90)
				.text("The Baggage Scanner sits on top of a belt or depot and screens everything passing underneath")
				.attachKeyFrame()
				.placeNearTarget()
				.pointAt(util.vector()
						.topOf(scanner));
		scene.idle(100);

		scene.world()
				.createItemOnBelt(inputBelt, Direction.NORTH,
						luggage(DyeColor.BLUE, new ItemStack(Items.WHITE_WOOL, 6), new ItemStack(Items.BOOK)));
		scene.overlay()
				.showText(70)
				.text("Luggage and packages are screened together with their contents")
				.placeNearTarget()
				.pointAt(util.vector()
						.blockSurface(scanner, Direction.NORTH));
		scene.idle(80);
		scene.world()
				.removeItemsFromBelt(util.grid()
						.at(2, 1, 2));
		scene.idle(10);

		scene.world()
				.createItemOnBelt(inputBelt, Direction.NORTH,
						luggage(DyeColor.RED, new ItemStack(Items.IRON_SWORD), new ItemStack(Items.STRING, 8)));
		scene.idle(22);
		alarm(scene, util.select()
				.position(scanner), scanner, true);
		scene.overlay()
				.showText(90)
				.text("If it finds weapons or metal, it beeps and gives off a redstone signal for two seconds")
				.attachKeyFrame()
				.placeNearTarget()
				.pointAt(util.vector()
						.blockSurface(scanner, Direction.NORTH));
		scene.idle(40);
		alarm(scene, util.select()
				.position(scanner), scanner, false);
		scene.idle(60);

		scene.overlay()
				.showText(80)
				.text("Sorting the luggage out is left to Create, for example with a brass tunnel or a funnel")
				.placeNearTarget()
				.pointAt(util.vector()
						.topOf(2, 1, 2));
		scene.idle(90);

		scene.world()
				.showSection(detector, Direction.DOWN);
		scene.idle(15);
		scene.overlay()
				.showText(90)
				.text("The Metal Detector checks everyone who walks through it, with their whole inventory")
				.attachKeyFrame()
				.placeNearTarget()
				.pointAt(util.vector()
						.blockSurface(util.grid()
								.at(3, 2, 4), Direction.NORTH));
		scene.idle(100);
		alarm(scene, detector, util.grid()
				.at(3, 1, 4), true);
		scene.overlay()
				.showText(90)
				.text("Both react to the item tag createairspace:security_alarm, which datapacks can extend")
				.placeNearTarget()
				.pointAt(util.vector()
						.blockSurface(util.grid()
								.at(3, 2, 4), Direction.NORTH));
		scene.idle(40);
		alarm(scene, detector, util.grid()
				.at(3, 1, 4), false);
		scene.idle(60);
	}

	private static void alarm(CreateSceneBuilder scene, Selection blocks, BlockPos pos, boolean alarm) {
		scene.world()
				.modifyBlocks(blocks, s -> s.setValue(SecurityScan.ALARM, alarm), false);
		if (alarm)
			scene.effects()
					.indicateRedstone(pos);
	}

	private static ItemStack luggage(DyeColor color, ItemStack... contents) {
		ItemStack stack = CAItems.LUGGAGE.get(LuggageType.HARDSHELL_SUITCASE)
				.get(color)
				.asStack();
		stack.set(AllDataComponents.PACKAGE_CONTENTS, ItemContainerContents.fromItems(List.of(contents)));
		return stack;
	}

}
