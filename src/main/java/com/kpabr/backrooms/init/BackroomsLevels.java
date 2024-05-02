package com.kpabr.backrooms.init;

import com.kpabr.backrooms.LevelOneChunkGenerator;
import com.kpabr.backrooms.LevelThreeChunkGenerator;
import com.kpabr.backrooms.LevelTwoChunkGenerator;
import com.kpabr.backrooms.LevelZeroChunkGenerator;
import com.kpabr.backrooms.world.biome.*;
import com.kpabr.backrooms.world.biome.biomes.level0.CrimsonHallsBiome;
import com.kpabr.backrooms.world.biome.biomes.level0.DecrepitBiome;
import com.kpabr.backrooms.world.biome.biomes.level0.MegalophobiaBiome;
import com.kpabr.backrooms.world.biome.biomes.level0.YellowHallsBiome;
import com.kpabr.backrooms.world.biome.biomes.level1.CementHallsBiome;
import com.kpabr.backrooms.world.biome.biomes.level1.ParkingGarageBiome;
import com.kpabr.backrooms.world.biome.biomes.level1.WarehouseBiome;
import com.kpabr.backrooms.world.biome.biomes.level2.PipesBiome;
import com.kpabr.backrooms.world.biome.biomes.level3.ElectricalStationBiome;
import com.kpabr.backrooms.world.biome.sources.LevelOneBiomeSource;
import com.kpabr.backrooms.world.biome.sources.LevelThreeBiomeSource;
import com.kpabr.backrooms.world.biome.sources.LevelTwoBiomeSource;
import com.kpabr.backrooms.world.biome.sources.LevelZeroBiomeSource;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import static com.kpabr.backrooms.util.RegistryHelper.get;
import static net.minecraft.server.command.CommandManager.literal;

public class BackroomsLevels {

    // Level 0 biomes
    public static final RegistryKey<Biome> DECREPIT_BIOME = get("decrepit", DecrepitBiome.create());
    public static final RegistryKey<Biome> MEGALOPHOBIA_BIOME = get("megalophobia", MegalophobiaBiome.create());
    public static final RegistryKey<Biome> YELLOW_WALLS_BIOME = get("yellow_walls", YellowHallsBiome.create());
    public static final RegistryKey<Biome> CRIMSON_WALLS_BIOME = get("crimson_walls", CrimsonHallsBiome.create());
    
    // Level 1 biomes
    public static final RegistryKey<Biome> CEMENT_WALLS_BIOME = get("cement_walls", CementHallsBiome.create());
    public static final RegistryKey<Biome> PARKING_GARAGE_BIOME = get("parking_garage", ParkingGarageBiome.create());
    public static final RegistryKey<Biome> WAREHOUSE_BIOME = get("warehouse", WarehouseBiome.create());
    
    // Level 2 biomes
    public static final RegistryKey<Biome> PIPES_BIOME = get("pipes", PipesBiome.create());
    
    // Level 4 biomes
    public static final RegistryKey<Biome> ELECTRICAL_STATION_BIOME = get("electrical_station", ElectricalStationBiome.create());


    // Levels/Dimensions
    // The dimension options refer to the JSON-file in the dimension subfolder of the datapack,
	// which will always share it's ID with the world that is created from it
	public static final RegistryKey<DimensionOptions> LEVEL_0_DIMENSION_KEY = RegistryKey.of(
        Registry.DIMENSION_KEY,
        new Identifier("backrooms", "level_0")
    );
    public static RegistryKey<World> LEVEL_0_WORLD_KEY = RegistryKey.of(
        Registry.WORLD_KEY,
        LEVEL_0_DIMENSION_KEY.getValue()
    );

    public static final RegistryKey<DimensionOptions> LEVEL_1_DIMENSION_KEY = RegistryKey.of(
        Registry.DIMENSION_KEY,
        new Identifier("backrooms", "level_1")
    );
    public static RegistryKey<World> LEVEL_1_WORLD_KEY = RegistryKey.of(
        Registry.WORLD_KEY,
        LEVEL_1_DIMENSION_KEY.getValue()
    );

    public static final RegistryKey<DimensionOptions> LEVEL_2_DIMENSION_KEY = RegistryKey.of(
        Registry.DIMENSION_KEY,
        new Identifier("backrooms", "level_2")
    );
    public static RegistryKey<World> LEVEL_2_WORLD_KEY = RegistryKey.of(
        Registry.WORLD_KEY,
        LEVEL_2_DIMENSION_KEY.getValue()
    );

    public static final RegistryKey<DimensionOptions> LEVEL_3_DIMENSION_KEY = RegistryKey.of(
        Registry.DIMENSION_KEY,
        new Identifier("backrooms", "level_3")
    );
    public static RegistryKey<World> LEVEL_3_WORLD_KEY = RegistryKey.of(
        Registry.WORLD_KEY,
        LEVEL_3_DIMENSION_KEY.getValue()
    );

    // don't forget to change this variable or portal block won't work
    public static final int LEVELS_AMOUNT = 4;

    // ServerWorlds for getting seed and NBT resources
	public static ServerWorld LEVEL_0_WORLD;
	public static ServerWorld LEVEL_1_WORLD;
	public static ServerWorld LEVEL_2_WORLD;
	public static ServerWorld LEVEL_3_WORLD;

    public static void init() {
        addLevel("backrooms", "level_0", "level_0_biome_source",  LevelZeroChunkGenerator.CODEC, LevelZeroBiomeSource.CODEC);
        addLevel("backrooms", "level_1", "level_1_biome_source",  LevelOneChunkGenerator.CODEC, LevelOneBiomeSource.CODEC);
        addLevel("backrooms", "level_2", "level_2_biome_source",  LevelTwoChunkGenerator.CODEC, LevelTwoBiomeSource.CODEC);
        addLevel("backrooms", "level_3", "level_3_biome_source",  LevelThreeChunkGenerator.CODEC, LevelThreeBiomeSource.CODEC);


        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			LEVEL_0_WORLD = server.getWorld(LEVEL_0_WORLD_KEY);
			LEVEL_1_WORLD = server.getWorld(LEVEL_1_WORLD_KEY);
			LEVEL_2_WORLD = server.getWorld(LEVEL_2_WORLD_KEY);
			LEVEL_3_WORLD = server.getWorld(LEVEL_3_WORLD_KEY);
        });

        // only for debug, remove // TODO
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) ->
				dispatcher.register(literal("tp0").executes(context -> { 
                    return debugTeleport(context, LEVEL_0_WORLD_KEY);
                })));
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) ->
				dispatcher.register(literal("tp1").executes(context -> { 
                    return debugTeleport(context, LEVEL_1_WORLD_KEY);
                })));
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) ->
				dispatcher.register(literal("tp2").executes(context -> { 
                    return debugTeleport(context, LEVEL_2_WORLD_KEY);
                })));
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) ->
				dispatcher.register(literal("tp3").executes(context -> { 
                    return debugTeleport(context, LEVEL_3_WORLD_KEY);
                })));
    }

    public static RegistryKey<World> addLevel(String namespace, String levelName, String biomeSourceName, Codec<? extends ChunkGenerator> chunkGenerator, Codec<? extends BiomeSource> biomeSource) {
        Registry.register(Registry.BIOME_SOURCE, new Identifier(namespace, biomeSourceName), biomeSource);
		Registry.register(Registry.CHUNK_GENERATOR, new Identifier(namespace, levelName), chunkGenerator);
        return RegistryKey.of(Registry.WORLD_KEY, new Identifier(namespace, levelName));
    }

    // only for debug, remove
    private static int debugTeleport(CommandContext<ServerCommandSource> context, RegistryKey<World> level) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
		ServerWorld serverWorld = player.getWorld();
		ServerWorld targetWorld = context.getSource().getServer().getWorld(level);

		if (serverWorld != targetWorld) {
			TeleportTarget target = new TeleportTarget(new Vec3d(0.5, 101, 0.5), Vec3d.ZERO, 0, 0);
			FabricDimensions.teleport(player, targetWorld, target);

			if (player.world != targetWorld) {
				throw new CommandException(new LiteralText("Teleportation failed!"));
			}

			targetWorld.setBlockState(new BlockPos(0, 100, 0), Blocks.DIAMOND_BLOCK.getDefaultState());
			targetWorld.setBlockState(new BlockPos(0, 101, 0), Blocks.TORCH.getDefaultState());
		} else {
			TeleportTarget target = new TeleportTarget(new Vec3d(0, 100, 0), Vec3d.ZERO,
					(float) Math.random() * 360 - 180, (float) Math.random() * 360 - 180);
			FabricDimensions.teleport(player, context.getSource().getServer().getWorld(World.OVERWORLD), target);
		}
		return 1;
    }
}
