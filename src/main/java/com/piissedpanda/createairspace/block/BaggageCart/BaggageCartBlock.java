package com.piissedpanda.createairspace.block.BaggageCart;

import java.util.List;
import java.util.Map;

import com.piissedpanda.createairspace.CABlockEntities;
import com.piissedpanda.createairspace.CAShapes;
import com.piissedpanda.createairspace.CATags;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.item.ItemHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.ItemHandlerHelper;

// Neighbouring carts join into one bigger cart: the net and wheels only show on the outside
public class BaggageCartBlock extends Block implements IBE<BaggageCartBlockEntity>, IWrenchable {

	public static final Map<Direction, BooleanProperty> CONNECTED = Map.of(Direction.NORTH, PipeBlock.NORTH,
			Direction.EAST, PipeBlock.EAST, Direction.SOUTH, PipeBlock.SOUTH, Direction.WEST, PipeBlock.WEST);

	public BaggageCartBlock(Properties properties) {
		super(properties);
		BlockState state = defaultBlockState();
		for (BooleanProperty property : CONNECTED.values())
			state = state.setValue(property, false);
		registerDefaultState(state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder.add(CONNECTED.values()
				.toArray(BooleanProperty[]::new)));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		BlockState state = defaultBlockState();
		for (Direction side : Direction.Plane.HORIZONTAL)
			state = state.setValue(CONNECTED.get(side), pContext.getLevel()
					.getBlockState(pContext.getClickedPos()
							.relative(side))
					.is(this));
		return state;
	}

	@Override
	public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState,
			LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
		if (!CONNECTED.containsKey(pDirection))
			return pState;
		return pState.setValue(CONNECTED.get(pDirection), pNeighborState.is(this));
	}

	@Override
	protected void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState,
			boolean pMovedByPiston) {
		if (!pOldState.is(this))
			BaggageCartBlockEntity.invalidateCart(pLevel, pPos);
	}

	@Override
	public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
		if (pState.is(pNewState.getBlock()))
			return;
		withBlockEntityDo(pLevel, pPos, be -> ItemHelper.dropContents(pLevel, pPos, be.inventory));
		IBE.onRemove(pState, pLevel, pPos, pNewState);
		for (Direction side : Direction.Plane.HORIZONTAL)
			BaggageCartBlockEntity.invalidateCart(pLevel, pPos.relative(side));
	}

	// Right-click stacks luggage on this cart, or on another part when it is full
	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
			Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (!stack.is(CATags.LUGGAGE))
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		if (!(level.getBlockEntity(pos) instanceof BaggageCartBlockEntity be))
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		if (level.isClientSide)
			return ItemInteractionResult.SUCCESS;

		ItemStack single = stack.copyWithCount(1);
		ItemStack rest = ItemHandlerHelper.insertItem(be.inventory, single, false);
		if (!rest.isEmpty())
			rest = ItemHandlerHelper.insertItem(be.cartInventory(), single, false);
		if (!rest.isEmpty())
			return ItemInteractionResult.SUCCESS;

		if (!player.isCreative())
			stack.shrink(1);
		AllSoundEvents.DEPOT_PLOP.playOnServer(level, pos);
		return ItemInteractionResult.SUCCESS;
	}

	// An empty hand takes the top piece of luggage
	@Override
	protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer,
			BlockHitResult pHitResult) {
		if (!pPlayer.getMainHandItem()
				.isEmpty())
			return InteractionResult.PASS;
		if (!(pLevel.getBlockEntity(pPos) instanceof BaggageCartBlockEntity be))
			return InteractionResult.PASS;
		if (pLevel.isClientSide)
			return InteractionResult.SUCCESS;

		ItemStack taken = takeTop(be);
		List<BaggageCartBlockEntity> cart = BaggageCartBlockEntity.getCart(pLevel, pPos);
		for (int i = cart.size() - 1; i >= 0 && taken.isEmpty(); i--)
			taken = takeTop(cart.get(i));
		if (taken.isEmpty())
			return InteractionResult.SUCCESS;

		pPlayer.getInventory()
				.placeItemBackInInventory(taken);
		AllSoundEvents.DEPOT_PLOP.playOnServer(pLevel, pPos);
		return InteractionResult.SUCCESS;
	}

	private static ItemStack takeTop(BaggageCartBlockEntity be) {
		int top = be.inventory.top();
		if (top < 0)
			return ItemStack.EMPTY;
		return be.inventory.extractItem(top, 1, false);
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState pState) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
		return getBlockEntityOptional(pLevel, pPos).map(be -> ItemHelper.calcRedstoneFromInventory(be.inventory))
				.orElse(0);
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return CAShapes.BAGGAGE_CART;
	}

	@Override
	protected boolean isPathfindable(BlockState pState, PathComputationType pPathComputationType) {
		return false;
	}

	@Override
	public Class<BaggageCartBlockEntity> getBlockEntityClass() {
		return BaggageCartBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends BaggageCartBlockEntity> getBlockEntityType() {
		return CABlockEntities.BAGGAGE_CART.get();
	}

}
