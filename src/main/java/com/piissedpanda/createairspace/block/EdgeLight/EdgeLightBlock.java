package com.piissedpanda.createairspace.block.EdgeLight;

import java.util.Locale;

import javax.annotation.Nullable;

import com.piissedpanda.createairspace.CAShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;

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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EdgeLightBlock extends Block implements IWrenchable, ProperWaterloggedBlock {

	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final EnumProperty<LightColor> COLOR = EnumProperty.create("color", LightColor.class);

	public EdgeLightBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(POWERED, false)
				.setValue(COLOR, LightColor.WHITE)
				.setValue(WATERLOGGED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder.add(POWERED, COLOR, WATERLOGGED));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		boolean powered = pContext.getLevel()
				.hasNeighborSignal(pContext.getClickedPos());
		return withWater(defaultBlockState().setValue(POWERED, powered), pContext);
	}

	@Override
	protected void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock,
			BlockPos pNeighborPos, boolean pMovedByPiston) {
		if (pLevel.isClientSide)
			return;
		boolean powered = pLevel.hasNeighborSignal(pPos);
		if (powered == pState.getValue(POWERED))
			return;
		pLevel.setBlock(pPos, pState.setValue(POWERED, powered), 2);
	}

	// Lets redstone dust run alongside a row of lights and power all of them
	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos,
			@Nullable Direction direction) {
		return direction != null && direction.getAxis()
				.isHorizontal();
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
			Player player, InteractionHand hand, BlockHitResult hitResult) {
		DyeColor dye = DyeColor.getColor(stack);
		if (dye == null)
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		LightColor color = LightColor.byDye(dye);
		if (color == null || color == state.getValue(COLOR))
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

		if (!level.isClientSide)
			level.setBlockAndUpdate(pos, state.setValue(COLOR, color));
		return ItemInteractionResult.SUCCESS;
	}

	@Override
	public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState,
			LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
		updateWater(pLevel, pState, pPos);
		return pState;
	}

	@Override
	public FluidState getFluidState(BlockState pState) {
		return fluidState(pState);
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return CAShapes.EDGE_LIGHT;
	}

	@Override
	protected boolean isPathfindable(BlockState pState, PathComputationType pPathComputationType) {
		return false;
	}

	// White runway, blue taxiway, green and red thresholds, yellow caution
	public enum LightColor implements StringRepresentable {
		WHITE(DyeColor.WHITE), BLUE(DyeColor.BLUE), GREEN(DyeColor.GREEN), RED(DyeColor.RED),
		YELLOW(DyeColor.YELLOW);

		public final DyeColor dye;

		LightColor(DyeColor dye) {
			this.dye = dye;
		}

		@Nullable
		public static LightColor byDye(DyeColor dye) {
			for (LightColor color : values())
				if (color.dye == dye)
					return color;
			return null;
		}

		@Override
		public String getSerializedName() {
			return name().toLowerCase(Locale.ROOT);
		}
	}

}
