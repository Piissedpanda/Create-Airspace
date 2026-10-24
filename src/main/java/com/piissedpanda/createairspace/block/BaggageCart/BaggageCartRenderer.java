package com.piissedpanda.createairspace.block.BaggageCart;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

// Luggage lies on its back, one piece per layer, slightly turned for a loaded look
public class BaggageCartRenderer extends SafeBlockEntityRenderer<BaggageCartBlockEntity> {

	private static final float DECK = 1 / 16f, LAYER = 7 / 16f, BACK = 11.5f / 16f;

	public BaggageCartRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	protected void renderSafe(BaggageCartBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
			int light, int overlay) {
		for (int layer = 0; layer < BaggageCartBlockEntity.LAYERS; layer++) {
			ItemStack luggage = be.inventory.getStackInSlot(layer);
			if (luggage.isEmpty())
				break;
			PartialModel model = AllPartialModels.PACKAGES.get(BuiltInRegistries.ITEM.getKey(luggage.getItem()));
			if (model == null)
				continue;

			float yaw = Math.floorMod(Mth.murmurHash3Mixer(be.getBlockPos()
					.hashCode() + layer * 31), 30) - 15;
			SuperByteBuffer sbb = CachedBuffers.partial(model, be.getBlockState());
			sbb.translate(.5f, DECK + layer * LAYER, .5f)
					.rotateYDegrees(yaw)
					.rotateXDegrees(90)
					.translate(-.5f, -PackageItem.getHeight(luggage) / 2, -BACK)
					.light(light)
					.overlay(overlay)
					.renderInto(ms, buffer.getBuffer(RenderType.solid()));
		}
	}

}
