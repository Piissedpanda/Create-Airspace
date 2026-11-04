package com.piissedpanda.createairspace.block.Cargo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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

// Once a docked container is in front, the gate slides out against its port point
// and the bolts on every side slide forward in their slots
public class ContainerPortRenderer extends SafeBlockEntityRenderer<ContainerPortBlockEntity> {

	private static final float TRAVEL = 1.5f / 16;
	// The bolts slide from the middle of their slots to the front end
	private static final float BOLT_TRAVEL = 5 / 16f;

	public ContainerPortRenderer(Context context) {
	}

	@Override
	protected void renderSafe(ContainerPortBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
			int light, int overlay) {
		BlockState state = be.getBlockState();
		Direction facing = state.getValue(HorizontalDirectionalBlock.FACING);
		float out = be.plate.getValue(partialTicks);

		float eased = out * out * (3 - 2 * out);
		float angle = AngleHelper.horizontalAngle(facing.getOpposite());
		VertexConsumer vc = buffer.getBuffer(RenderType.solid());

		CachedBuffers.partial(CAPartialModels.CONTAINER_PORT_PLATE, state)
				.center()
				.rotateYDegrees(angle)
				.uncenter()
				.translate(0, 0, -TRAVEL * eased)
				.light(light)
				.overlay(overlay)
				.renderInto(ms, vc);

		// Two bolts on every side, like a vault door
		for (int quarter = 0; quarter < 4; quarter++)
			CachedBuffers.partial(CAPartialModels.CONTAINER_PORT_BOLTS, state)
					.center()
					.rotateYDegrees(angle)
					.rotateZDegrees(quarter * 90)
					.uncenter()
					.translate(0, 0, -BOLT_TRAVEL * eased)
					.light(light)
					.overlay(overlay)
					.renderInto(ms, vc);
	}

}
