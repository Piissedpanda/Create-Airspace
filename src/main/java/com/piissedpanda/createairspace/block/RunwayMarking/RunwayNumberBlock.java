package com.piissedpanda.createairspace.block.RunwayMarking;

import com.piissedpanda.createairspace.CAShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

// One character is 2 x 3 blocks, painted with a paint pail, the wrench changes the character.
// FACING points to the top of the character.
// PART = row * 2 + column, part 0 is the bottom left block.
public class RunwayNumberBlock extends Block implements IWrenchable {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<Glyph> GLYPH = EnumProperty.create("glyph", Glyph.class);
	public static final IntegerProperty PART = IntegerProperty.create("part", 0, 5);
	public static final int COLUMNS = 2, ROWS = 3;

	public RunwayNumberBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH)
				.setValue(GLYPH, Glyph.ONE)
				.setValue(PART, 0));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder.add(FACING, GLYPH, PART));
	}

	// Paints a whole character at once, the top points away from the painter
	public static boolean paint(Level level, BlockPos origin, Direction facing, Block block) {
		BlockState state = block.defaultBlockState()
				.setValue(FACING, facing);
		for (int part = 0; part < COLUMNS * ROWS; part++) {
			BlockPos pos = partPos(origin, facing, part);
			BlockState existing = level.getBlockState(pos);
			if (!existing.canBeReplaced() && !(existing.getBlock() instanceof RunwayMarkingBlock))
				return false;
			if (!state.canSurvive(level, pos))
				return false;
		}
		for (int part = 0; part < COLUMNS * ROWS; part++)
			level.setBlock(partPos(origin, facing, part), state.setValue(PART, part),
					Block.UPDATE_ALL | Block.UPDATE_KNOWN_SHAPE);
		return true;
	}

	@Override
	public InteractionResult onWrenched(BlockState state, UseOnContext context) {
		Level level = context.getLevel();
		BlockPos origin = origin(context.getClickedPos(), state);
		Glyph glyph = state.getValue(GLYPH)
				.next();

		// UPDATE_KNOWN_SHAPE keeps the half changed character from breaking itself
		if (!level.isClientSide)
			for (int part = 0; part < COLUMNS * ROWS; part++)
				level.setBlock(partPos(origin, state.getValue(FACING), part), state.setValue(GLYPH, glyph)
						.setValue(PART, part), Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
		IWrenchable.playRotateSound(level, context.getClickedPos());
		return InteractionResult.SUCCESS;
	}

	@Override
	public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState,
			LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
		if (pDirection == Direction.DOWN && !pState.canSurvive(pLevel, pPos))
			return Blocks.AIR.defaultBlockState();
		if (pDirection.getAxis()
				.isVertical())
			return pState;

		// A missing part removes the whole character
		Direction facing = pState.getValue(FACING);
		int part = pState.getValue(PART);
		int column = part % COLUMNS + (pDirection == facing.getClockWise() ? 1
				: pDirection == facing.getCounterClockWise() ? -1 : 0);
		int row = part / COLUMNS + (pDirection == facing ? 1 : pDirection == facing.getOpposite() ? -1 : 0);
		if (column < 0 || column >= COLUMNS || row < 0 || row >= ROWS)
			return pState;
		if (pNeighborState.is(this) && pNeighborState.getValue(FACING) == facing
				&& pNeighborState.getValue(GLYPH) == pState.getValue(GLYPH)
				&& pNeighborState.getValue(PART) == row * COLUMNS + column)
			return pState;
		return Blocks.AIR.defaultBlockState();
	}

	@Override
	protected boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
		BlockPos below = pPos.below();
		return pLevel.getBlockState(below)
				.isFaceSturdy(pLevel, below, Direction.UP);
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

	public static BlockPos partPos(BlockPos origin, Direction facing, int part) {
		return origin.relative(facing.getClockWise(), part % COLUMNS)
				.relative(facing, part / COLUMNS);
	}

	public static BlockPos origin(BlockPos pos, BlockState state) {
		Direction facing = state.getValue(FACING);
		int part = state.getValue(PART);
		return pos.relative(facing.getCounterClockWise(), part % COLUMNS)
				.relative(facing.getOpposite(), part / COLUMNS);
	}

	public enum Glyph implements StringRepresentable {
		ZERO("0"), ONE("1"), TWO("2"), THREE("3"), FOUR("4"), FIVE("5"), SIX("6"), SEVEN("7"), EIGHT("8"),
		NINE("9"), LEFT("l"), RIGHT("r"), CENTER("c");

		public final String id;

		Glyph(String id) {
			this.id = id;
		}

		public Glyph next() {
			return values()[(ordinal() + 1) % values().length];
		}

		@Override
		public String getSerializedName() {
			return id;
		}
	}

}
