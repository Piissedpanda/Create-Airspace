package com.piissedpanda.createaviation.Block.BarrierPost;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.piissedpanda.createaviation.CAPartialModels;
import com.piissedpanda.createaviation.CreateAviation;
import com.piissedpanda.createaviation.Block.BarrierPost.BarrierPostBlockEntity.ConnectionStats;
import com.piissedpanda.createaviation.foundation.render.RenderTypes;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;

import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

public class BarrierPostRenderer extends SafeBlockEntityRenderer<BarrierPostBlockEntity> {

	public static final ResourceLocation BELT_LOCATION = CreateAviation
			.asResource("textures/block/belt.png");
	public static final int MIP_DISTANCE = 48;

	public BarrierPostRenderer(Context context) {
	}

	@Override
	protected void renderSafe(BarrierPostBlockEntity be, float partialTicks, PoseStack ms,
			MultiBufferSource buffer,
			int light, int overlay) {
		renderBelts(be, ms, buffer, light, overlay);

		if (VisualizationManager.supportsVisualization(be.getLevel()))
			return;

		CachedBuffers.partial(CAPartialModels.BARRIER_POST, be.getBlockState())
				.light(light)
				.overlay(overlay)
				.renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));
	}

	private void renderBelts(BarrierPostBlockEntity be, PoseStack ms, MultiBufferSource buffer, int light,
			int overlay) {
		for (BlockPos blockPos : be.connections) {
			ConnectionStats stats = be.connectionStats.get(blockPos);
			if (stats == null)
				continue;

			Vec3 diff = stats.end()
					.subtract(stats.start());
			double yaw = (float) Mth.RAD_TO_DEG * Mth.atan2(diff.x, diff.z);
			double pitch = (float) Mth.RAD_TO_DEG * Mth.atan2(diff.y, diff.multiply(1, 0, 1)
					.length());

			Level level = be.getLevel();
			BlockPos tilePos = be.getBlockPos();
			Vec3 startOffset = stats.start()
					.subtract(Vec3.atCenterOf(tilePos));

			if (!VisualizationManager.supportsVisualization(be.getLevel())) {
				SuperByteBuffer guard = CachedBuffers.partial(CAPartialModels.BARRIER_POST_GUARD,
						be.getBlockState());
				guard.center();
				guard.rotateYDegrees((float) yaw);

				guard.uncenter();
				guard.light(light)
						.overlay(overlay)
						.renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));
			}

			ms.pushPose();
			var belt = TransformStack.of(ms);
			belt.center();
			belt.translate(startOffset);
			belt.rotateYDegrees((float) yaw);
			belt.rotateXDegrees(90 - (float) pitch);
			belt.translate(0, 8 / 16f, 0);
			belt.uncenter();

			int light1 = LightTexture.pack(level.getBrightness(LightLayer.BLOCK, tilePos),
					level.getBrightness(LightLayer.SKY, tilePos));
			int light2 = LightTexture.pack(level.getBrightness(LightLayer.BLOCK, tilePos.offset(blockPos)),
					level.getBrightness(LightLayer.SKY, tilePos.offset(blockPos)));

			boolean far = Minecraft.getInstance().level == be.getLevel() && !Minecraft.getInstance()
					.getBlockEntityRenderDispatcher().camera.getPosition()
					.closerThan(Vec3.atCenterOf(tilePos)
							.add(blockPos.getX() / 2f, blockPos.getY() / 2f, blockPos.getZ() / 2f), MIP_DISTANCE);

			renderBelt(ms, buffer, stats.beltLength(), light1, light2, far, be.getBeltColor(blockPos));

			ms.popPose();
		}
	}

	public static void renderBelt(PoseStack ms, MultiBufferSource buffer, float length, int light1,
			int light2, boolean far, DyeColor color) {
		float radius = far ? 1f / 16f : 1.5f / 16f;
		float minV = 0;
		float maxV = far ? 1 / 16f : length + minV;
		float minU = 6 / 16f;
		float maxU = 10 / 16f;

		ms.pushPose();
		ms.translate(0.5D, 0.0D, 0.5D);

		VertexConsumer vc = buffer.getBuffer(RenderTypes.belt(BELT_LOCATION));
		renderPart(ms, vc, length, 0.0F, radius, radius, 0.0F, -radius, 0.0F, 0.0F, -radius, minU, maxU, minV, maxV,
				light1, light2, far, color);

		ms.popPose();
	}

	private static void renderPart(PoseStack pPoseStack, VertexConsumer pConsumer, float pMaxY, float pX0, float pZ0,
			float pX1, float pZ1, float pX2, float pZ2, float pX3, float pZ3, float pMinU, float pMaxU, float pMinV,
			float pMaxV, int light1, int light2, boolean far, DyeColor color) {
		PoseStack.Pose posestack$pose = pPoseStack.last();
		Matrix4f matrix4f = posestack$pose.pose();

		renderQuad(matrix4f, posestack$pose, pConsumer, 0, pMaxY, pX0, pZ0, pX3, pZ3, pMinU, pMaxU, pMinV, pMaxV,
				light1,
				light2,
				color);
	}

	private static void renderQuad(Matrix4f pPose, PoseStack.Pose pNormal, VertexConsumer pConsumer, float pMinY,
			float pMaxY,
			float pMinX, float pMinZ, float pMaxX, float pMaxZ, float pMinU, float pMaxU, float pMinV, float pMaxV,
			int light1, int light2, DyeColor color) {
		addVertex(pPose, pNormal, pConsumer, pMaxY, pMinX, pMinZ, pMaxU, pMinV, light2, color);
		addVertex(pPose, pNormal, pConsumer, pMinY, pMinX, pMinZ, pMaxU, pMaxV, light1, color);
		addVertex(pPose, pNormal, pConsumer, pMinY, pMaxX, pMaxZ, pMinU, pMaxV, light1, color);
		addVertex(pPose, pNormal, pConsumer, pMaxY, pMaxX, pMaxZ, pMinU, pMinV, light2, color);
	}

	private static void addVertex(Matrix4f pPose, PoseStack.Pose pNormal, VertexConsumer pConsumer, float pY, float pX,
			float pZ, float pU, float pV, int light, DyeColor color) {
		pConsumer.addVertex(pPose, pX, pY, pZ)
				.setColor(color.getTextureDiffuseColor())
				.setUv(pU, pV)
				.setOverlay(OverlayTexture.NO_OVERLAY)
				.setLight(light)
				.setNormal(pNormal, 0.0F, 1.0F, 0.0F);
	}

	@Override
	public int getViewDistance() {
		return 256;
	}

	@Override
	public boolean shouldRenderOffScreen(BarrierPostBlockEntity be) {
		return true;
	}

}
