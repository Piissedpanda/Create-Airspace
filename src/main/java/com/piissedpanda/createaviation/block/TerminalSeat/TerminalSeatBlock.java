package com.piissedpanda.createaviation.block.TerminalSeat;

import java.util.Locale;

import com.piissedpanda.createaviation.CABlocks;
import com.piissedpanda.createaviation.CAShapes;
import com.simibubi.create.content.contraptions.actors.seat.SeatBlock;
import com.simibubi.create.foundation.utility.BlockHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.util.FakePlayer;

// Extends Create's seat, so sitting, mobs and contraptions work the same way
public class TerminalSeatBlock extends SeatBlock {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<Shape> SHAPE = EnumProperty.create("shape", Shape.class);

	public TerminalSeatBlock(Properties properties, DyeColor color) {
		super(properties, color);
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH)
				.setValue(SHAPE, Shape.SINGLE));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder.add(FACING, SHAPE));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		BlockState state = super.getStateForPlacement(pContext).setValue(FACING, pContext.getHorizontalDirection()
				.getOpposite());
		return withShape(state, pContext.getLevel(), pContext.getClickedPos());
	}

	@Override
	public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState,
			LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
		BlockState state = super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
		if (pDirection.getAxis()
				.isVertical())
			return state;
		return withShape(state, pLevel, pPos);
	}

	private BlockState withShape(BlockState state, BlockGetter level, BlockPos pos) {
		Direction facing = state.getValue(FACING);
		boolean left = connectsTo(level.getBlockState(pos.relative(facing.getCounterClockWise())), facing);
		boolean right = connectsTo(level.getBlockState(pos.relative(facing.getClockWise())), facing);

		Shape shape = Shape.SINGLE;
		if (left && right)
			shape = Shape.MIDDLE;
		else if (left)
			shape = Shape.RIGHT;
		else if (right)
			shape = Shape.LEFT;
		return state.setValue(SHAPE, shape);
	}

	private static boolean connectsTo(BlockState state, Direction facing) {
		if (!(state.getBlock() instanceof TerminalSeatBlock))
			return false;
		return state.getValue(FACING) == facing;
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
			Player player, InteractionHand hand, BlockHitResult hitResult) {
		DyeColor dye = DyeColor.getColor(stack);
		if (dye == null || dye == color || player.isShiftKeyDown() || player instanceof FakePlayer)
			return super.useItemOn(stack, state, level, pos, player, hand, hitResult);

		if (level.isClientSide)
			return ItemInteractionResult.SUCCESS;
		BlockState newState = BlockHelper.copyProperties(state, CABlocks.TERMINAL_SEATS.get(dye)
				.getDefaultState());
		level.setBlockAndUpdate(pos, newState);
		return ItemInteractionResult.SUCCESS;
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return CAShapes.TERMINAL_SEAT.get(pState.getValue(FACING));
	}

	@Override
	public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos,
			CollisionContext pContext) {
		VoxelShape back = CAShapes.TERMINAL_SEAT_BACK.get(pState.getValue(FACING));
		if (pContext instanceof EntityCollisionContext ecc && ecc.getEntity() instanceof Player)
			return Shapes.or(CAShapes.TERMINAL_SEAT_COLLISION_PLAYERS, back);
		return Shapes.or(CAShapes.TERMINAL_SEAT_COLLISION, back);
	}

	@Override
	public BlockState rotate(BlockState pState, Rotation pRotation) {
		return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState pState, Mirror pMirror) {
		return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
	}

	// Seen from someone sitting on the seat
	public enum Shape implements StringRepresentable {
		SINGLE, LEFT, MIDDLE, RIGHT;

		@Override
		public String getSerializedName() {
			return name().toLowerCase(Locale.ROOT);
		}
	}

}
