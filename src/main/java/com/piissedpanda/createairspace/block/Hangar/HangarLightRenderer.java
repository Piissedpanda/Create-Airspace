package com.piissedpanda.createairspace.block.Hangar;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.piissedpanda.createairspace.block.TerminalLight.TerminalLightBlock;
import com.piissedpanda.createairspace.foundation.render.RenderTypes;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

// Soft cone of light from the lens down to the floor, brighter with a stronger signal
public class HangarLightRenderer extends SafeBlockEntityRenderer<HangarLightBlockEntity> {

	public static final int MAX_DISTANCE = 16;
	private static final int SEGMENTS = 12;
	private static final float LENS = 9.5f / 16, TOP_RADIUS = 4.5f / 16, SPREAD = 0.3f;

	public HangarLightRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	protected void renderSafe(HangarLightBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
			int light, int overlay) {
		int level = be.getBlockState()
				.getValue(TerminalLightBlock.LEVEL);
		if (level == 0)
			return;

		float length = LENS + floorDistance(be.getLevel(), be.getBlockPos()) - 1;
		float bottomRadius = TOP_RADIUS + length * SPREAD;
		float alpha = 0.14f * level / 15;
		VertexConsumer vc = buffer.getBuffer(RenderTypes.beaconBeam());
		Matrix4f pose = ms.last()
				.pose();
		float bottom = LENS - length;

		for (int i = 0; i < SEGMENTS; i++) {
			float a0 = Mth.TWO_PI * i / SEGMENTS, a1 = Mth.TWO_PI * (i + 1) / SEGMENTS;
			vertex(vc, pose, a0, TOP_RADIUS, LENS, alpha);
			vertex(vc, pose, a1, TOP_RADIUS, LENS, alpha);
			vertex(vc, pose, a1, bottomRadius, bottom, 0);
			vertex(vc, pose, a0, bottomRadius, bottom, 0);
		}
	}

	// Blocks down to the first solid block, at most MAX_DISTANCE
	private static int floorDistance(Level level, BlockPos pos) {
		for (int d = 1; d < MAX_DISTANCE; d++) {
			BlockPos below = pos.below(d);
			if (level.getBlockState(below)
					.isSolidRender(level, below))
				return d;
		}
		return MAX_DISTANCE;
	}

	private static void vertex(VertexConsumer vc, Matrix4f pose, float angle, float radius, float y, float alpha) {
		vc.addVertex(pose, 0.5f + Mth.cos(angle) * radius, y, 0.5f + Mth.sin(angle) * radius)
				.setColor(1f, 0.96f, 0.85f, alpha);
	}

	@Override
	public boolean shouldRenderOffScreen(HangarLightBlockEntity be) {
		return true;
	}

}
