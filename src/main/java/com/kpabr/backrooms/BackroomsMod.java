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
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.example.GeckoLibMod;

import static com.kpabr.backrooms.BackroomsComponents.WRETCHED;

public class BackroomsMod implements ModInitializer {

	static public String ModId = "backrooms";
	public static final Logger LOGGER = LoggerFactory.getLogger("backrooms");

	@Override
	public void onInitialize() {
		BackroomsConfig.init();
		BackroomsSounds.init();
		BackroomsParticles.init();
		BackroomStatusEffects.init();
		BackroomsProjectiles.init();
		BackroomsFluids.init();
		BackroomsBlocks.init();
		BackroomsGroups.init();
		BackroomsItems.init();
		BackroomsEntities.init();
		BackroomsLevels.init();
		Registry.register(LimlibRegistries.LIMINAL_SHADER_APPLIER, id("stong_simple_shader"), StrongLiminalShader.CODEC);
		LOGGER.info("Backrooms mod was loaded!");
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
			if (player.hasStatusEffect(BackroomStatusEffects.RAGGED)) {
				player.removeStatusEffect(BackroomStatusEffects.RAGGED);
			} else if (player.hasStatusEffect(BackroomStatusEffects.ROTTEN)) {
				player.removeStatusEffect(BackroomStatusEffects.ROTTEN);
			} else if (player.hasStatusEffect(BackroomStatusEffects.WRETCHED)) {
				player.removeStatusEffect(BackroomStatusEffects.WRETCHED);
			}
			return;
		}
		WretchedComponent wretched = WRETCHED.get(player);

		final var currentWorld = player.getWorld().getRegistryKey().getValue().getNamespace();
		if(currentWorld.equals("backrooms") && player.isAlive()) {
			if(wretched.increment()) {
				wretched.remove(100);
				BackroomsEntities.WRETCH.spawn(player.getWorld(), null, null, player, player.getBlockPos(), SpawnReason.MOB_SUMMONED, false, false);
				player.damage(BackroomsDamageSource.WRETCHED_CYCLE_DEATH, Float.MAX_VALUE);
				player.removeStatusEffect(BackroomStatusEffects.WRETCHED);
				return;
			}
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

	static {
		GeckoLibMod.DISABLE_IN_DEV = true;
	}
}
