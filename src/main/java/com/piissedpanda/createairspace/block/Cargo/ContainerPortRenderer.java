package com.piissedpanda.createairspace.block.Cargo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.piissedpanda.createairspace.CAPartialModels;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;

import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

// Once a docked container is in front, the gate slides out against its port point
// and the latches on both sides reach forward and catch it
public class ContainerPortRenderer extends SafeBlockEntityRenderer<ContainerPortBlockEntity> {

	private static final float TRAVEL = 1.5f / 16;
	// The side latches reach past the front and catch the container
	private static final float LATCH_TRAVEL = 3.5f / 16;

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

		// Latches on the east and west side of the port
		for (PartialModel latch : new PartialModel[] { CAPartialModels.CONTAINER_PORT_LATCH_EAST,
				CAPartialModels.CONTAINER_PORT_LATCH_WEST })
			CachedBuffers.partial(latch, state)
					.center()
					.rotateYDegrees(angle)
					.uncenter()
					.translate(0, 0, -LATCH_TRAVEL * eased)
					.light(light)
					.overlay(overlay)
					.renderInto(ms, vc);
	}

}
