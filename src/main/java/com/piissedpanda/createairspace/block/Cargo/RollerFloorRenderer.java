package com.piissedpanda.createairspace.block.Cargo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.piissedpanda.createairspace.CAPartialModels;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

// Shaft ends and rollers of both networks, each turning with its own speed
public class RollerFloorRenderer extends SafeBlockEntityRenderer<RollerFloorBlockEntity> {

	private static final float ROLLER_Y = 15.25f / 16;
	// Roller centres in pixels, X rollers in the north west and south east
	private static final float[][] ROLLERS_X = { { 4, 2.5f }, { 4, 5.5f }, { 12, 10.5f }, { 12, 13.5f } };
	private static final float[][] ROLLERS_Z = { { 10.5f, 4 }, { 13.5f, 4 }, { 2.5f, 12 }, { 5.5f, 12 } };

	public RollerFloorRenderer(Context context) {
	}

	@Override
	protected void renderSafe(RollerFloorBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
			int light, int overlay) {
		BlockState state = be.getBlockState();
		VertexConsumer vb = buffer.getBuffer(RenderType.solid());

		for (Direction.Axis axis : new Direction.Axis[] { Direction.Axis.X, Direction.Axis.Z }) {
			RollerFloorBlockEntity.Drive drive = be.getDrive(axis);
			float angle = KineticBlockEntityRenderer.getAngleForBe(drive, be.getBlockPos(), axis);

			for (Direction.AxisDirection sign : Direction.AxisDirection.values())
				KineticBlockEntityRenderer.kineticRotationTransform(CachedBuffers.partialFacing(
						AllPartialModels.SHAFT_HALF, state, Direction.fromAxisAndDirection(axis, sign)), drive, axis,
						angle, light)
						.renderInto(ms, vb);

			PartialModel roller = axis == Direction.Axis.X ? CAPartialModels.ROLLER_FLOOR_ROLLER_X
					: CAPartialModels.ROLLER_FLOOR_ROLLER_Z;
			Direction positive = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
			for (float[] centre : axis == Direction.Axis.X ? ROLLERS_X : ROLLERS_Z)
				CachedBuffers.partial(roller, state)
						.translate(centre[0] / 16, ROLLER_Y, centre[1] / 16)
						.rotate(angle, positive)
						.light(light)
						.renderInto(ms, vb);
		}
	}

}
