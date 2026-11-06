package com.piissedpanda.createairspace.block.Cargo;

import org.joml.Quaternionf;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.piissedpanda.createairspace.CAPartialModels;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;

import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

// Shaft ends of both networks and the discs on top. Like on an omnidirectional floor the discs spin
// and lean towards the direction the floor moves things
public class RollerFloorRenderer extends SafeBlockEntityRenderer<RollerFloorBlockEntity> {

	private static final float DISC_Y = 15.5f / 16;
	private static final float[][] DISCS = { { 4.5f, 4.5f }, { 4.5f, 11.5f }, { 11.5f, 4.5f }, { 11.5f, 11.5f } };
	private static final float SPIN_PER_BLOCK = 0.06f;
	private static final float MAX_LEAN = 10 * Mth.DEG_TO_RAD;

	public RollerFloorRenderer(Context context) {
	}

	@Override
	protected void renderSafe(RollerFloorBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
			int light, int overlay) {
		BlockState state = be.getBlockState();
		VertexConsumer solid = buffer.getBuffer(RenderType.solid());

		for (Direction.Axis axis : new Direction.Axis[] { Direction.Axis.X, Direction.Axis.Z }) {
			RollerFloorBlockEntity.Drive drive = be.getDrive(axis);
			float angle = KineticBlockEntityRenderer.getAngleForBe(drive, be.getBlockPos(), axis);
			for (Direction.AxisDirection sign : Direction.AxisDirection.values())
				KineticBlockEntityRenderer.kineticRotationTransform(CachedBuffers.partialFacing(
						AllPartialModels.SHAFT_HALF, state, Direction.fromAxisAndDirection(axis, sign)), drive, axis,
						angle, light)
						.renderInto(ms, solid);
		}

		Vec3 velocity = be.getSurfaceVelocity();
		float speed = (float) velocity.horizontalDistance();
		Quaternionf pose = new Quaternionf();
		if (speed > 0) {
			float time = AnimationTickHolder.getRenderTime(be.getLevel());
			float lean = Math.min(MAX_LEAN, speed * 1.5f * Mth.DEG_TO_RAD);
			pose.rotateAxis(lean, (float) velocity.z / speed, 0, (float) -velocity.x / speed)
					.rotateY(time * speed * SPIN_PER_BLOCK);
		}

		VertexConsumer cutout = buffer.getBuffer(RenderType.cutoutMipped());
		for (float[] centre : DISCS)
			CachedBuffers.partial(CAPartialModels.ROLLER_FLOOR_DISC, state)
					.translate(centre[0] / 16, DISC_Y, centre[1] / 16)
					.rotate(pose)
					.light(light)
					.renderInto(ms, cutout);
	}

}
