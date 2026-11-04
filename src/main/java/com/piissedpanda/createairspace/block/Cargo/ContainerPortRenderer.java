package com.piissedpanda.createairspace.block.Cargo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.piissedpanda.createairspace.CAPartialModels;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;

import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

// The plate slides out against the port point once a docked container is in front
public class ContainerPortRenderer extends SafeBlockEntityRenderer<ContainerPortBlockEntity> {

	private static final float TRAVEL = 1.5f / 16;

	public ContainerPortRenderer(Context context) {
	}

	@Override
	protected void renderSafe(ContainerPortBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
			int light, int overlay) {
		BlockState state = be.getBlockState();
		Direction facing = state.getValue(HorizontalDirectionalBlock.FACING);
		float out = be.plate.getValue(partialTicks);

		CachedBuffers.partial(CAPartialModels.CONTAINER_PORT_PLATE, state)
				.center()
				.rotateYDegrees(AngleHelper.horizontalAngle(facing.getOpposite()))
				.uncenter()
				.translate(0, 0, -TRAVEL * out * out * (3 - 2 * out))
				.light(light)
				.overlay(overlay)
				.renderInto(ms, buffer.getBuffer(RenderType.solid()));
	}

}
