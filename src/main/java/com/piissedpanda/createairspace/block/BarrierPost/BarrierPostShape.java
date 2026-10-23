package com.piissedpanda.createairspace.block.BarrierPost;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.piissedpanda.createairspace.CAShapes;
import com.simibubi.create.content.trains.track.TrackBlockOutline;

import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class BarrierPostShape {

    @Nullable
    public abstract Vec3 intersect(Vec3 from, Vec3 to);

    public abstract float getBeltPosition(Vec3 intersection);

    protected abstract void drawOutline(BlockPos anchor, PoseStack ms, VertexConsumer vb);

    public abstract Vec3 getVec(BlockPos anchor, float position);

    public static class BarrierPostOBB extends BarrierPostShape {

        BlockPos connection;
        double yaw, pitch;
        AABB bounds;
        Vec3 pivot;
        final double radius = 0.175;
        VoxelShape voxelShape;

        Vec3[] linePoints;

        public BarrierPostOBB(BlockPos connection, Vec3 start, Vec3 end) {
            this.connection = connection;
            Vec3 diff = end.subtract(start);
            double d = diff.length();
            double dxz = diff.multiply(1, 0, 1)
                    .length();
            yaw = Mth.RAD_TO_DEG * Mth.atan2(diff.x, diff.z);
            pitch = Mth.RAD_TO_DEG * Mth.atan2(-diff.y, dxz);
            bounds = new AABB(start, start).expandTowards(new Vec3(0, 0, d))
                    .inflate(radius, radius, 0);
            pivot = start;
            voxelShape = Shapes.create(bounds);
        }

        @Override
        public Vec3 intersect(Vec3 from, Vec3 to) {
            from = counterTransform(from);
            to = counterTransform(to);

            Vec3 result = bounds.clip(from, to)
                    .orElse(null);
            if (result == null)
                return null;

            result = transform(result);
            return result;
        }

        private Vec3 counterTransform(Vec3 from) {
            from = from.subtract(pivot);
            from = VecHelper.rotate(from, -yaw, Axis.Y);
            from = VecHelper.rotate(from, -pitch, Axis.X);
            from = from.add(pivot);
            return from;
        }

        private Vec3 transform(Vec3 result) {
            result = result.subtract(pivot);
            result = VecHelper.rotate(result, pitch, Axis.X);
            result = VecHelper.rotate(result, yaw, Axis.Y);
            result = result.add(pivot);
            return result;
        }

        @Override
        public void drawOutline(BlockPos anchor, PoseStack ms, VertexConsumer vb) {
            TransformStack.of(ms)
                    .translate(pivot)
                    .rotateYDegrees((float) yaw)
                    .rotateXDegrees((float) pitch)
                    .translateBack(pivot);
            TrackBlockOutline.renderShape(voxelShape, ms, vb, null);
        }

        @Override
        public float getBeltPosition(Vec3 intersection) {
            int dots = (int) Math.round(Vec3.atLowerCornerOf(connection)
                    .length() - 3);
            double length = bounds.getZsize();
            double selection = Math.min(bounds.getZsize(), intersection.distanceTo(pivot));

            double margin = length - dots;
            selection = Mth.clamp(selection - margin, 0, length - margin * 2);
            selection = Math.round(selection);

            return (float) (selection + margin + 0.025);
        }

        @Override
        public Vec3 getVec(BlockPos anchor, float position) {
            float x = (float) bounds.getCenter().x;
            float y = (float) bounds.getCenter().y;
            Vec3 from = new Vec3(x, y, bounds.minZ);
            Vec3 to = new Vec3(x, y, bounds.maxZ);
            Vec3 point = from.lerp(to, Mth.clamp(position / from.distanceTo(to), 0, 1));
            point = transform(point);
            return point.add(Vec3.atLowerCornerOf(anchor));
        }
    }

    public static class BarrierPostBB extends BarrierPostShape {

        AABB bounds;

        public BarrierPostBB(Vec3 center) {
            bounds = new AABB(
                    center.x - 0.25,
                    center.y,
                    center.z - 0.25,
                    center.x + 0.25,
                    center.y + 1.25,
                    center.z + 0.25);
        }

        @Override
        public Vec3 intersect(Vec3 from, Vec3 to) {
            return bounds.clip(from, to)
                    .orElse(null);
        }

        @Override
        public void drawOutline(BlockPos anchor, PoseStack ms, VertexConsumer vb) {
            TrackBlockOutline.renderShape(CAShapes.BARRIER_POST_INTERACTION, ms, vb, null);
        }

        @Override
        public float getBeltPosition(Vec3 intersection) {
            return 0;
        }

        @Override
        public Vec3 getVec(BlockPos anchor, float position) {
            return bounds.getCenter()
                    .add(Vec3.atLowerCornerOf(anchor))
                    .add(0, -0.125, 0);
        }

    }

}
