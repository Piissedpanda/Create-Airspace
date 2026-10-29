package com.piissedpanda.createairspace.block.CheckIn;

import java.util.Locale;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
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
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

// Luggage moving over the plate, and its weight on the screen at the front (red when overweight)
public class LuggageScaleRenderer extends SafeBlockEntityRenderer<LuggageScaleBlockEntity> {

	private static final float PLATE = 14 / 16f, BACK = 11.5f / 16f;
	private static final float SCREEN_HEIGHT = 6.5f / 16, SCREEN_FRONT = 0.5f + 0.2f / 16 + 0.002f;

	public LuggageScaleRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	protected void renderSafe(LuggageScaleBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
			int light, int overlay) {
		renderLuggage(be, partialTicks, ms, buffer, light, overlay);
		renderDisplay(be.getLuggage()
				.isEmpty() ? 0 : be.getWeight(), be.getBlockState()
						.getValue(LuggageScaleBlock.FACING),
				ms, buffer);
	}

	private static void renderLuggage(LuggageScaleBlockEntity be, float partialTicks, PoseStack ms,
			MultiBufferSource buffer, int light, int overlay) {
		TransportedItemStack held = be.heldItem;
		if (held == null || !held.insertedFrom.getAxis()
				.isHorizontal())
			return;
		PartialModel model = AllPartialModels.PACKAGES.get(BuiltInRegistries.ITEM.getKey(held.stack.getItem()));
		if (model == null)
			return;

		// Along the direction of travel, from the entry edge (0) to the exit edge (1)
		float progress = Mth.lerp(partialTicks, held.prevBeltPosition, held.beltPosition);
		Vec3 offset = Vec3.atLowerCornerOf(held.insertedFrom.getNormal())
				.scale(progress - .5f);
		CachedBuffers.partial(model, be.getBlockState())
				.translate(.5f + offset.x, PLATE, .5f + offset.z)
				.rotateYDegrees(-held.insertedFrom.toYRot())
				.rotateXDegrees(90)
				.translate(-.5f, -PackageItem.getHeight(held.stack) / 2, -BACK)
				.light(light)
				.overlay(overlay)
				.renderInto(ms, buffer.getBuffer(RenderType.solid()));
	}

	private static void renderDisplay(double weight, Direction facing, PoseStack ms, MultiBufferSource buffer) {
		Font font = Minecraft.getInstance().font;
		String text = String.format(Locale.ROOT, "%.1f kg", weight);
		int color = weight > LuggageWeight.OVERWEIGHT ? 0xFFFF4030 : 0xFF40FF60;
		float scale = 1 / 140f;

		ms.pushPose();
		ms.translate(.5, SCREEN_HEIGHT, .5);
		ms.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
		ms.translate(0, 0, SCREEN_FRONT);
		ms.scale(scale, -scale, scale);
		font.drawInBatch(text, -font.width(text) / 2f, -4, color, false, ms.last()
				.pose(), buffer, Font.DisplayMode.POLYGON_OFFSET, 0, LightTexture.FULL_BRIGHT);
		ms.popPose();
	}

}
