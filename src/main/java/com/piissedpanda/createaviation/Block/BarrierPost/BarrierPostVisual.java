package com.piissedpanda.createaviation.Block.BarrierPost;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.piissedpanda.createaviation.CAPartialModels;
import com.simibubi.create.foundation.render.SpecialModels;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

public class BarrierPostVisual extends AbstractBlockEntityVisual<BarrierPostBlockEntity>
        implements SimpleDynamicVisual {

    private final List<TransformedInstance> instances = new ArrayList<>();

    private final BarrierPostBlockEntity blockEntity;
    private int lastVisualUpdateVersion = -1;

    public BarrierPostVisual(VisualizationContext context, BarrierPostBlockEntity blockEntity,
            float partialTick) {
        super(context, blockEntity, partialTick);

        this.blockEntity = blockEntity;

        setupGuards();
    }

    @Override
    public void beginFrame(DynamicVisual.Context context) {
        if (blockEntity.visualUpdateVersion == lastVisualUpdateVersion)
            return;

        setupGuards();
        lastVisualUpdateVersion = blockEntity.visualUpdateVersion;
    }

    private void deleteGuards() {
        for (TransformedInstance guard : instances) {
            guard.delete();
        }
        instances.clear();
    }

    private void setupGuards() {
        deleteGuards();

        var guardInstancer = instancerProvider().instancer(InstanceTypes.TRANSFORMED,
                SpecialModels.chunkDiffuse(CAPartialModels.BARRIER_POST_GUARD));

        Level level = blockEntity.getLevel();
        if (level == null)
            return;

        BlockPos pos = blockEntity.getBlockPos();

        int light = LightTexture.pack(
                level.getBrightness(LightLayer.BLOCK, pos),
                level.getBrightness(LightLayer.SKY, pos));

        for (BlockPos blockPos : blockEntity.connections) {
            BarrierPostBlockEntity.ConnectionStats stats = blockEntity.connectionStats.get(blockPos);
            if (stats == null) {
                continue;
            }

            Vec3 diff = stats.end()
                    .subtract(stats.start());
            double yaw = Mth.RAD_TO_DEG * Mth.atan2(diff.x, diff.z);

            TransformedInstance guard = guardInstancer.createInstance();
            guard.translate(getVisualPosition())
                    .center()
                    .rotateYDegrees((float) yaw)
                    .uncenter()
                    .light(light)
                    .setChanged();

            instances.add(guard);
        }
    }

    @Override
    public void updateLight(float partialTick) {
        for (TransformedInstance instance : instances) {
            relight(instance);
        }
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        for (TransformedInstance instance : instances)
            consumer.accept(instance);
    }

    @Override
    protected void _delete() {
        deleteGuards();
    }

}
