package com.piissedpanda.createaviation.block.AirportBeacon;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.piissedpanda.createaviation.CAPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;

import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class AirportBeaconRenderer extends SafeBlockEntityRenderer<AirportBeaconBlockEntity> {

	public static final float BEAM_LENGTH = 6;
	private static final float LENS_Y = 11 / 16f;
	private static final float LENS_Z = 4.5f / 16f; // distance of a lens from the centre

	public AirportBeaconRenderer(Context context) {
	}

	@Override
	protected void renderSafe(AirportBeaconBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
			int light, int overlay) {
		BlockState state = be.getBlockState();
		float angle = KineticBlockEntityRenderer.getAngleForBe(be, be.getBlockPos(), Direction.Axis.Y);
		boolean lit = be.getSpeed() != 0;

		SuperByteBuffer head = CachedBuffers.partial(CAPartialModels.AIRPORT_BEACON_HEAD, state);
		head.rotateCentered(angle, Direction.UP)
				.light(light)
				.overlay(overlay)
				.renderInto(ms, buffer.getBuffer(RenderType.solid()));

		SuperByteBuffer lenses = CachedBuffers.partial(CAPartialModels.AIRPORT_BEACON_LENSES, state);
		lenses.rotateCentered(angle, Direction.UP)
				.light(lit ? LightTexture.FULL_BRIGHT : light)
				.overlay(overlay)
				.renderInto(ms, buffer.getBuffer(RenderType.solid()));

		if (!lit)
			return;

		ms.pushPose();
		ms.translate(.5, LENS_Y, .5);
		ms.mulPose(Axis.YP.rotation(angle));
		VertexConsumer vc = buffer.getBuffer(RenderType.lightning());
		renderBeam(ms, vc, -1, 0x40FF60);
		renderBeam(ms, vc, 1, 0xFFFFFF);
		ms.popPose();
	}

	// A fading cone pointing along the z axis, drawn from both sides
	private static void renderBeam(PoseStack ms, VertexConsumer vc, int side, int rgb) {
		Matrix4f pose = ms.last()
				.pose();
		float near = 1.5f / 16f;
		float far = 0.9f;
		float z0 = side * LENS_Z;
		float z1 = side * (LENS_Z + BEAM_LENGTH);
		float[][] corners = { { -1, -1 }, { 1, -1 }, { 1, 1 }, { -1, 1 } };

		for (int i = 0; i < 4; i++) {
			float[] a = corners[i];
			float[] b = corners[(i + 1) % 4];
			for (int pass = 0; pass < 2; pass++) {
				float[] first = pass == 0 ? a : b;
				float[] second = pass == 0 ? b : a;
				vertex(vc, pose, first[0] * near, first[1] * near, z0, rgb, 0.35f);
				vertex(vc, pose, second[0] * near, second[1] * near, z0, rgb, 0.35f);
				vertex(vc, pose, second[0] * far, second[1] * far, z1, rgb, 0);
				vertex(vc, pose, first[0] * far, first[1] * far, z1, rgb, 0);
			}
		}
	}

	private static void vertex(VertexConsumer vc, Matrix4f pose, float x, float y, float z, int rgb, float alpha) {
		vc.addVertex(pose, x, y, z)
				.setColor((rgb >> 16 & 0xFF) / 255f, (rgb >> 8 & 0xFF) / 255f, (rgb & 0xFF) / 255f, alpha);
	}

	@Override
	public boolean shouldRenderOffScreen(AirportBeaconBlockEntity be) {
		return true;
	}

}
