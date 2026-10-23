package com.piissedpanda.createairspace.block.Windsock;

import com.mojang.blaze3d.vertex.PoseStack;
import com.piissedpanda.createairspace.CAPartialModels;
import com.piissedpanda.createairspace.foundation.weather.Wind;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;

import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.createmod.ponder.api.level.PonderLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.world.level.Level;

public class WindsockRenderer extends SafeBlockEntityRenderer<WindsockBlockEntity> {

	private static final float PIVOT_Y = 27 / 16f;

	public WindsockRenderer(Context context) {
	}

	@Override
	protected void renderSafe(WindsockBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
			int light, int overlay) {
		Level level = be.getLevel();
		if (level == null)
			return;

		// Ponder has no game time, so the wind changes much faster there
		double time = level instanceof PonderLevel ? AnimationTickHolder.getRenderTime(level) * 40
				: level.getGameTime() + partialTicks;
		float flutterTime = AnimationTickHolder.getRenderTime(level);

		float strength = Wind.getStrength(level, be.getBlockPos(), time, partialTicks);
		float yaw = Wind.getDirection(time) + (float) Math.sin(flutterTime * 0.6f) * 4 * strength;
		float droop = (1 - strength) * 80 + (float) Math.sin(flutterTime * 0.9f + 1) * 3;

		SuperByteBuffer sock = CachedBuffers.partial(CAPartialModels.WINDSOCK_SOCK, be.getBlockState());
		sock.translate(.5f, PIVOT_Y, .5f)
				.rotateYDegrees(-yaw)
				.rotateXDegrees(droop)
				.translate(-.5f, -.5f, 0)
				.light(light)
				.overlay(overlay)
				.renderInto(ms, buffer.getBuffer(RenderType.cutout()));
	}

	@Override
	public boolean shouldRenderOffScreen(WindsockBlockEntity be) {
		return true;
	}

}
