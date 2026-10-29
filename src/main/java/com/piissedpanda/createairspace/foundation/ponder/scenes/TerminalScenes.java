package com.piissedpanda.createairspace.foundation.ponder.scenes;

import java.util.List;

import com.piissedpanda.createairspace.CAItems;
import com.piissedpanda.createairspace.block.CheckIn.LuggageScaleBlockEntity;
import com.piissedpanda.createairspace.block.Luggage.LuggageType;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class TerminalScenes {

	public static void luggageScale(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("luggage_scale", "Weighing Luggage");
		scene.configureBasePlate(0, 0, 5);
		scene.world()
				.showSection(util.select()
						.layer(0), Direction.UP);
		scene.idle(10);

		BlockPos scale = util.grid()
				.at(2, 1, 2);
		BlockPos inputBelt = util.grid()
				.at(0, 1, 2);
		Selection belts = util.select()
				.fromTo(0, 1, 2, 1, 1, 2)
				.add(util.select()
						.fromTo(3, 1, 2, 4, 1, 2));
		Selection motors = util.select()
				.position(0, 1, 1)
				.add(util.select()
						.position(4, 1, 1));
		Selection redstone = util.select()
				.fromTo(2, 1, 0, 2, 1, 1);

		ItemStack light = luggage(DyeColor.BLUE, new ItemStack(Items.WHITE_WOOL, 6), new ItemStack(Items.STRING, 32));
		ItemStack heavy = luggage(DyeColor.RED, new ItemStack(Items.STONE_BRICKS, 32));

		scene.world()
				.showSection(util.select()
						.position(scale), Direction.DOWN);
		scene.idle(15);

		scene.overlay()
				.showControls(util.vector()
						.topOf(scale), Pointing.DOWN, 40)
				.rightClick()
				.withItem(light);
		scene.idle(7);
		scene.world()
				.modifyBlockEntity(scale, LuggageScaleBlockEntity.class, be -> be.placeByHand(light, Direction.SOUTH));
		scene.overlay()
				.showText(80)
				.text("Right-click with a piece of luggage to put it on the scale. The screen shows its weight")
				.attachKeyFrame()
				.placeNearTarget()
				.pointAt(util.vector()
						.blockSurface(scale, Direction.SOUTH));
		scene.idle(90);

		scene.overlay()
				.showText(80)
				.text("Blocks inside count with their mass, other items by how full their stack is")
				.placeNearTarget()
				.pointAt(util.vector()
						.topOf(scale));
		scene.idle(90);

		scene.overlay()
				.showControls(util.vector()
						.topOf(scale), Pointing.DOWN, 40)
				.rightClick()
				.whileSneaking();
		scene.idle(7);
		scene.world()
				.modifyBlockEntity(scale, LuggageScaleBlockEntity.class, LuggageScaleBlockEntity::takeLuggage);
		scene.overlay()
				.showText(60)
				.text("Sneak and right-click with an empty hand to take it off again")
				.placeNearTarget()
				.pointAt(util.vector()
						.topOf(scale));
		scene.idle(70);

		// Belt line through the scale
		scene.world()
				.setKineticSpeed(belts.add(motors), -32);
		scene.world()
				.showSection(motors, Direction.DOWN);
		scene.idle(5);
		scene.world()
				.showSection(belts, Direction.DOWN);
		scene.idle(15);

		scene.overlay()
				.showText(80)
				.text("Placed between belts, luggage rolls across and stops in the middle to be weighed")
				.attachKeyFrame()
				.placeNearTarget()
				.pointAt(util.vector()
						.topOf(scale));
		scene.world()
				.createItemOnBelt(inputBelt, Direction.NORTH, light);
		scene.idle(90);
		scene.world()
				.createItemOnBelt(inputBelt, Direction.NORTH, heavy);
		scene.idle(30);

		scene.overlay()
				.showText(70)
				.text("Above 23 kg the weight turns red")
				.placeNearTarget()
				.pointAt(util.vector()
						.blockSurface(scale, Direction.SOUTH));
		scene.idle(80);

		// Comparator reading a suitcase put on by hand
		scene.world()
				.showSection(redstone, Direction.DOWN);
		scene.idle(15);
		scene.world()
				.modifyBlockEntity(scale, LuggageScaleBlockEntity.class, be -> be.placeByHand(heavy, Direction.SOUTH));
		scene.world()
				.modifyBlock(util.grid()
						.at(2, 1, 1), s -> s.setValue(BlockStateProperties.POWERED, true), false);
		scene.world()
				.modifyBlock(util.grid()
						.at(2, 1, 0), s -> s.setValue(BlockStateProperties.LIT, true), false);
		scene.overlay()
				.showText(90)
				.text("Comparators read the weight: 0 at the minimum, 15 at the maximum, evenly in between")
				.attachKeyFrame()
				.placeNearTarget()
				.pointAt(util.vector()
						.topOf(util.grid()
								.at(2, 1, 1)));
		scene.idle(100);

		scene.overlay()
				.showControls(util.vector()
						.blockSurface(scale, Direction.SOUTH), Pointing.DOWN, 40)
				.rightClick();
		scene.idle(7);
		scene.overlay()
				.showText(80)
				.text("Right-click with an empty hand to set the minimum and maximum weight")
				.placeNearTarget()
				.pointAt(util.vector()
						.blockSurface(scale, Direction.SOUTH));
		scene.idle(90);
		scene.markAsFinished();
	}

	private static ItemStack luggage(DyeColor color, ItemStack... contents) {
		ItemStack stack = CAItems.LUGGAGE.get(LuggageType.HARDSHELL_SUITCASE)
				.get(color)
				.asStack();
		stack.set(AllDataComponents.PACKAGE_CONTENTS, ItemContainerContents.fromItems(List.of(contents)));
		return stack;
	}

}
