package com.piissedpanda.createairspace.block.Security;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.piissedpanda.createairspace.CAPartialModels;
import com.simibubi.create.content.logistics.FlapStuffs;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;

import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;

// Rubber curtains at both ends, swung like the flaps of a Create tunnel
public class BaggageScannerRenderer extends SafeBlockEntityRenderer<BaggageScannerBlockEntity> {

	public BaggageScannerRenderer(Context context) {
	}

	@Override
	protected void renderSafe(BaggageScannerBlockEntity be, float partialTicks, PoseStack ms,
			MultiBufferSource buffer, int light, int overlay) {
		Direction.Axis axis = be.getBlockState()
				.getValue(BaggageScannerBlock.AXIS);
		SuperByteBuffer curtain = CachedBuffers.partial(CAPartialModels.BAGGAGE_SCANNER_CURTAIN, be.getBlockState());
		VertexConsumer vb = buffer.getBuffer(RenderType.solid());

		for (int i = 0; i < 2; i++) {
			Direction side = Direction.fromAxisAndDirection(axis,
					i == 0 ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE);
			float swing = be.curtains[i].getValue(partialTicks);
			FlapStuffs.renderFlaps(ms, vb, curtain, FlapStuffs.TUNNEL_PIVOT, side, swing, 0, light);
		}
	}

}
