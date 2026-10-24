package com.piissedpanda.createairspace.block.GroundCrew;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.piissedpanda.createairspace.CAItems;

import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.IArmPoseTransformer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

// While right-click is held, every arm holding a wand goes up and waves
public class MarshallingWandClient {

	public static final EnumProxy<HumanoidModel.ArmPose> WAVE = new EnumProxy<>(HumanoidModel.ArmPose.class, false,
			(IArmPoseTransformer) MarshallingWandClient::wave);

	public static void register(RegisterClientExtensionsEvent event) {
		event.registerItem(new Extensions(), CAItems.MARSHALLING_WAND.get());
	}

	private static float waveAngle(float time) {
		return 0.25f + 0.35f * (Mth.sin(time * 0.45f) + 1) / 2;
	}

	private static boolean isWaving(LivingEntity entity) {
		return entity.isUsingItem() && entity.getUseItem()
				.is(CAItems.MARSHALLING_WAND.get());
	}

	private static void wave(HumanoidModel<?> model, LivingEntity entity, HumanoidArm arm) {
		ModelPart part = arm == HumanoidArm.RIGHT ? model.rightArm : model.leftArm;
		float side = arm == HumanoidArm.RIGHT ? 1 : -1;
		part.xRot = -Mth.PI + 0.25f;
		part.yRot = 0;
		part.zRot = side * waveAngle(AnimationTickHolder.getRenderTime());
	}

	private static class Extensions implements IClientItemExtensions {

		@Override
		public HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack stack) {
			return isWaving(entity) ? WAVE.getValue() : null;
		}

		// First person: the wand swings sideways in front of the camera
		@Override
		public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm,
				ItemStack itemInHand, float partialTick, float equipProcess, float swingProcess) {
			if (!isWaving(player))
				return false;
			float side = arm == HumanoidArm.RIGHT ? 1 : -1;
			poseStack.translate(side * 0.56f, -0.52f, -0.72f);
			poseStack.mulPose(Axis.ZP.rotation(-side * (waveAngle(AnimationTickHolder.getRenderTime()) - 0.4f)));
			return true;
		}
	}

}
