package com.piissedpanda.createaviation.foundation.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import java.util.function.Function;

import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class RenderTypes extends RenderStateShard {
	private static final Function<ResourceLocation, RenderType> BELT = Util.memoize((p_234330_) -> {
		return RenderType.create("barrier_post_belt", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 256, false,
				true, RenderType.CompositeState.builder()
						.setShaderState(RENDERTYPE_CUTOUT_MIPPED_SHADER)
						.setTextureState(new RenderStateShard.TextureStateShard(p_234330_, false, true))
						.setTransparencyState(NO_TRANSPARENCY)
						.setWriteMaskState(COLOR_DEPTH_WRITE)
						.setLightmapState(LIGHTMAP)
						.setOverlayState(OVERLAY)
						.createCompositeState(false));
	});

	// Additive light without depth, so clouds, water and entities behind it stay
	// visible
	private static final RenderType BEACON_BEAM = RenderType.create("airport_beacon_beam",
			DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, true,
			RenderType.CompositeState.builder()
					.setShaderState(RENDERTYPE_LIGHTNING_SHADER)
					.setTransparencyState(LIGHTNING_TRANSPARENCY)
					.setWriteMaskState(COLOR_WRITE)
					.setCullState(NO_CULL)
					.setOutputState(TRANSLUCENT_TARGET)
					.createCompositeState(false));

	public static RenderType beaconBeam() {
		return BEACON_BEAM;
	}

	public static RenderType belt(ResourceLocation pLocation) {
		return BELT.apply(pLocation);
	}

	private RenderTypes() {
		super(null, null, null);
	}
}
