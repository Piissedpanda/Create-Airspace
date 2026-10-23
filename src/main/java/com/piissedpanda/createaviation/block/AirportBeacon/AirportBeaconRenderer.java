package com.piissedpanda.createaviation.block.AirportBeacon;

import java.util.ArrayList;
import java.util.List;

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
import net.createmod.ponder.api.level.PonderLevel;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

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
			lens.rotateCentered(angle + toRadians(beam.angle()), Direction.UP)
					.color(power > 0 ? beam.color() : darken(beam.color()))
					.light(power > 0 ? LightTexture.FULL_BRIGHT : light)
					.overlay(overlay)
					.renderInto(ms, buffer.getBuffer(RenderType.solid()));
		}

		if (power == 0)
			return;

		float strength = power / 15f;
		float length = 2 + strength * (MAX_BEAM_LENGTH - 2);
		float alpha = 0.08f + strength * 0.3f;
		List<Ray> rays = getRays(mode, angle);

		VertexConsumer beams = buffer.getBuffer(RenderTypes.beaconBeam());
		for (Ray ray : rays) {
			ms.pushPose();
			ms.translate(.5, LENS_Y, .5);
			ms.mulPose(Axis.YP.rotation(ray.angle()));
			renderCone(ms, beams, length, ray.width(), alpha, ray.color());
			ms.popPose();
		}

		if (be.getLevel() instanceof PonderLevel || !AirportBeaconGlare.isEnabled())
			return;
		renderGlare(be, ms, buffer, rays, strength);
	}

	// Split beams become two narrow rays
	private static List<Ray> getRays(AirportBeaconBlock.Mode mode, float headAngle) {
		List<Ray> rays = new ArrayList<>();
		for (Beam beam : mode.beams) {
			float angle = headAngle + toRadians(beam.angle());
			if (beam.split() == 0) {
				rays.add(new Ray(angle, 1, beam.color()));
				continue;
			}
			rays.add(new Ray(angle - toRadians(beam.split()), 0.5f, beam.color()));
			rays.add(new Ray(angle + toRadians(beam.split()), 0.5f, beam.color()));
		}
		return rays;
	}

	// A flare facing the camera, bright only while a ray points right at it
	private static void renderGlare(AirportBeaconBlockEntity be, PoseStack ms, MultiBufferSource buffer,
			List<Ray> rays, float strength) {
		Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
		BlockPos pos = be.getBlockPos();
		Vec3 lens = Vec3.atLowerCornerOf(pos)
				.add(.5, LENS_Y, .5);
		Vec3 toCamera = camera.getPosition()
				.subtract(lens);
		double distance = toCamera.length();
		if (distance < 1)
			return;
		Vec3 view = toCamera.scale(1 / distance);

		float strongest = 0;
		int color = 0xFFFFFF;
		for (Ray ray : rays) {
			// Rays point north (-z) before they are turned
			double facing = -Math.sin(ray.angle()) * view.x - Math.cos(ray.angle()) * view.z;
			float intensity = (float) Math.pow(Math.max(0, facing), 60) * strength * (0.5f + ray.width() / 2);
			if (intensity <= strongest)
				continue;
			strongest = intensity;
			color = ray.color();
		}

		VertexConsumer vc = buffer.getBuffer(RenderTypes.beaconFlare());
		ms.pushPose();
		ms.translate(.5 + view.x * 0.6, LENS_Y + view.y * 0.6, .5 + view.z * 0.6);
		ms.mulPose(camera.rotation());
		float size = 0.5f + (float) distance * 0.05f;
		renderFlare(ms, vc, size * 0.5f, 0.15f * strength, 0xFFFFFF);
		if (strongest > 0.01f)
			renderFlare(ms, vc, size * (0.6f + strongest * 1.4f), strongest, color);
		ms.popPose();

		if (strongest > 0.05f && isVisible(be.getLevel(), camera.getPosition(), lens, pos))
			AirportBeaconGlare.offer(strongest, color);
	}

	private static boolean isVisible(Level level, Vec3 from, Vec3 to, BlockPos beacon) {
		BlockHitResult hit = level.clip(new ClipContext(from, to, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE,
				CollisionContext.empty()));
		return hit.getType() == HitResult.Type.MISS || hit.getBlockPos()
				.equals(beacon);
	}

	private static void renderFlare(PoseStack ms, VertexConsumer vc, float size, float alpha, int rgb) {
		Matrix4f pose = ms.last()
				.pose();
		float r = (rgb >> 16 & 0xFF) / 255f;
		float g = (rgb >> 8 & 0xFF) / 255f;
		float b = (rgb & 0xFF) / 255f;
		vc.addVertex(pose, -size, -size, 0)
				.setUv(0, 1)
				.setColor(r, g, b, alpha);
		vc.addVertex(pose, size, -size, 0)
				.setUv(1, 1)
				.setColor(r, g, b, alpha);
		vc.addVertex(pose, size, size, 0)
				.setUv(1, 0)
				.setColor(r, g, b, alpha);
		vc.addVertex(pose, -size, size, 0)
				.setUv(0, 0)
				.setColor(r, g, b, alpha);
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

	private static float toRadians(float degrees) {
		return degrees * (float) Math.PI / 180;
	}

	private static int darken(int rgb) {
		return (rgb >> 1) & 0x7F7F7F;
	}

	@Override
	public boolean shouldRenderOffScreen(AirportBeaconBlockEntity be) {
		return true;
	}

	private record Ray(float angle, float width, int color) {
	}

}
