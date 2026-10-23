package com.piissedpanda.createairspace.block.AirfieldSign;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.piissedpanda.createairspace.block.AirfieldSign.AirfieldSignBlock.Style;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class AirfieldSignRenderer extends SafeBlockEntityRenderer<AirfieldSignBlockEntity> {

	private static final float TEXT_HEIGHT = 7 / 16f;
	private static final float FACE_OFFSET = 2 / 16f + 0.005f; // panel is 4 pixels thick
	private static final float FACE_CENTER = 9 / 16f - 0.5f;

	public AirfieldSignRenderer(Context context) {
	}

	@Override
	protected void renderSafe(AirfieldSignBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
			int light, int overlay) {
		Level level = be.getLevel();
		BlockState state = be.getBlockState();
		if (level == null || !(state.getBlock() instanceof AirfieldSignBlock))
			return;

		List<BlockPos> row = AirfieldSignBlock.getRow(level, be.getBlockPos());
		if (row.isEmpty() || !row.getFirst()
				.equals(be.getBlockPos()))
			return;

		String text = getRowText(level, row);
		if (text.isEmpty())
			return;

		Font font = Minecraft.getInstance().font;
		int width = font.width(text);
		float rowLength = row.size();
		float scale = TEXT_HEIGHT / 8f;
		if (width * scale > rowLength - 0.25f)
			scale = (rowLength - 0.25f) / width;

		Direction facing = state.getValue(AirfieldSignBlock.FACING);
		Style style = state.getValue(AirfieldSignBlock.STYLE);
		int color = 0xFF000000 | style.textColor;

		ms.pushPose();
		ms.translate(.5, .5, .5);
		ms.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
		ms.translate((rowLength - 1) / 2f, FACE_CENTER, 0);

		// Front, then the back side turned around
		for (int side = 0; side < 2; side++) {
			ms.pushPose();
			ms.mulPose(Axis.YP.rotationDegrees(side * 180));
			ms.translate(0, 0, FACE_OFFSET);
			ms.scale(scale, -scale, scale);
			font.drawInBatch(text, -width / 2f, -3.5f, color, false, ms.last()
					.pose(), buffer, Font.DisplayMode.POLYGON_OFFSET, 0, LightTexture.FULL_BRIGHT);
			ms.popPose();
		}
		ms.popPose();
	}

	private static String getRowText(Level level, List<BlockPos> row) {
		for (BlockPos pos : row)
			if (level.getBlockEntity(pos) instanceof AirfieldSignBlockEntity be && !be.getText()
					.isEmpty())
				return be.getText();
		return "";
	}

	@Override
	public boolean shouldRenderOffScreen(AirfieldSignBlockEntity be) {
		return true;
	}

}
