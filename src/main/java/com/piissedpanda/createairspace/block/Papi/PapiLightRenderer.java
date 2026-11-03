package com.piissedpanda.createairspace.block.Papi;

import com.mojang.blaze3d.vertex.PoseStack;
import com.piissedpanda.createairspace.CAPartialModels;
import com.piissedpanda.createairspace.block.AirportBeacon.AirportBeaconRenderer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;

import net.createmod.catnip.render.CachedBuffers;
import net.createmod.ponder.api.level.PonderLevel;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

// Every player sees the colour of their own approach angle
public class PapiLightRenderer extends SafeBlockEntityRenderer<PapiLightBlockEntity> {

	public static final int WHITE = 0xFFF8F0;
	public static final int RED = 0xFF2010;
	public static final int OFF = 0x303030;
	private static final double LENS_Y = 7.5 / 16;
	private static final double LENS_OFFSET = 4.5 / 16; // from the block centre towards the front

	public PapiLightRenderer(Context context) {
	}

	@Override
	protected void renderSafe(PapiLightBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
			int light, int overlay) {
		BlockState state = be.getBlockState();
		Direction facing = state.getValue(PapiLightBlock.FACING);
		int color = getColor(be, state, facing);

		CachedBuffers.partial(CAPartialModels.PAPI_LIGHT_LENS, state)
				.rotateCentered(yRotation(facing), Direction.UP)
				.color(color)
				.light(color == OFF ? light : LightTexture.FULL_BRIGHT)
				.overlay(overlay)
				.renderInto(ms, buffer.getBuffer(RenderType.solid()));

		if (color == OFF || be.getLevel() instanceof PonderLevel)
			return;
		Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
		Vec3 toCamera = camera.getPosition()
				.subtract(lensPosition(be.getBlockPos(), facing));
		double distance = toCamera.length();
		if (distance < 6)
			return;
		AirportBeaconRenderer.renderFlareAt(ms, buffer, camera, toCamera.scale(1 / distance),
				lensPosition(BlockPos.ZERO, facing), 0.2f + (float) distance * 0.02f, 0.7f, color);
	}

	private static int getColor(PapiLightBlockEntity be, BlockState state, Direction facing) {
		if (!state.getValue(PapiLightBlock.POWERED))
			return OFF;
		if (be.getLevel() instanceof PonderLevel)
			return be.ponderViewAngle > be.getAngle() ? WHITE : RED;

		Vec3 toCamera = Minecraft.getInstance().gameRenderer.getMainCamera()
				.getPosition()
				.subtract(lensPosition(be.getBlockPos(), facing));
		double horizontal = Math.sqrt(toCamera.x * toCamera.x + toCamera.z * toCamera.z);
		double along = toCamera.x * facing.getStepX() + toCamera.z * facing.getStepZ();
		// Dark from behind and more than 60 degrees to the side
		if (along <= 0 || along < horizontal / 2)
			return OFF;
		double elevation = Math.toDegrees(Math.atan2(toCamera.y, horizontal));
		return elevation > be.getAngle() ? WHITE : RED;
	}

	private static Vec3 lensPosition(BlockPos pos, Direction facing) {
		return Vec3.atLowerCornerOf(pos)
				.add(.5 + facing.getStepX() * LENS_OFFSET, LENS_Y, .5 + facing.getStepZ() * LENS_OFFSET);
	}

	// Models face north, same turn as the blockstate
	public static float yRotation(Direction facing) {
		return (float) Math.toRadians(-((facing.toYRot() + 180) % 360));
	}

	@Override
	public boolean shouldRenderOffScreen(PapiLightBlockEntity be) {
		return true;
	}

	@Override
	public int getViewDistance() {
		return 256;
	}

}
