package com.piissedpanda.createairspace.block.Cargo;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.piissedpanda.createairspace.CABlockEntities;
import com.piissedpanda.createairspace.CAShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.item.ItemHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

// Blocks join into a cargo container once they form one of the ULD shapes. The
// blockstates alone describe the container, so it survives being moved and rotated
public class CargoContainerBlock extends Block implements IBE<CargoContainerBlockEntity>, IWrenchable {

	public static final BooleanProperty FORMED = BooleanProperty.create("formed");
	public static final EnumProperty<Slope> SLOPE = EnumProperty.create("slope", Slope.class);

	// Ordered, so datagen writes the blockstates the same way every run
	public static final Map<Direction, BooleanProperty> CONNECTED = ImmutableMap.<Direction, BooleanProperty>builder()
			.put(Direction.NORTH, PipeBlock.NORTH)
			.put(Direction.EAST, PipeBlock.EAST)
			.put(Direction.SOUTH, PipeBlock.SOUTH)
			.put(Direction.WEST, PipeBlock.WEST)
			.put(Direction.UP, PipeBlock.UP)
			.put(Direction.DOWN, PipeBlock.DOWN)
			.build();

	public CargoContainerBlock(Properties properties) {
		super(properties);
		registerDefaultState(unformed(defaultBlockState()));
	}

	public static BlockState unformed(BlockState state) {
		for (BooleanProperty property : CONNECTED.values())
			state = state.setValue(property, false);
		return state.setValue(FORMED, false)
				.setValue(SLOPE, Slope.NONE);
	}

	public static boolean isConnected(BlockState state, Direction side) {
		return state.getBlock() instanceof CargoContainerBlock && state.getValue(FORMED)
				&& state.getValue(CONNECTED.get(side));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(FORMED, SLOPE)
				.add(CONNECTED.values()
						.toArray(BooleanProperty[]::new)));
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
			ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		// The LD3 contour faces the player who finished the container
		if (placer != null)
			withBlockEntityDo(level, pos, be -> be.placedFacing = placer.getDirection()
					.getOpposite());
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		if (!movedByPiston && !oldState.is(this) && !state.getValue(FORMED))
			level.scheduleTick(pos, this, 1);
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		CargoContainers.tryForm(level, pos);
	}

	@Override
	protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
		if (state.is(newState.getBlock()))
			return;
		withBlockEntityDo(level, pos, be -> ItemHelper.dropContents(level, pos, be.inventory));
		IBE.onRemove(state, level, pos, newState);
		if (!level.isClientSide && !movedByPiston && state.getValue(FORMED))
			CargoContainers.dissolveAround(level, pos, state);
	}

	// A wrench turns the contour of an LD3 to the next side
	@Override
	public InteractionResult onWrenched(BlockState state, UseOnContext context) {
		Level level = context.getLevel();
		if (!state.getValue(FORMED))
			return InteractionResult.PASS;
		if (!level.isClientSide && !CargoContainers.turnSlope(level, context.getClickedPos()))
			return InteractionResult.PASS;
		return InteractionResult.SUCCESS;
	}

	@Override
	protected BlockState rotate(BlockState state, Rotation rotation) {
		BlockState rotated = state;
		for (Direction side : Direction.Plane.HORIZONTAL)
			rotated = rotated.setValue(CONNECTED.get(rotation.rotate(side)), state.getValue(CONNECTED.get(side)));
		Direction slope = state.getValue(SLOPE).direction;
		return rotated.setValue(SLOPE, Slope.of(slope == null ? null : rotation.rotate(slope)));
	}

	@Override
	protected BlockState mirror(BlockState state, Mirror mirror) {
		BlockState mirrored = state;
		for (Direction side : Direction.Plane.HORIZONTAL)
			mirrored = mirrored.setValue(CONNECTED.get(mirror.mirror(side)), state.getValue(CONNECTED.get(side)));
		Direction slope = state.getValue(SLOPE).direction;
		return mirrored.setValue(SLOPE, Slope.of(slope == null ? null : mirror.mirror(slope)));
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		Direction slope = state.getValue(SLOPE).direction;
		return slope == null ? Shapes.block() : CAShapes.CONTAINER_SLOPE.get(slope);
	}

	@Override
	protected boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
		return getBlockEntityOptional(level, pos).map(CargoContainerBlockEntity::getContainerInventory)
				.map(ItemHelper::calcRedstoneFromInventory)
				.orElse(0);
	}

	@Override
	public Class<CargoContainerBlockEntity> getBlockEntityClass() {
		return CargoContainerBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends CargoContainerBlockEntity> getBlockEntityType() {
		return CABlockEntities.CARGO_CONTAINER.get();
	}

	// Side of the LD3 that has the contour, only set on its two lower front blocks
	public enum Slope implements StringRepresentable {
		NONE(null), NORTH(Direction.NORTH), EAST(Direction.EAST), SOUTH(Direction.SOUTH), WEST(Direction.WEST);

		@Nullable
		public final Direction direction;

		Slope(@Nullable Direction direction) {
			this.direction = direction;
		}

		public static Slope of(@Nullable Direction direction) {
			for (Slope slope : values())
				if (slope.direction == direction)
					return slope;
			return NONE;
		}

		@Override
		public String getSerializedName() {
			return name().toLowerCase();
		}
	}

}
