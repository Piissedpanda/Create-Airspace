package com.piissedpanda.createaviation.block.AirportBeacon;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.piissedpanda.createaviation.CAPartialModels;
import com.piissedpanda.createaviation.block.AirportBeacon.AirportBeaconBlock.Beam;
import com.piissedpanda.createaviation.foundation.render.RenderTypes;
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

	public static final float MAX_BEAM_LENGTH = 10;
	private static final float LENS_Y = 11 / 16f;
	private static final float LENS_Z = 4.5f / 16f; // distance of a lens from the centre

	public AirportBeaconRenderer(Context context) {
	}

	@Override
	protected void renderSafe(AirportBeaconBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
			int light, int overlay) {
		BlockState state = be.getBlockState();
		float angle = KineticBlockEntityRenderer.getAngleForBe(be, be.getBlockPos(), Direction.Axis.Y);
		int power = state.getValue(AirportBeaconBlock.POWER);
		AirportBeaconBlock.Mode mode = state.getValue(AirportBeaconBlock.MODE);

		CachedBuffers.partial(CAPartialModels.AIRPORT_BEACON_HEAD, state)
				.rotateCentered(angle, Direction.UP)
				.light(light)
				.overlay(overlay)
				.renderInto(ms, buffer.getBuffer(RenderType.solid()));

		// One tinted lens per beam of the pattern, dim while unpowered
		for (Beam beam : mode.beams) {
			SuperByteBuffer lens = CachedBuffers.partial(CAPartialModels.AIRPORT_BEACON_LENS, state);
			lens.rotateCentered(angle + beam.angle() * (float) Math.PI / 180, Direction.UP)
					.color(power > 0 ? beam.color() : darken(beam.color()))
					.light(power > 0 ? LightTexture.FULL_BRIGHT : light)
					.overlay(overlay)
					.renderInto(ms, buffer.getBuffer(RenderType.solid()));
		}

		if (power == 0)
			return;

		float length = 2 + power / 15f * (MAX_BEAM_LENGTH - 2);
		float alpha = 0.08f + power / 15f * 0.3f;
		VertexConsumer vc = buffer.getBuffer(RenderTypes.beaconBeam());
		for (Beam beam : mode.beams) {
			if (beam.split() == 0) {
				renderBeam(ms, vc, angle, beam.angle(), length, 1, alpha, beam.color());
				continue;
			}
			renderBeam(ms, vc, angle, beam.angle() - beam.split(), length, 0.5f, alpha, beam.color());
			renderBeam(ms, vc, angle, beam.angle() + beam.split(), length, 0.5f, alpha, beam.color());
		}
	}

	private static void renderBeam(PoseStack ms, VertexConsumer vc, float headAngle, float beamAngle, float length,
			float width, float alpha, int rgb) {
		ms.pushPose();
		ms.translate(.5, LENS_Y, .5);
		ms.mulPose(Axis.YP.rotation(headAngle + beamAngle * (float) Math.PI / 180));
		renderCone(ms, vc, length, width, alpha, rgb);
		ms.popPose();
	}

	private static int darken(int rgb) {
		return (rgb >> 1) & 0x7F7F7F;
	}

	// A fading cone pointing north (-z)
	private static void renderCone(PoseStack ms, VertexConsumer vc, float length, float width, float alpha, int rgb) {
		Matrix4f pose = ms.last()
				.pose();
		float near = 1.5f / 16f * width;
		float far = 0.12f * length * width;
		float z0 = -LENS_Z;
		float z1 = -(LENS_Z + length);
		float[][] corners = { { -1, -1 }, { 1, -1 }, { 1, 1 }, { -1, 1 } };

		for (int i = 0; i < 4; i++) {
			float[] a = corners[i];
			float[] b = corners[(i + 1) % 4];
			vertex(vc, pose, a[0] * near, a[1] * near, z0, rgb, alpha);
			vertex(vc, pose, b[0] * near, b[1] * near, z0, rgb, alpha);
			vertex(vc, pose, b[0] * far, b[1] * far, z1, rgb, 0);
			vertex(vc, pose, a[0] * far, a[1] * far, z1, rgb, 0);
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
