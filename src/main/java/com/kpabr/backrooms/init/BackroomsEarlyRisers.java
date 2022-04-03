package com.kpabr.backrooms.init;

import com.chocohead.mm.api.ClassTinkerers;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;

public class BackroomsEarlyRisers implements Runnable {

	public static final String LEVEL_0_KEY = "level0";

	@Override
	public void run() {
		MappingResolver remapper = FabricLoader.getInstance().getMappingResolver();

		// Biome Types
		ClassTinkerers.enumBuilder(remapper.mapClassName("intermediary", "net.minecraft.class_1959$class_1961"), String.class).addEnum(LEVEL_0_KEY, () -> new Object[] { LEVEL_0_KEY }).build();
	}
}
