package com.piissedpanda.createairspace.block.AirportBeacon;

import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import com.piissedpanda.createairspace.CABlockEntities;
import com.piissedpanda.createairspace.CALang;
import com.piissedpanda.createairspace.CAShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

// Rotation turns the head, redstone from the sides controls the light
public class AirportBeaconBlock extends KineticBlock implements IBE<AirportBeaconBlockEntity> {

	public static final IntegerProperty POWER = BlockStateProperties.POWER;
	public static final EnumProperty<Mode> MODE = EnumProperty.create("mode", Mode.class);

	public AirportBeaconBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(POWER, 0)
				.setValue(MODE, Mode.LAND));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder.add(POWER, MODE));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		int power = pContext.getLevel()
				.getBestNeighborSignal(pContext.getClickedPos());
		return defaultBlockState().setValue(POWER, power);
	}

	@Override
	protected void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock,
			BlockPos pNeighborPos, boolean pMovedByPiston) {
		if (pLevel.isClientSide)
			return;
		int power = pLevel.getBestNeighborSignal(pPos);
		if (power == pState.getValue(POWER))
			return;
		pLevel.setBlock(pPos, pState.setValue(POWER, power), 2);
	}

	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos,
			@Nullable Direction direction) {
		return direction != null && direction.getAxis()
				.isHorizontal();
	}

	@Override
	public InteractionResult onWrenched(BlockState state, UseOnContext context) {
		Level level = context.getLevel();
		Mode mode = state.getValue(MODE)
				.next();
		Player player = context.getPlayer();

		if (!level.isClientSide)
			level.setBlock(context.getClickedPos(), state.setValue(MODE, mode), 3);
		else if (player != null)
			showMode(player, mode);
		IWrenchable.playRotateSound(level, context.getClickedPos());
		return InteractionResult.SUCCESS;
	}

	// Shown above the hotbar
	private static void showMode(Player player, Mode mode) {
		String key = "airport_beacon.mode." + mode.getSerializedName();
		CALang.translate("airport_beacon.mode", CALang.translate(key)
				.component())
				.sendStatus(player);
	}

	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return face == Direction.DOWN;
	}

	@Override
	public Axis getRotationAxis(BlockState state) {
		return Axis.Y;
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return CAShapes.AIRPORT_BEACON;
	}

	@Override
	protected boolean isPathfindable(BlockState pState, PathComputationType pPathComputationType) {
		return false;
	}

	@Override
	public Class<AirportBeaconBlockEntity> getBlockEntityClass() {
		return AirportBeaconBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends AirportBeaconBlockEntity> getBlockEntityType() {
		return CABlockEntities.AIRPORT_BEACON.get();
	}

	// split: one lens sends two narrow beams that flash shortly after another
	public record Beam(float angle, int color, float split) {

		Beam(float angle, int color) {
			this(angle, color, 0);
		}

	}

	// Real beacon patterns, military: two quick whites between greens
	public enum Mode implements StringRepresentable {
		LAND(List.of(new Beam(0, Colors.WHITE), new Beam(180, Colors.GREEN))),
		WATER(List.of(new Beam(0, Colors.WHITE), new Beam(180, Colors.YELLOW))),
		HELIPORT(List.of(new Beam(0, Colors.GREEN), new Beam(120, Colors.YELLOW), new Beam(240, Colors.WHITE))),
		MILITARY(List.of(new Beam(0, Colors.GREEN), new Beam(180, Colors.WHITE, 7)));

		public final List<Beam> beams;

		Mode(List<Beam> beams) {
			this.beams = beams;
		}

		public Mode next() {
			return values()[(ordinal() + 1) % values().length];
		}

		@Override
		public String getSerializedName() {
			return name().toLowerCase(Locale.ROOT);
		}
	}

	private static class Colors {
		static final int WHITE = 0xFFFFFF;
		static final int GREEN = 0x40FF60;
		static final int YELLOW = 0xFFD030;
	}

}
