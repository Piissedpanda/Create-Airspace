package com.piissedpanda.createairspace.block.AirfieldSign;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.piissedpanda.createairspace.CABlockEntities;
import com.piissedpanda.createairspace.CAShapes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.equipment.clipboard.ClipboardEntry;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AirfieldSignBlock extends Block implements IWrenchable, IBE<AirfieldSignBlockEntity> {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<Style> STYLE = EnumProperty.create("style", Style.class);
	public static final int MAX_ROW_LENGTH = 16;

	public AirfieldSignBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH)
				.setValue(STYLE, Style.LOCATION));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder.add(FACING, STYLE));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		BlockState state = defaultBlockState().setValue(FACING, pContext.getHorizontalDirection()
				.getOpposite());

		// Continue the style of a row the sign is placed next to
		Direction facing = state.getValue(FACING);
		for (Direction side : new Direction[] { facing.getClockWise(), facing.getCounterClockWise() }) {
			BlockState neighbour = pContext.getLevel()
					.getBlockState(pContext.getClickedPos()
							.relative(side));
			if (neighbour.getBlock() instanceof AirfieldSignBlock && neighbour.getValue(FACING) == facing)
				return state.setValue(STYLE, neighbour.getValue(STYLE));
		}
		return state;
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
			Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (stack.isEmpty() && player.isShiftKeyDown()) {
			if (!level.isClientSide)
				setRowText(level, pos, "");
			return ItemInteractionResult.SUCCESS;
		}

		Component text = null;
		if (stack.is(Items.NAME_TAG) && stack.has(DataComponents.CUSTOM_NAME))
			text = stack.get(DataComponents.CUSTOM_NAME);
		if (AllBlocks.CLIPBOARD.isIn(stack)) {
			List<ClipboardEntry> entries = ClipboardEntry.getLastViewedEntries(stack);
			if (!entries.isEmpty())
				text = entries.getFirst().text;
		}
		if (text == null)
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

		if (!level.isClientSide)
			setRowText(level, pos, text.getString());
		return ItemInteractionResult.SUCCESS;
	}

	@Override
	public InteractionResult onWrenched(BlockState state, UseOnContext context) {
		if (context.getClickedFace()
				.getAxis()
				.isVertical())
			return IWrenchable.super.onWrenched(state, context);

		// Wrenching the front or back switches the sign type of the whole row
		Level level = context.getLevel();
		Style next = state.getValue(STYLE)
				.next();
		for (BlockPos pos : getRow(level, context.getClickedPos()))
			level.setBlock(pos, level.getBlockState(pos)
					.setValue(STYLE, next), 3);
		IWrenchable.playRotateSound(level, context.getClickedPos());
		return InteractionResult.SUCCESS;
	}

	public static void setRowText(Level level, BlockPos pos, String text) {
		List<BlockPos> row = getRow(level, pos);
		for (int i = 0; i < row.size(); i++) {
			if (!(level.getBlockEntity(row.get(i)) instanceof AirfieldSignBlockEntity be))
				continue;
			be.setText(i == 0 ? text : "");
		}
	}

	// Signs facing the same way with the same style, from left to right as seen
	// from the front
	public static List<BlockPos> getRow(BlockGetter level, BlockPos pos) {
		List<BlockPos> row = new ArrayList<>();
		BlockState state = level.getBlockState(pos);
		if (!(state.getBlock() instanceof AirfieldSignBlock))
			return row;

		Direction facing = state.getValue(FACING);
		Direction left = facing.getClockWise();
		BlockPos start = pos;
		for (int i = 0; i < MAX_ROW_LENGTH && belongsToRow(level.getBlockState(start.relative(left)), state); i++)
			start = start.relative(left);

		BlockPos current = start;
		for (int i = 0; i < MAX_ROW_LENGTH && belongsToRow(level.getBlockState(current), state); i++) {
			row.add(current);
			current = current.relative(left.getOpposite());
		}
		return row;
	}

	public static boolean belongsToRow(BlockState other, BlockState state) {
		if (!(other.getBlock() instanceof AirfieldSignBlock))
			return false;
		return other.getValue(FACING) == state.getValue(FACING) && other.getValue(STYLE) == state.getValue(STYLE);
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return CAShapes.AIRFIELD_SIGN.get(pState.getValue(FACING));
	}

	@Override
	public BlockState rotate(BlockState pState, Rotation pRotation) {
		return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState pState, Mirror pMirror) {
		return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
	}

	@Override
	protected boolean isPathfindable(BlockState pState, PathComputationType pPathComputationType) {
		return false;
	}

	@Override
	public Class<AirfieldSignBlockEntity> getBlockEntityClass() {
		return AirfieldSignBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends AirfieldSignBlockEntity> getBlockEntityType() {
		return CABlockEntities.AIRFIELD_SIGN.get();
	}

	// Location: yellow on black, Direction: black on yellow, Mandatory: white on
	// red
	public enum Style implements StringRepresentable {
		LOCATION(0xFFD21F, "minecraft:block/black_concrete"),
		DIRECTION(0x111111, "minecraft:block/yellow_concrete"),
		MANDATORY(0xFFFFFF, "minecraft:block/red_concrete");

		public final int textColor;
		public final String faceTexture;

		Style(int textColor, String faceTexture) {
			this.textColor = textColor;
			this.faceTexture = faceTexture;
		}

		public Style next() {
			return values()[(ordinal() + 1) % values().length];
		}

		@Override
		public String getSerializedName() {
			return name().toLowerCase(Locale.ROOT);
		}
	}

}
