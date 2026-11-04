package com.piissedpanda.createairspace.block.Cargo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.piissedpanda.createairspace.CAPartialModels;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;

import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

// Four claws fold up out of their slots and grip the lock point of the container
public class ContainerLockRenderer extends SafeBlockEntityRenderer<ContainerLockBlockEntity> {

	private static final float GRIP_ANGLE = 80;
	// Hinge of the claw in the north slot, the model faces up
	private static final float HINGE_Y = 14.75f / 16, HINGE_Z = 1 / 16f;

	public ContainerLockRenderer(Context context) {
	}

	@Override
	protected void renderSafe(ContainerLockBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
			int light, int overlay) {
		BlockState state = be.getBlockState();
		Direction facing = state.getValue(DirectionalBlock.FACING);
		float grip = be.grip.getValue(partialTicks);
		float angle = GRIP_ANGLE * grip * grip * (3 - 2 * grip);
		VertexConsumer vc = buffer.getBuffer(RenderType.cutoutMipped());

		for (int i = 0; i < 4; i++)
			CachedBuffers.partial(CAPartialModels.CONTAINER_LOCK_CLAW, state)
					.center()
					.rotate(facing.getRotation())
					.rotateYDegrees(i * 90)
					.uncenter()
					.translate(0, HINGE_Y, HINGE_Z)
					.rotateXDegrees(-angle)
					.translate(0, -HINGE_Y, -HINGE_Z)
					.light(light)
					.overlay(overlay)
					.renderInto(ms, vc);
	}

}
