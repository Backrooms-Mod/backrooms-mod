package com.kpabr.backrooms.init;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.world.biome.sources.LevelOneBiomeSource;
import com.kpabr.backrooms.world.biome.sources.LevelThreeBiomeSource;
import com.kpabr.backrooms.world.biome.sources.LevelTwoBiomeSource;
import com.kpabr.backrooms.world.biome.sources.LevelZeroBiomeSource;
import com.kpabr.backrooms.world.chunk.LevelOneChunkGenerator;
import com.kpabr.backrooms.world.chunk.LevelThreeChunkGenerator;
import com.kpabr.backrooms.world.chunk.LevelTwoChunkGenerator;
import com.kpabr.backrooms.world.chunk.LevelZeroChunkGenerator;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.CommandException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import static com.kpabr.backrooms.util.RegistryHelper.getBiome;
import static net.minecraft.server.command.CommandManager.literal;

import java.util.HashMap;

public class BackroomsLevels {

    // Levels Biomes
    // The biome options refer to the JSON-file in the worldgen/biome subfolder of
    // the datapack,
    // which will always share it's ID with the biome that is created from it

    // Level 0 biomes
    public static final RegistryKey<Biome> DECREPIT_BIOME = getBiome("decrepit");
    public static final RegistryKey<Biome> MEGALOPHOBIA_BIOME = getBiome("megalophobia");
    public static final RegistryKey<Biome> YELLOW_WALLS_BIOME = getBiome("yellow_walls");
    public static final RegistryKey<Biome> CRIMSON_WALLS_BIOME = getBiome("crimson_walls");

    // Level 1 biomes
    public static final RegistryKey<Biome> CEMENT_WALLS_BIOME = getBiome("cement_walls");
    public static final RegistryKey<Biome> PARKING_GARAGE_BIOME = getBiome("parking_garage");
    public static final RegistryKey<Biome> WAREHOUSE_BIOME = getBiome("warehouse");

    // Level 2 biomes
    public static final RegistryKey<Biome> PIPES_BIOME = getBiome("pipes");

    // Level 4 biomes
    public static final RegistryKey<Biome> ELECTRICAL_STATION_BIOME = getBiome("electrical_station");

    public static final HashMap<RegistryKey<Biome>, RegistryEntry<Biome>> BIOME_KEY_TO_ENTRY = new HashMap<RegistryKey<Biome>, RegistryEntry<Biome>>();

    // Levels/Dimensions
    // The dimension options refer to the JSON-file in the dimension subfolder of
    // the datapack,
    // which will always share it's ID with the world that is created from it
    public static final RegistryKey<DimensionOptions> LEVEL_0_DIMENSION_KEY = RegistryKey.of(
            RegistryKeys.DIMENSION,
            new Identifier(BackroomsMod.ModId, "level_0"));
    public static RegistryKey<World> LEVEL_0_WORLD_KEY = RegistryKey.of(
            RegistryKeys.WORLD,
            LEVEL_0_DIMENSION_KEY.getValue());

    public static final RegistryKey<DimensionOptions> LEVEL_1_DIMENSION_KEY = RegistryKey.of(
            RegistryKeys.DIMENSION,
            new Identifier(BackroomsMod.ModId, "level_1"));
    public static RegistryKey<World> LEVEL_1_WORLD_KEY = RegistryKey.of(
            RegistryKeys.WORLD,
            LEVEL_1_DIMENSION_KEY.getValue());

    public static final RegistryKey<DimensionOptions> LEVEL_2_DIMENSION_KEY = RegistryKey.of(
            RegistryKeys.DIMENSION,
            new Identifier(BackroomsMod.ModId, "level_2"));
    public static RegistryKey<World> LEVEL_2_WORLD_KEY = RegistryKey.of(
            RegistryKeys.WORLD,
            LEVEL_2_DIMENSION_KEY.getValue());

    public static final RegistryKey<DimensionOptions> LEVEL_3_DIMENSION_KEY = RegistryKey.of(
            RegistryKeys.DIMENSION,
            new Identifier(BackroomsMod.ModId, "level_3"));
    public static RegistryKey<World> LEVEL_3_WORLD_KEY = RegistryKey.of(
            RegistryKeys.WORLD,
            LEVEL_3_DIMENSION_KEY.getValue());

    // don't forget to change this variable or portal block won't work
    public static final int LEVELS_AMOUNT = 4;

    // ServerWorlds for getting seed and NBT resources
    public static ServerWorld LEVEL_0_WORLD;
    public static ServerWorld LEVEL_1_WORLD;
    public static ServerWorld LEVEL_2_WORLD;
    public static ServerWorld LEVEL_3_WORLD;

    public static void init() {
        addLevel(BackroomsMod.ModId, "level_0", "level_0_biome_source", LevelZeroChunkGenerator.CODEC,
                LevelZeroBiomeSource.CODEC);
        addLevel(BackroomsMod.ModId, "level_1", "level_1_biome_source", LevelOneChunkGenerator.CODEC,
                LevelOneBiomeSource.CODEC);
        addLevel(BackroomsMod.ModId, "level_2", "level_2_biome_source", LevelTwoChunkGenerator.CODEC,
                LevelTwoBiomeSource.CODEC);
        addLevel(BackroomsMod.ModId, "level_3", "level_3_biome_source", LevelThreeChunkGenerator.CODEC,
                LevelThreeBiomeSource.CODEC);

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            LEVEL_0_WORLD = server.getWorld(LEVEL_0_WORLD_KEY);
            LEVEL_1_WORLD = server.getWorld(LEVEL_1_WORLD_KEY);
            LEVEL_2_WORLD = server.getWorld(LEVEL_2_WORLD_KEY);
            LEVEL_3_WORLD = server.getWorld(LEVEL_3_WORLD_KEY);
        });

        // only for debug
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher
                    .register(literal("tp0").executes(context -> {
                        return debugTeleport(context, LEVEL_0_WORLD_KEY);
                    })));
            CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher
                    .register(literal("tp1").executes(context -> {
                        return debugTeleport(context, LEVEL_1_WORLD_KEY);
                    })));
            CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher
                    .register(literal("tp2").executes(context -> {
                        return debugTeleport(context, LEVEL_2_WORLD_KEY);
                    })));
            CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher
                    .register(literal("tp3").executes(context -> {
                        return debugTeleport(context, LEVEL_3_WORLD_KEY);
                    })));
        }
    }

    public static RegistryKey<World> addLevel(String namespace, String levelName, String biomeSourceName,
            Codec<? extends ChunkGenerator> chunkGenerator, Codec<? extends BiomeSource> biomeSource) {
        Registry.register(Registries.BIOME_SOURCE, new Identifier(namespace, biomeSourceName), biomeSource);
        Registry.register(Registries.CHUNK_GENERATOR, new Identifier(namespace, levelName), chunkGenerator);
        return RegistryKey.of(RegistryKeys.WORLD, new Identifier(namespace, levelName));
    }

    // only for debug
    private static int debugTeleport(CommandContext<ServerCommandSource> context, RegistryKey<World> level)
            throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        ServerWorld serverWorld = player.getServerWorld();
        ServerWorld targetWorld = context.getSource().getServer().getWorld(level);

        if (serverWorld != targetWorld) {
            TeleportTarget target = new TeleportTarget(new Vec3d(0.5, 101, 0.5), Vec3d.ZERO, 0, 0);
            FabricDimensions.teleport(player, targetWorld, target);

            if (player.getWorld() != targetWorld) {
                throw new CommandException(Text.literal("Teleportation failed!"));
            }
        } else {
            TeleportTarget target = new TeleportTarget(new Vec3d(0, 100, 0), Vec3d.ZERO,
                    (float) Math.random() * 360 - 180, (float) Math.random() * 360 - 180);
            FabricDimensions.teleport(player, context.getSource().getServer().getWorld(World.OVERWORLD), target);
        }
        return 1;
    }
}
