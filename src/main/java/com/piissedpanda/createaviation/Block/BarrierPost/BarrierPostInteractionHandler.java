package com.piissedpanda.createaviation.Block.BarrierPost;

import java.util.List;
import java.util.Map.Entry;

import com.google.common.cache.Cache;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.utility.RaycastHelper;
import com.simibubi.create.foundation.utility.TickBasedCache;

import net.createmod.catnip.data.WorldAttached;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.platform.CatnipServices;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.common.Tags;

@EventBusSubscriber(Dist.CLIENT)
public class BarrierPostInteractionHandler {

    public static WorldAttached<Cache<BlockPos, List<BarrierPostShape>>> loadedBelts = new WorldAttached<>(
            $ -> new TickBasedCache<>(60, true));

    public static BlockPos selectedPost;
    public static float selectedBeltPosition;
    public static BlockPos selectedConnection;
    public static Vec3 selectedBakedPosition;
    public static BarrierPostShape selectedShape;

    public static void clientTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            selectedPost = null;
            selectedShape = null;
            selectedConnection = null;
            selectedBakedPosition = null;
            selectedBeltPosition = 0;
            return;
        }

        if (!isActive()) {
            selectedPost = null;
            selectedShape = null;
            selectedConnection = null;
            selectedBakedPosition = null;
            selectedBeltPosition = 0;
            return;
        }

        boolean isWrench = mc.player.isHolding(i -> i.is(Tags.Items.TOOLS_WRENCH));
        boolean dismantling = isWrench && mc.player.isShiftKeyDown();
        boolean isDye = mc.player.isHolding(i -> i.is(Tags.Items.DYES));
        double range = mc.player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE) + 1;

        Vec3 from = mc.player.getEyePosition();
        Vec3 to = RaycastHelper.getTraceTarget(mc.player, range, from);
        HitResult hitResult = mc.hitResult;

        double bestDiff = Float.MAX_VALUE;
        if (hitResult != null)
            bestDiff = hitResult.getLocation()
                    .distanceToSqr(from);

        BlockPos bestPost = null;
        BarrierPostShape bestShape = null;
        selectedConnection = null;

        for (Entry<BlockPos, List<BarrierPostShape>> entry : loadedBelts.get(Minecraft.getInstance().level)
                .asMap()
                .entrySet()) {
            BlockPos postPos = entry.getKey();
            for (BarrierPostShape shape : entry.getValue()) {
                if (shape instanceof BarrierPostShape.BarrierPostBB && dismantling)
                    continue;
                Vec3 postVec = Vec3.atLowerCornerOf(postPos);
                Vec3 intersect = shape.intersect(from.subtract(postVec), to.subtract(postVec));
                if (intersect == null)
                    continue;

                double distanceToSqr = intersect.add(postVec)
                        .distanceToSqr(from);
                if (distanceToSqr > bestDiff)
                    continue;
                bestDiff = distanceToSqr;
                bestPost = postPos;
                bestShape = shape;
                selectedBeltPosition = shape.getBeltPosition(intersect);
                if (shape instanceof BarrierPostShape.BarrierPostOBB obb)
                    selectedConnection = obb.connection;
            }
        }

        selectedPost = bestPost;
        if (bestPost == null) {
            selectedShape = null;
            selectedBakedPosition = null;
            selectedConnection = null;
            return;
        }

        selectedShape = bestShape;
        selectedBakedPosition = bestShape.getVec(bestPost, selectedBeltPosition);

        if (!isWrench && !isDye) {
            Outliner.getInstance()
                    .chaseAABB("BeltPointSelection", new AABB(selectedBakedPosition, selectedBakedPosition))
                    .colored(Color.WHITE)
                    .lineWidth(1 / 6f)
                    .disableLineNormals();
        }
    }

    private static boolean isActive() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null)
            return false;

        return mc.player.isHolding(i -> i.is(Tags.Items.TOOLS_WRENCH))
                || mc.player.isHolding(i -> i.is(Tags.Items.DYES));
    }

    public static boolean onUse() {
        if (selectedPost == null)
            return false;

        Minecraft mc = Minecraft.getInstance();
        ItemStack mainHandItem = mc.player.getMainHandItem();
        ItemStack offHandItem = mc.player.getOffhandItem();
        ItemStack usedItem;
        if (!mainHandItem.isEmpty() && (mainHandItem.is(Tags.Items.TOOLS_WRENCH) || mainHandItem.is(Tags.Items.DYES))) {
            usedItem = mainHandItem;
        } else if (!offHandItem.isEmpty()
                && (offHandItem.is(Tags.Items.TOOLS_WRENCH) || offHandItem.is(Tags.Items.DYES))) {
            usedItem = offHandItem;
        } else {
            return false;
        }

        if (mc.player.isHolding(stack -> stack.is(Tags.Items.TOOLS_WRENCH))) {
            if (!mc.player.isShiftKeyDown()) {
                return false;
            }

            if (selectedConnection == null) {
                return false;
            }

            CatnipServices.NETWORK.sendToServer(
                    new BarrierPostConnectionPacket(selectedPost, selectedPost.offset(selectedConnection),
                            usedItem, false));
            return true;
        } else if (mc.player.isHolding(stack -> stack.is(Tags.Items.DYES))) {
            if (selectedConnection == null) {
                return false;
            }
            CatnipServices.NETWORK.sendToServer(
                    new BarrierPostDyeingPacket(selectedPost, selectedPost.offset(selectedConnection), usedItem,
                            DyeColor.getColor(usedItem)));
            return true;
        }

        return false;
    }

    public static void drawCustomBlockSelection(PoseStack ms, MultiBufferSource buffer, Vec3 camera) {
        if (selectedPost == null || selectedShape == null)
            return;

        VertexConsumer vb = buffer.getBuffer(RenderType.lines());
        ms.pushPose();
        ms.translate(selectedPost.getX() - camera.x, selectedPost.getY() - camera.y, selectedPost.getZ() - camera.z);
        selectedShape.drawOutline(selectedPost, ms, vb);
        ms.popPose();
    }

    @SubscribeEvent
    public static void hideVanillaBlockSelection(RenderHighlightEvent.Block event) {
        if (selectedPost == null || selectedShape == null)
            return;

        event.setCanceled(true);
    }

}
