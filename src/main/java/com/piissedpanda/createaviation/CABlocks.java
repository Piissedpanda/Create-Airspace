package com.piissedpanda.createaviation;

import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;

import com.piissedpanda.createaviation.Block.BarrierPost.BarrierPostBlock;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;

import net.minecraft.world.level.material.MapColor;

public class CABlocks {
        private static final CreateRegistrate REGISTRATE = CreateAviation.REGISTRATE;

        static {
                REGISTRATE.setCreativeTab(CACreativeModeTabs.BASE_TAB);
        }

        public static final BlockEntry<BarrierPostBlock> BARRIER_POST = REGISTRATE
                        .block("barrier_post", BarrierPostBlock::new)
                        .initialProperties(SharedProperties::stone)
                        .properties(p -> p.noOcclusion()
                                        .mapColor(MapColor.PODZOL))
                        .transform(axeOrPickaxe())
                        .blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p)))
                        .item()
                        .transform(customItemModel())
                        .register();

        public static void register() {
        }
}
