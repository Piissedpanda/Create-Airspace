package com.piissedpanda.createairspace.events;

import com.piissedpanda.createairspace.block.BarrierPost.BarrierPostConnectionHandler;
import com.piissedpanda.createairspace.block.BarrierPost.BarrierPostInteractionHandler;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;

@EventBusSubscriber(Dist.CLIENT)
public class InputEvents {

    @SubscribeEvent
    public static void onClickInput(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null)
            return;

        KeyMapping key = event.getKeyMapping();

        if (key == mc.options.keyUse
                && (BarrierPostConnectionHandler.onRightClick())) {
            event.setCanceled(true);
            return;
        }

        if (!event.isUseItem())
            return;

        if (BarrierPostInteractionHandler.onUse()) {
            event.setCanceled(true);
            return;
        }
    }
}
