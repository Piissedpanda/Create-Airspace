package com.piissedpanda.createaviation;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;

public class CAPartialModels {

	public static final PartialModel

	BARRIER_POST = block("barrier_post/block"), BARRIER_POST_GUARD = block("barrier_post/guard"),
			WINDSOCK_SOCK = block("windsock/sock");

	private static PartialModel block(String path) {
		return PartialModel.of(CreateAviation.asResource("block/" + path));
	}

	public static void init() {
		// init static fields
	}

}
