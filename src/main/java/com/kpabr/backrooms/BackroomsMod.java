package com.kpabr.backrooms;


import com.kpabr.backrooms.client.render.sky.StrongLiminalShader;
import com.kpabr.backrooms.component.WretchedComponent;
import com.kpabr.backrooms.config.BackroomsConfig;
import com.kpabr.backrooms.init.*;
import net.fabricmc.api.ModInitializer;
import com.kpabr.backrooms.init.BackroomsBlocks;
import com.kpabr.backrooms.init.BackroomsGroups;
import com.kpabr.backrooms.init.BackroomsItems;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.ludocrypt.limlib.impl.LimlibRegistries;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.core.jmx.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.kpabr.backrooms.BackroomsComponents.WRETCHED;

public class BackroomsMod implements ModInitializer {

	static public String ModId = "backrooms";
	public static final Logger LOGGER = LoggerFactory.getLogger("backrooms");

	@Override
	public void onInitialize() {
		BackroomsConfig.init();
		LOGGER.info("Loaded config");
		BackroomsSoundEvents.init();
		LOGGER.info("Loaded sounds");
		BackroomsParticles.init();
		LOGGER.info("Loaded particles");
		BackroomsBlocks.init();
		LOGGER.info("Loaded blocks");
		BackroomsGroups.init();
		LOGGER.info("Loaded groups");
		BackroomsItems.init();
		LOGGER.info("Loaded items");
		BackroomsProjectiles.init();
		LOGGER.info("Loaded Projectiles, pew pew");
		BackroomsLevels.init();
		LOGGER.info("Loaded levels");
		BackroomsFlammableBlocks.init();
		LOGGER.info("loaded FIRE");
		BackroomsEntities.init();
		LOGGER.info("loaded your nightmares");
		Registry.register(LimlibRegistries.LIMINAL_SHADER_APPLIER, id("stong_simple_shader"), StrongLiminalShader.CODEC);
		LOGGER.info("Everything is loaded !");


		ServerTickEvents.END_SERVER_TICK.register((server) -> {
			List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();

			for (ServerPlayerEntity player : players) {
				if(player.age % 400 == 0 && player.age != 0) { // 400 = 20tps*20s
					WretchedComponent wretched = WRETCHED.get(player);
					wretched.increment();
					LOGGER.info(String.valueOf(wretched.getValue())); // debugging reasons
				}
			}
		});
	}

	/*@Override
	public void registerModDimensions(Map<Identifier, ExtraDimension> registry) {
		registry.put(Level0.LEVEL_0_ID, new Level0());
	}*/

	public static Identifier id(String id) {
		return new Identifier("backrooms", id);
	}

}
