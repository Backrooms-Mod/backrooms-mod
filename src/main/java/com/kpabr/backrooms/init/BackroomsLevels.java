package com.kpabr.backrooms.init;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.world.biome.*;
import com.kpabr.backrooms.world.chunk.LevelOneChunkGenerator;
import com.kpabr.backrooms.world.chunk.LevelThreeChunkGenerator;
import com.kpabr.backrooms.world.chunk.LevelTwoChunkGenerator;
import com.kpabr.backrooms.world.chunk.LevelZeroChunkGenerator;
import net.ludocrypt.limlib.api.LiminalEffects;
import net.ludocrypt.limlib.api.LiminalWorld;
import net.ludocrypt.limlib.api.render.LiminalBaseEffects;
import net.ludocrypt.limlib.api.sound.ReverbSettings;
import net.ludocrypt.limlib.api.world.AbstractNbtChunkGenerator;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.OptionalLong;

import static com.kpabr.backrooms.util.RegistryHelper.get;

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


    public static LiminalEffects DEFAULT_LEVEL_EFFECTS = new LiminalEffects(Optional.of(new LiminalBaseEffects.SimpleBaseEffects(Optional.empty(), false, "NONE", true, false, true)), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(new ReverbSettings().setDecayTime(2.15F).setDensity(0.725F)));

    // don't forget to change this variable or portal block won't work
    public static final int LEVELS_AMOUNT = 4;

    public static final LiminalWorld LEVEL_0 = addLevel("level_0", LevelZeroChunkGenerator.class, Level0BiomeSource.class);
    public static final LiminalWorld LEVEL_1 = addLevel("level_1", LevelOneChunkGenerator.class, Level1BiomeSource.class);
    public static final LiminalWorld LEVEL_2 = addLevel("level_2", LevelTwoChunkGenerator.class, Level2BiomeSource.class);
    public static final LiminalWorld LEVEL_3 = addLevel("level_3", LevelThreeChunkGenerator.class, Level3BiomeSource.class);




    public static void init() {
        Registry.register(Registry.BIOME_SOURCE, "level_0_biome_source", Level0BiomeSource.CODEC);
        Registry.register(Registry.BIOME_SOURCE, "level_1_biome_source", Level1BiomeSource.CODEC);
        Registry.register(Registry.BIOME_SOURCE, "level_2_biome_source", Level2BiomeSource.CODEC);
        Registry.register(Registry.BIOME_SOURCE, "level_3_biome_source", Level3BiomeSource.CODEC);
        get("level_0_chunk_generator", LevelZeroChunkGenerator.CODEC);
        get("level_1_chunk_generator", LevelOneChunkGenerator.CODEC);
        get("level_2_chunk_generator", LevelTwoChunkGenerator.CODEC);
        get("level_3_chunk_generator", LevelThreeChunkGenerator.CODEC);
    }

    public static<T extends AbstractNbtChunkGenerator, S extends BiomeSource> LiminalWorld addLevel(String name, Class<T> chunkGenerator, Class<S> biomeSource) {
        return addLevelWithEffects(name, chunkGenerator, biomeSource, DEFAULT_LEVEL_EFFECTS);
    }

    public static<T extends AbstractNbtChunkGenerator, S extends BiomeSource> LiminalWorld addLevelWithEffects(String name, Class<T> chunkGenerator, Class<S> biomeSource, LiminalEffects effects) {
        final Identifier levelId = BackroomsMod.id(name);

        // Messy wrapper
        return get(levelId, new LiminalWorld(levelId, DimensionType.create(OptionalLong.of(23500), true, false, false, true, 1.0, false, false, true, false, false, 0, 128, 128, TagKey.of(Registry.BLOCK_KEY, levelId), levelId, /*0.075F*/0.000F),
                (world, dimensionTypeRegistry, biomeRegistry, structureRegistry, chunkGeneratorSettingsRegistry, noiseSettingsRegistry, registryManager, seed) ->
                        new DimensionOptions(
                                dimensionTypeRegistry.getOrCreateEntry(world.getDimensionTypeKey()),
                                createChunkGenerator(chunkGenerator, biomeSource, biomeRegistry, seed)
                        ),
                effects));
    }

    // Creating chunk generator for registerLevel in runtime.
    private static<T extends AbstractNbtChunkGenerator, S extends BiomeSource> T createChunkGenerator(Class<T> chunkGeneratorClass, Class<S> biomeSourceClass, Registry<Biome> registry, long seed) {
        try {
            Constructor<T> chunkGeneratorConstructor = chunkGeneratorClass.getConstructor(BiomeSource.class, long.class);
            Constructor<S> biomeSourceConstructor = biomeSourceClass.getConstructor(Registry.class, long.class);
            return chunkGeneratorConstructor.newInstance(biomeSourceConstructor.newInstance(registry, seed), seed);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
