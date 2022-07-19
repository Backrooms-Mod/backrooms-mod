package com.kpabr.backrooms;

import com.kpabr.backrooms.init.*;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BackroomsMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	static public String ModId = "backrooms";
	public static final Logger LOGGER = LoggerFactory.getLogger("backrooms");

	@Override
	public void onInitialize() {
		BackroomsBlocks.init();
		LOGGER.info("Loaded blocks");
		BackroomsGroups.init();
		LOGGER.info("Loaded groups");
		BackroomsItems.init();
		LOGGER.info("Loaded items");

	}
	public static Identifier id(String id) {
		return new Identifier("backrooms", id);
	}
}
