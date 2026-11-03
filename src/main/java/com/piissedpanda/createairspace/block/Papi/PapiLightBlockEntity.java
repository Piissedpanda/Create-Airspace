package com.piissedpanda.createairspace.block.Papi;

import java.util.List;

import com.piissedpanda.createairspace.CALang;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;

import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class PapiLightBlockEntity extends SmartBlockEntity {

	public static final int DEFAULT_ANGLE = 30;

	protected ScrollValueBehaviour angle;

	// Ponder has no real camera, scenes set the angle they look from
	public float ponderViewAngle = 5;

	public PapiLightBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		angle = new ScrollValueBehaviour(CALang.translate("papi_light.angle")
				.component(), this, new AngleSlot()).between(5, 90)
				.withFormatter(PapiLightBlockEntity::format);
		angle.value = DEFAULT_ANGLE;
		behaviours.add(angle);
	}

	public void setAngle(int tenths) {
		angle.setValue(tenths);
	}

	// Degrees
	public float getAngle() {
		return angle.getValue() / 10f;
	}

	private static String format(int tenths) {
		return tenths / 10 + "." + tenths % 10 + "°";
	}

	@Override
	protected AABB createRenderBoundingBox() {
		return new AABB(worldPosition).inflate(4);
	}

	// On the back of the housing
	private static class AngleSlot extends ValueBoxTransform.Sided {

		@Override
		protected Vec3 getSouthLocation() {
			return VecHelper.voxelSpace(8, 7.5, 14.5);
		}

		@Override
		protected boolean isSideActive(BlockState state, Direction direction) {
			return direction == state.getValue(PapiLightBlock.FACING)
					.getOpposite();
		}

	}

}
