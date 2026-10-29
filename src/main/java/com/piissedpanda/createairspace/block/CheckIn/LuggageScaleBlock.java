package com.piissedpanda.createairspace.block.CheckIn;

import com.piissedpanda.createairspace.CABlockEntities;
import com.piissedpanda.createairspace.CAShapes;
import com.piissedpanda.createairspace.CATags;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;

import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

// Weighs luggage passing over it on belts, or put on by hand. The display faces the passenger,
// right-click opens the range for the comparator signal, sneak-right-click takes the luggage off.
public class LuggageScaleBlock extends Block implements IBE<LuggageScaleBlockEntity>, IWrenchable {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	public LuggageScaleBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder.add(FACING));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return defaultBlockState().setValue(FACING, pContext.getHorizontalDirection()
				.getOpposite());
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
			Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (!stack.is(CATags.LUGGAGE))
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		if (!(level.getBlockEntity(pos) instanceof LuggageScaleBlockEntity be) || !be.getLuggage()
				.isEmpty())
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		if (level.isClientSide)
			return ItemInteractionResult.SUCCESS;

		be.placeByHand(stack, state.getValue(FACING));
		if (!player.isCreative())
			stack.shrink(1);
		AllSoundEvents.DEPOT_PLOP.playOnServer(level, pos);
		return ItemInteractionResult.SUCCESS;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer,
			BlockHitResult pHitResult) {
		if (!(pLevel.getBlockEntity(pPos) instanceof LuggageScaleBlockEntity be))
			return InteractionResult.PASS;

		if (pPlayer.isShiftKeyDown()) {
			if (be.getLuggage()
					.isEmpty())
				return InteractionResult.PASS;
			if (!pLevel.isClientSide) {
				pPlayer.getInventory()
						.placeItemBackInInventory(be.takeLuggage());
				AllSoundEvents.DEPOT_PLOP.playOnServer(pLevel, pPos);
			}
			return InteractionResult.SUCCESS;
		}

		CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> LuggageScaleScreen.open(be));
		return InteractionResult.SUCCESS;
	}

	@Override
	public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
		if (!pState.is(pNewState.getBlock()))
			withBlockEntityDo(pLevel, pPos, be -> Containers.dropItemStack(pLevel, pPos.getX(), pPos.getY(),
					pPos.getZ(), be.getLuggage()));
		IBE.onRemove(pState, pLevel, pPos, pNewState);
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState pState) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
		return getBlockEntityOptional(pLevel, pPos).map(LuggageScaleBlockEntity::getSignal)
				.orElse(0);
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return CAShapes.LUGGAGE_SCALE;
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
	public Class<LuggageScaleBlockEntity> getBlockEntityClass() {
		return LuggageScaleBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends LuggageScaleBlockEntity> getBlockEntityType() {
		return CABlockEntities.LUGGAGE_SCALE.get();
	}

}
