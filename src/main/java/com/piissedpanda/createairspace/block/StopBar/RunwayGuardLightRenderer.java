package com.piissedpanda.createairspace.block.StopBar;

import com.mojang.blaze3d.vertex.PoseStack;
import com.piissedpanda.createairspace.CAPartialModels;
import com.piissedpanda.createairspace.block.AirportBeacon.AirportBeaconRenderer;
import com.piissedpanda.createairspace.block.Papi.PapiLightRenderer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.ponder.api.level.PonderLevel;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class RunwayGuardLightRenderer extends SafeBlockEntityRenderer<RunwayGuardLightBlockEntity> {

	private static final int YELLOW = 0xFFC800;
	private static final int OFF = 0x403810;
	private static final float PERIOD = 30; // ticks for one flash of both lamps
	private static final double LENS_Y = 10 / 16.0;
	private static final double LENS_X = 4 / 16.0; // from the centre
	private static final double LENS_Z = 2.25 / 16.0; // from the centre towards the front

	public RunwayGuardLightRenderer(Context context) {
	}

	@Override
	protected void renderSafe(RunwayGuardLightBlockEntity be, float partialTicks, PoseStack ms,
			MultiBufferSource buffer, int light, int overlay) {
		BlockState state = be.getBlockState();
		Direction facing = state.getValue(RunwayGuardLightBlock.FACING);
		boolean powered = state.getValue(RunwayGuardLightBlock.POWERED);

		float phase = (be.getLevel()
				.getGameTime() % (long) PERIOD + partialTicks) / PERIOD * Mth.TWO_PI;
		float west = powered ? Math.max(0, Mth.sin(phase)) : 0;
		float east = powered ? Math.max(0, -Mth.sin(phase)) : 0;

		renderLens(CAPartialModels.RUNWAY_GUARD_LIGHT_LENS_WEST, state, facing, west, ms, buffer, light, overlay);
		renderLens(CAPartialModels.RUNWAY_GUARD_LIGHT_LENS_EAST, state, facing, east, ms, buffer, light, overlay);

		if (!powered || be.getLevel() instanceof PonderLevel)
			return;
		Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
		Vec3 centre = Vec3.atLowerCornerOf(be.getBlockPos())
				.add(.5, LENS_Y, .5);
		Vec3 toCamera = camera.getPosition()
				.subtract(centre);
		double distance = toCamera.length();
		double along = toCamera.x * facing.getStepX() + toCamera.z * facing.getStepZ();
		if (distance < 6 || along <= 0)
			return;
		Vec3 view = toCamera.scale(1 / distance);
		float size = 0.15f + (float) distance * 0.015f;
		// West of a lamp facing north is the counter-clockwise side
		Direction left = facing.getCounterClockWise();
		if (west > 0.05f)
			AirportBeaconRenderer.renderFlareAt(ms, buffer, camera, view, lens(facing, left), size, west * 0.7f,
					YELLOW);
		if (east > 0.05f)
			AirportBeaconRenderer.renderFlareAt(ms, buffer, camera, view, lens(facing, left.getOpposite()), size,
					east * 0.7f, YELLOW);
	}

	private static Vec3 lens(Direction facing, Direction side) {
		return new Vec3(.5 + facing.getStepX() * LENS_Z + side.getStepX() * LENS_X, LENS_Y,
				.5 + facing.getStepZ() * LENS_Z + side.getStepZ() * LENS_X);
	}

	private static void renderLens(PartialModel partial, BlockState state, Direction facing, float intensity,
			PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
		int color = intensity > 0.05f ? mix(OFF, YELLOW, intensity) : OFF;
		CachedBuffers.partial(partial, state)
				.rotateCentered(PapiLightRenderer.yRotation(facing), Direction.UP)
				.color(color)
				.light(intensity > 0.05f ? LightTexture.FULL_BRIGHT : light)
				.overlay(overlay)
				.renderInto(ms, buffer.getBuffer(RenderType.solid()));
	}

	private static int mix(int from, int to, float t) {
		int r = (int) Mth.lerp(t, from >> 16 & 0xFF, to >> 16 & 0xFF);
		int g = (int) Mth.lerp(t, from >> 8 & 0xFF, to >> 8 & 0xFF);
		int b = (int) Mth.lerp(t, from & 0xFF, to & 0xFF);
		return r << 16 | g << 8 | b;
	}

	@Override
	public boolean shouldRenderOffScreen(RunwayGuardLightBlockEntity be) {
		return true;
	}

	@Override
	public int getViewDistance() {
		return 256;
	}

}
