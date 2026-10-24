package com.piissedpanda.createairspace.block.GroundCrew;

import com.piissedpanda.createairspace.CAItems;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import top.theillusivec4.curios.api.CuriosApi;

// Only loaded when Curios is installed: ear defenders also count in the Curios head slot
public class EarDefendersCurios {

	public static void init(IEventBus modEventBus) {
		EarDefendersItem.addIsWearingPredicate(player -> CuriosApi.getCuriosInventory(player)
				.map(curios -> curios.isEquipped(CAItems.EAR_DEFENDERS.get()))
				.orElse(false));

		if (FMLEnvironment.dist.isClient())
			EarDefendersCurioRenderer.init(modEventBus);
	}

}
