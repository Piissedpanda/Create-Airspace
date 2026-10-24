package com.piissedpanda.createairspace.block.GroundCrew;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.piissedpanda.createairspace.CAItems;
import com.piissedpanda.createairspace.CreateAirspace;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.client.ICurioRenderer;

// Draws ear defenders worn in the Curios head slot, like Create's goggles
public class EarDefendersCurioRenderer implements ICurioRenderer {

	public static final ModelLayerLocation LAYER = new ModelLayerLocation(
			CreateAirspace.asResource("ear_defenders"), "ear_defenders");

	private final HumanoidModel<LivingEntity> model;

	public EarDefendersCurioRenderer(ModelPart part) {
		this.model = new HumanoidModel<>(part);
	}

	public static void init(IEventBus modEventBus) {
		modEventBus.addListener(EarDefendersCurioRenderer::onClientSetup);
		modEventBus.addListener(EarDefendersCurioRenderer::onRegisterLayers);
	}

	private static void onClientSetup(FMLClientSetupEvent event) {
		CuriosRendererRegistry.register(CAItems.EAR_DEFENDERS.get(), () -> new EarDefendersCurioRenderer(
				Minecraft.getInstance()
						.getEntityModels()
						.bakeLayer(LAYER)));
	}

	private static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(LAYER, () -> {
			MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, 0);
			mesh.getRoot()
					.addOrReplaceChild("head", new CubeListBuilder(), PartPose.ZERO);
			return LayerDefinition.create(mesh, 1, 1);
		});
	}

	@Override
	public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext,
			PoseStack ms, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource buffer, int light,
			float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw,
			float headPitch) {
		LivingEntity entity = slotContext.entity();
		model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
		ICurioRenderer.followHeadRotations(entity, model.head);

		ms.pushPose();
		ms.translate(model.head.x / 16.0, model.head.y / 16.0, model.head.z / 16.0);
		ms.mulPose(Axis.ZP.rotation(model.head.zRot));
		ms.mulPose(Axis.YP.rotation(model.head.yRot));
		ms.mulPose(Axis.XP.rotation(model.head.xRot));
		ms.translate(0, -0.25, 0);
		ms.mulPose(Axis.YP.rotationDegrees(180));
		ms.scale(0.625f, -0.625f, -0.625f);

		Minecraft mc = Minecraft.getInstance();
		mc.getItemRenderer()
				.renderStatic(stack, ItemDisplayContext.HEAD, light, OverlayTexture.NO_OVERLAY, ms, buffer, mc.level,
						0);
		ms.popPose();
	}

}
