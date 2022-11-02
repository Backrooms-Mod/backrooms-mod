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
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

import static com.kpabr.backrooms.BackroomsComponents.WRETCHED;

public class BackroomsMod implements ModInitializer {

	static public String ModId = "backrooms";
	public static final Logger LOGGER = LoggerFactory.getLogger("backrooms");

	@Override
	public void onInitialize() {
		BackroomsConfig.init();
		LOGGER.info("Loaded config");
		BackroomsSounds.init();
		LOGGER.info("Loaded sounds");
		BackroomsParticles.init();
		LOGGER.info("Loaded particles");
		BackroomStatusEffects.init();
		LOGGER.info("Loaded status effects");
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

		// registering every tick event
		ServerTickEvents.END_SERVER_TICK.register((server) -> {
			List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();

			for (ServerPlayerEntity player : players) {
				// Iterating through every player
				// And check if they're on the server for at least (wretchedCycleStepTime) seconds
				if((player.age % (20*BackroomsConfig.getInstance().wretchedCycleStepTime)) == 0 && player.age != 0) {
                    applyWretchedCycle(player);
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

	public static void applyWretchedCycle(ServerPlayerEntity player) {
		if(player.interactionManager.getGameMode().equals(GameMode.CREATIVE) || player.interactionManager.getGameMode().equals(GameMode.SPECTATOR)) {
			return;
		}

		if(!Objects.equals(player.getWorld().getRegistryKey().getValue().getNamespace(), "backrooms")) {
			if(Objects.equals(player.getWorld().getRegistryKey().getValue().getNamespace(), "minecraft")) {
				WretchedComponent wretched = WRETCHED.get(player);
				if(wretched.getValue() <= 0) {
					return;
				}
				wretched.decrement();
				return;
			}
		}

		WretchedComponent wretched = WRETCHED.get(player);
		if(wretched.getValue() >= 100) {
			return;
		}
		wretched.increment();
		if(wretched.getValue() >= 24 && wretched.getValue() <= 49 && !player.hasStatusEffect(BackroomStatusEffects.RAGGED)) {
			player.addStatusEffect(new StatusEffectInstance(BackroomStatusEffects.RAGGED, 9999999));

		} else if(wretched.getValue() >= 50 && wretched.getValue() <= 74 && !player.hasStatusEffect(BackroomStatusEffects.ROTTEN)) {
			player.removeStatusEffect(BackroomStatusEffects.RAGGED);
			player.addStatusEffect(new StatusEffectInstance(BackroomStatusEffects.ROTTEN, 9999999));

		} else if(wretched.getValue() >= 75 && !player.hasStatusEffect(BackroomStatusEffects.WRETCHED)) {
			player.removeStatusEffect(BackroomStatusEffects.RAGGED);
			player.removeStatusEffect(BackroomStatusEffects.ROTTEN);
			player.addStatusEffect(new StatusEffectInstance(BackroomStatusEffects.WRETCHED, 9999999));
		}
		LOGGER.info(String.valueOf(wretched.getValue())); // debugging reasons
	}
}
