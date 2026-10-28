package com.piissedpanda.createairspace.block.CheckIn;

import java.util.Locale;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

// Luggage lying on the plate and its weight on the display, red when overweight
public class LuggageScaleRenderer extends SafeBlockEntityRenderer<LuggageScaleBlockEntity> {

	private static final float PLATE = 4 / 16f, BACK = 11.5f / 16f;

	public LuggageScaleRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	protected void renderSafe(LuggageScaleBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
			int light, int overlay) {
		Direction facing = be.getBlockState()
				.getValue(LuggageScaleBlock.FACING);
		ItemStack luggage = be.getLuggage();
		renderLuggage(luggage, facing, ms, buffer, light, overlay, be);
		renderDisplay(luggage.isEmpty() ? 0 : be.getWeight(), facing, ms, buffer);
	}

	private static void renderLuggage(ItemStack luggage, Direction facing, PoseStack ms, MultiBufferSource buffer,
			int light, int overlay, LuggageScaleBlockEntity be) {
		if (luggage.isEmpty())
			return;
		PartialModel model = AllPartialModels.PACKAGES.get(BuiltInRegistries.ITEM.getKey(luggage.getItem()));
		if (model == null)
			return;
		CachedBuffers.partial(model, be.getBlockState())
				.translate(.5f, PLATE, .5f)
				.rotateYDegrees(-facing.toYRot() + 90)
				.rotateXDegrees(90)
				.translate(-.5f, -PackageItem.getHeight(luggage) / 2, -BACK)
				.light(light)
				.overlay(overlay)
				.renderInto(ms, buffer.getBuffer(RenderType.solid()));
	}

	private static void renderDisplay(double weight, Direction facing, PoseStack ms, MultiBufferSource buffer) {
		Font font = Minecraft.getInstance().font;
		String text = String.format(Locale.ROOT, "%.1f kg", weight);
		int color = weight > LuggageWeight.OVERWEIGHT ? 0xFFFF4030 : 0xFF40FF60;
		float scale = 1 / 160f;

		ms.pushPose();
		ms.translate(.5, 14 / 16f, .5);
		// Just in front of the screen, which sits 4 px behind the centre
		ms.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
		ms.translate(0, 0, -(4 / 16f - 0.005f));
		ms.scale(scale, -scale, scale);
		font.drawInBatch(text, -font.width(text) / 2f, -4, color, false, ms.last()
				.pose(), buffer, Font.DisplayMode.POLYGON_OFFSET, 0, LightTexture.FULL_BRIGHT);
		ms.popPose();
	}

}
