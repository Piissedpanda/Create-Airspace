package com.piissedpanda.createairspace.block.RunwayMarking;

import java.util.Locale;

import com.piissedpanda.createairspace.CABlocks;
import com.piissedpanda.createairspace.CALang;
import com.piissedpanda.createairspace.CAShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.utility.BlockHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RunwayMarkingBlock extends Block implements IWrenchable {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<Pattern> PATTERN = EnumProperty.create("pattern", Pattern.class);

	public RunwayMarkingBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH)
				.setValue(PATTERN, Pattern.CENTER_LINE));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder.add(FACING, PATTERN));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		Direction facing = pContext.getHorizontalDirection();
		BlockState state = defaultBlockState().setValue(FACING, facing);

		// Continue the pattern of a marking next to it, the one behind the player first
		BlockPos pos = pContext.getClickedPos();
		for (Direction side : new Direction[] { facing.getOpposite(), facing, facing.getClockWise(),
				facing.getCounterClockWise() }) {
			BlockState neighbour = pContext.getLevel()
					.getBlockState(pos.relative(side));
			if (neighbour.getBlock() instanceof RunwayMarkingBlock)
				return state.setValue(PATTERN, neighbour.getValue(PATTERN));
		}
		return state;
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
			Player player, InteractionHand hand, BlockHitResult hitResult) {
		DyeColor dye = DyeColor.getColor(stack);
		if (dye == null)
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		Block target = switch (dye) {
		case WHITE -> CABlocks.WHITE_RUNWAY_MARKING.get();
		case YELLOW -> CABlocks.YELLOW_RUNWAY_MARKING.get();
		default -> null;
		};
		if (target == null || target == this)
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

		if (!level.isClientSide)
			level.setBlockAndUpdate(pos, BlockHelper.copyProperties(state, target.defaultBlockState()));
		return ItemInteractionResult.SUCCESS;
	}

	// The wrench switches the pattern, the direction comes from placing
	@Override
	public InteractionResult onWrenched(BlockState state, UseOnContext context) {
		Level level = context.getLevel();
		Pattern pattern = state.getValue(PATTERN)
				.next();
		Player player = context.getPlayer();

		if (!level.isClientSide)
			level.setBlock(context.getClickedPos(), state.setValue(PATTERN, pattern), 3);
		else if (player != null)
			CALang.translate("runway_marking.pattern", CALang.translate("runway_marking.pattern." + pattern
					.getSerializedName())
					.component())
					.sendStatus(player);
		IWrenchable.playRotateSound(level, context.getClickedPos());
		return InteractionResult.SUCCESS;
	}

	@Override
	protected boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
		BlockPos below = pPos.below();
		return pLevel.getBlockState(below)
				.isFaceSturdy(pLevel, below, Direction.UP);
	}

	@Override
	public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState,
			LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
		if (pDirection == Direction.DOWN && !pState.canSurvive(pLevel, pPos))
			return Blocks.AIR.defaultBlockState();
		return pState;
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return CAShapes.RUNWAY_MARKING;
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos,
			CollisionContext pContext) {
		return Shapes.empty();
	}

	@Override
	public BlockState rotate(BlockState pState, Rotation pRotation) {
		return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState pState, Mirror pMirror) {
		return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
	}

	public enum Pattern implements StringRepresentable {
		CENTER_LINE, EDGE_LINE, DOUBLE_LINE, BAR, FULL, HOLDING_POSITION, CURVE, DIAGONAL;

		public Pattern next() {
			return values()[(ordinal() + 1) % values().length];
		}

		@Override
		public String getSerializedName() {
			return name().toLowerCase(Locale.ROOT);
		}
	}

}
