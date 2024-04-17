package com.kpabr.backrooms.config;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.Random;

import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.gamerule.v1.rule.EnumRule;


public class BackroomsGamerules {

    // Register Enum game rule, appears on the world creation screen under the backrooms category and command /gamerule respawn
    public static final GameRules.Key<EnumRule<Respawn>> RESPAWN =
            GameRuleRegistry.register("respawn", new CustomGameRuleCategory(new Identifier("backrooms", "backrooms_category"), Text.of("Backrooms")), GameRuleFactory.createEnumRule(Respawn.natural, (server, rule) -> {
                onRespawnGameruleChanged(server, rule.get());
            }));

    
    public static void init() {
        // ENTITY_LOAD is called whenever an entity is loaded (new chunk, natural spawn, player joins world, player respawns)
        // When the entity is a player and RESPAWN is set to natural, reset the SpawnPoint overwrite
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof ServerPlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) entity;
                if (world.getGameRules().get(RESPAWN).get() == Respawn.natural) {
                    player.setSpawnPoint(null, null, 0f, false, false);
                }
                else {
                    setPlayerSpawnpoint(player);
                } 
                
            }
        });

        // called when an entity is leaving the bed
        EntitySleepEvents.STOP_SLEEPING.register((LivingEntity, BlockPos) -> {
            // do nothing when natural is set, so the bed is the new SpawnPoint
            if (LivingEntity.getWorld().getGameRules().get(RESPAWN).get() == Respawn.natural) {
                return;
            } else {
                if (LivingEntity instanceof ServerPlayerEntity) {
                    setPlayerSpawnpoint((ServerPlayerEntity) LivingEntity);
                }
            }

        });
    }

    // when the rule is changed, iterate over every player and set the right spawn or reset the overwrite
    private static void onRespawnGameruleChanged(MinecraftServer server, Respawn respawnType) {
        
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {

            if (player.getWorld().getGameRules().get(RESPAWN).get() != Respawn.natural) {
                setPlayerSpawnpoint(player);
            } else {
                player.setSpawnPoint(null, null, 0f, false, false);
            }
        }
    }

    // sets the spawn according to RESPAWN, only when player is inside a backrooms level
    private static void setPlayerSpawnpoint(ServerPlayerEntity player) {
        Respawn respawnPolicy = player.getWorld().getGameRules().get(RESPAWN).get();
        if (player.getWorld().getRegistryKey().getValue().toString().contains("backrooms") && respawnPolicy == Respawn.in_current_level) {
            setSpawnpointToCurrentLevel(player);
        } else if (player.getWorld().getRegistryKey().getValue().toString().contains("backrooms") && respawnPolicy == Respawn.in_level_zero) {
            setSpawnpointToLevel0(player);
        }
    }


    private static void setSpawnpointToCurrentLevel(ServerPlayerEntity player) {
        Random rand = player.getWorld().getRandom();

        // random position in the level
        int newX = (rand.nextInt(25) * 16) + rand.nextInt(16);
        int newZ = (rand.nextInt(25) * 16) + rand.nextInt(16);
        // uses current y of the player so the respawn point is inside the level and not over or under it
        int newY = player.getBlockY();

        BlockPos.Mutable mutBlockPos = new BlockPos(newX, newY, newZ).mutableCopy();
    
        mutBlockPos = new BlockPos(newX, newY, newZ).mutableCopy();
        boolean up = false;
        // searches for a place inside the level where two blocks are air so the player can spawn
        while (!(player.getWorld().isAir(mutBlockPos) && player.getWorld().isAir(mutBlockPos.up()))) {
            mutBlockPos.move(Direction.SOUTH).move(Direction.EAST);
            if (up) {
                mutBlockPos.move(Direction.UP);
            } else {
                mutBlockPos.move(Direction.DOWN);
            }
            up = !up;
        }
        
        player.setSpawnPoint(player.getEntityWorld().getRegistryKey(), mutBlockPos.toImmutable(), 0f, true, false);
    }

    private static void setSpawnpointToLevel0(ServerPlayerEntity player) {
        Random rand = player.getWorld().getRandom();

        World level_0 = player.getServer().getWorld(
            RegistryKey.of(
                    Registry.WORLD_KEY,
                    new Identifier("backrooms:level_0")));

        // random position in the level
        int newX = (rand.nextInt(25) * 16) + rand.nextInt(16);
        int newZ = (rand.nextInt(25) * 16) + rand.nextInt(16);
        // y fixed to 26
        int newY = 26;

        BlockPos.Mutable mutBlockPos = new BlockPos(newX, newY, newZ).mutableCopy();
    
        mutBlockPos = new BlockPos(newX, newY, newZ).mutableCopy();
        boolean up = false;
        // searches for a place inside level 0 where two blocks are air so the player can spawn
        while (!(level_0.isAir(mutBlockPos) && level_0.isAir(mutBlockPos.up()))) {
            mutBlockPos.move(Direction.SOUTH).move(Direction.EAST);
            if (up) {
                mutBlockPos.move(Direction.UP);
            } else {
                mutBlockPos.move(Direction.DOWN);
            }
            up = !up;
        }

        player.setSpawnPoint(level_0.getRegistryKey(), mutBlockPos.toImmutable(), 0f, true, false);
    }

    public enum Respawn {
        natural,
        in_current_level,
        in_level_zero;
    }
}