package com.piissedpanda.createairspace.block.TerminalLight;

import com.piissedpanda.createairspace.CALang;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.DyedBlockList;
import com.simibubi.create.foundation.utility.BlockHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

// Off without a signal, redstone dims it up to full. Right-click or a wrench switches it to always on.
public abstract class TerminalLightBlock extends Block implements IWrenchable {

	public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 15);
	public static final BooleanProperty ALWAYS_ON = BooleanProperty.create("always_on");

	public final DyeColor color;

	public TerminalLightBlock(Properties properties, DyeColor color) {
		super(properties);
		this.color = color;
		registerDefaultState(defaultBlockState().setValue(LEVEL, 0)
				.setValue(ALWAYS_ON, false));
	}

	// The same kind of light in all 16 colours, for dyeing
	protected abstract DyedBlockList<? extends TerminalLightBlock> variants();

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder.add(LEVEL, ALWAYS_ON));
	}

	protected BlockState withLevel(BlockState state, BlockPlaceContext context) {
		return state.setValue(LEVEL, context.getLevel()
				.getBestNeighborSignal(context.getClickedPos()));
	}

	@Override
	protected void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock,
			BlockPos pNeighborPos, boolean pMovedByPiston) {
		updateLevel(pState, pLevel, pPos);
	}

	private static void updateLevel(BlockState state, Level level, BlockPos pos) {
		if (level.isClientSide)
			return;
		int light = state.getValue(ALWAYS_ON) ? 15 : level.getBestNeighborSignal(pos);
		if (light != state.getValue(LEVEL))
			level.setBlock(pos, state.setValue(LEVEL, light), Block.UPDATE_CLIENTS);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer,
			BlockHitResult pHitResult) {
		toggle(pState, pLevel, pPos, pPlayer);
		return InteractionResult.sidedSuccess(pLevel.isClientSide);
	}

	@Override
	public InteractionResult onWrenched(BlockState state, UseOnContext context) {
		toggle(state, context.getLevel(), context.getClickedPos(), context.getPlayer());
		IWrenchable.playRotateSound(context.getLevel(), context.getClickedPos());
		return InteractionResult.SUCCESS;
	}

	private static void toggle(BlockState state, Level level, BlockPos pos, Player player) {
		if (level.isClientSide)
			return;
		BlockState toggled = state.cycle(ALWAYS_ON);
		level.setBlock(pos, toggled, Block.UPDATE_CLIENTS);
		updateLevel(toggled, level, pos);
		if (player != null)
			CALang.translate(toggled.getValue(ALWAYS_ON) ? "terminal_light.always_on" : "terminal_light.redstone")
					.sendStatus(player);
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
			Player player, InteractionHand hand, BlockHitResult hitResult) {
		DyeColor dye = DyeColor.getColor(stack);
		if (dye == null || dye == color)
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		if (!level.isClientSide)
			level.setBlockAndUpdate(pos, BlockHelper.copyProperties(state, variants().get(dye)
					.getDefaultState()));
		return ItemInteractionResult.SUCCESS;
	}

	// Light colour, a bit brighter than the dye so it reads as light
	public static int tint(DyeColor color) {
		int rgb = color.getTextureDiffuseColor();
		int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
		r += (255 - r) / 3;
		g += (255 - g) / 3;
		b += (255 - b) / 3;
		return 0xFF000000 | r << 16 | g << 8 | b;
	}

	// Blockstate rotations for panels and strips, which are modelled facing down.
	// toWorld maps model sides (north = top, east = right) with the same rotation.
	public static int rotationX(Direction facing) {
		return switch (facing) {
		case DOWN -> 0;
		case UP -> 180;
		default -> 270;
		};
	}

	public static int rotationY(Direction facing) {
		return switch (facing) {
		case DOWN -> 0;
		case UP -> 180;
		default -> ((int) facing.toYRot() + 180) % 360;
		};
	}

	// Where a side of the model (facing down) ends up in the world
	public static Direction toWorld(Direction facing, Direction modelSide) {
		Direction side = modelSide;
		for (int i = 0; i < rotationX(facing) / 90; i++)
			side = rotateX(side);
		for (int i = 0; i < rotationY(facing) / 90; i++)
			side = side.getAxis()
					.isHorizontal() ? side.getClockWise() : side;
		return side;
	}

	// Blockstate rotation around x by 90 degrees: up becomes north
	private static Direction rotateX(Direction side) {
		return switch (side) {
		case UP -> Direction.NORTH;
		case NORTH -> Direction.DOWN;
		case DOWN -> Direction.SOUTH;
		case SOUTH -> Direction.UP;
		default -> side;
		};
	}

}
