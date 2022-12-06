package com.kpabr.backrooms;


import com.kpabr.backrooms.client.render.sky.StrongLiminalShader;
import com.kpabr.backrooms.component.WretchedComponent;
import com.kpabr.backrooms.config.BackroomsConfig;
import com.kpabr.backrooms.entity.living.HoundLivingEntity;
import com.kpabr.backrooms.entity.living.WretchEntity;
import com.kpabr.backrooms.init.*;
import net.fabricmc.api.ModInitializer;
import com.kpabr.backrooms.init.BackroomsBlocks;
import com.kpabr.backrooms.init.BackroomsGroups;
import com.kpabr.backrooms.init.BackroomsItems;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.ludocrypt.limlib.impl.LimlibRegistries;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		BackroomsFluids.init();
		LOGGER.info("Loaded fluids");
		BackroomsGroups.init();
		LOGGER.info("Loaded groups");
		BackroomsItems.init();
		LOGGER.info("Loaded items");
		BackroomsEntities.init();
		LOGGER.info("loaded your nightmares");
		BackroomsLevels.init();
		LOGGER.info("Loaded levels");
		Registry.register(LimlibRegistries.LIMINAL_SHADER_APPLIER, id("stong_simple_shader"), StrongLiminalShader.CODEC);
		LOGGER.info("Everything is loaded !");
		// registering every tick event
		ServerTickEvents.END_SERVER_TICK.register((server) -> {
			// Iterating through every player
			// And check if they're on the server for at least (wretchedCycleStepTime) seconds
			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				if((player.age % (20 * BackroomsConfig.getInstance().wretchedCycleStepTime)) == 0 && player.age != 0) {
                    applyWretchedCycle(player);
				}
			}
		});
	}

	public static Identifier id(String name) {
		return new Identifier("backrooms", name);
	}

	public static void applyWretchedCycle(ServerPlayerEntity player) {
		if(player.interactionManager.getGameMode() == GameMode.CREATIVE || player.interactionManager.getGameMode() == GameMode.SPECTATOR) {
			return;
		}
		WretchedComponent wretched = WRETCHED.get(player);

		if(player.getWorld().getRegistryKey().getValue().getNamespace().equals("backrooms") && wretched.increment()) {
			wretched.remove(100);
			BackroomsEntities.WRETCH.spawn(player.getWorld(), null, null, player, player.getBlockPos(), SpawnReason.MOB_SUMMONED, false, false);
			player.damage(BackroomsDamageSource.WRETCHED_CYCLE_DEATH, Float.MAX_VALUE);
			return;
		} else {
			wretched.decrement();
		}

		if(wretched.getValue() >= 24 && wretched.getValue() < 50 && !player.hasStatusEffect(BackroomStatusEffects.RAGGED)) {
			player.addStatusEffect(new StatusEffectInstance(BackroomStatusEffects.RAGGED, 9999999));
		} else if(wretched.getValue() >= 50 && wretched.getValue() < 75 && !player.hasStatusEffect(BackroomStatusEffects.ROTTEN)) {
			player.removeStatusEffect(BackroomStatusEffects.RAGGED);
			player.addStatusEffect(new StatusEffectInstance(BackroomStatusEffects.ROTTEN, 9999999));
		} else if(wretched.getValue() >= 75 && !player.hasStatusEffect(BackroomStatusEffects.WRETCHED)) {
			player.removeStatusEffect(BackroomStatusEffects.RAGGED);
			player.removeStatusEffect(BackroomStatusEffects.ROTTEN);
			player.addStatusEffect(new StatusEffectInstance(BackroomStatusEffects.WRETCHED, 9999999));
		}
	}
}
