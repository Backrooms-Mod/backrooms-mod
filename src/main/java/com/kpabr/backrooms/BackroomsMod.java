package com.kpabr.backrooms;


import com.kpabr.backrooms.client.render.sky.StrongLiminalShader;
import com.kpabr.backrooms.config.BackroomsConfig;
import com.kpabr.backrooms.init.*;
import net.fabricmc.api.ModInitializer;
import com.kpabr.backrooms.init.BackroomsBlocks;
import com.kpabr.backrooms.init.BackroomsGroups;
import com.kpabr.backrooms.init.BackroomsItems;
import net.ludocrypt.limlib.impl.LimlibRegistries;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackroomsMod implements ModInitializer {

	static public String ModId = "backrooms";
	public static final Logger LOGGER = LoggerFactory.getLogger("backrooms");

	@Override
	public void onInitialize() {
		BackroomsConfig.init();
		LOGGER.info("Loaded config");
		BackroomsBlocks.init();
		LOGGER.info("Loaded blocks");
		BackroomsGroups.init();
		LOGGER.info("Loaded groups");
		BackroomsItems.init();
		LOGGER.info("Loaded items");
		BackroomsLevels.init();
		LOGGER.info("Loaded levels");
		Registry.register(LimlibRegistries.LIMINAL_SHADER_APPLIER, id("stong_simple_shader"), StrongLiminalShader.CODEC);

	}

	/*@Override
	public void registerModDimensions(Map<Identifier, ExtraDimension> registry) {
		registry.put(Level0.LEVEL_0_ID, new Level0());
	}*/

	public static Identifier id(String id) {
		return new Identifier("backrooms", id);
	}

}
