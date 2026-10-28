package com.piissedpanda.createairspace.block.Hangar;

import com.piissedpanda.createairspace.CABlockEntities;
import com.piissedpanda.createairspace.CAShapes;
import com.piissedpanda.createairspace.block.TerminalLight.TerminalLightBlock;
import com.simibubi.create.foundation.block.DyedBlockList;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

// Hall light under the hangar roof, switched like terminal lights, with a visible light cone
public class HangarLightBlock extends TerminalLightBlock implements IBE<HangarLightBlockEntity> {

	public HangarLightBlock(Properties properties) {
		super(properties, DyeColor.WHITE);
	}

	@Override
	protected DyedBlockList<? extends TerminalLightBlock> variants() {
		return null;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return withLevel(defaultBlockState(), pContext);
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return CAShapes.HANGAR_LIGHT;
	}

	@Override
	public Class<HangarLightBlockEntity> getBlockEntityClass() {
		return HangarLightBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends HangarLightBlockEntity> getBlockEntityType() {
		return CABlockEntities.HANGAR_LIGHT.get();
	}

}
