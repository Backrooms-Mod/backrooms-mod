package com.kpabr.backrooms.init;
import static com.kpabr.backrooms.util.RegistryHelper.get;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.OptionalLong;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.world.biome.LevelZeroBiomeSource;
import com.kpabr.backrooms.world.biome.Level1BiomeSource;
import com.kpabr.backrooms.world.biome.CrimsonHallsBiome;
import com.kpabr.backrooms.world.biome.LevelZeroNormalBiome;
import com.kpabr.backrooms.world.biome.CementHallsBiome;
import com.kpabr.backrooms.world.chunk.LevelOneChunkGenerator;
import com.kpabr.backrooms.world.chunk.LevelZeroChunkGenerator;
import net.ludocrypt.limlib.api.LiminalEffects;
import net.ludocrypt.limlib.api.LiminalWorld;
import net.ludocrypt.limlib.api.render.LiminalBaseEffects;
import net.ludocrypt.limlib.api.sound.ReverbSettings;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class BackroomsLevels {

    public static final RegistryKey<Biome> LEVEL_ZERO_NORMAL_BIOME = get("normal_biome", LevelZeroNormalBiome.create());
    public static final RegistryKey<Biome> CRIMSON_WALLS_BIOME = get("crimson_walls", CrimsonHallsBiome.create());
    public static final RegistryKey<Biome> CEMENT_WALLS_BIOME = get("cement_walls", CementHallsBiome.create());

    public static final LiminalEffects LEVEL_ZERO_EFFECTS = new LiminalEffects(Optional.of(new LiminalBaseEffects.SimpleBaseEffects(Optional.empty(), false, "NONE", true, false, true)), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(new ReverbSettings().setDecayTime(2.15F).setDensity(0.725F)));
    //public static final LiminalWorld LEVEL_0 = get("level_0", new LiminalWorld(BackroomsMod.id("level_0"), DimensionType.create(OptionalLong.of(23500), true, false, false, true, 1.0, false, false, true, false, false, 0, 128, 128, TagKey.of(Registry.BLOCK_KEY, BackroomsMod.id("level_zero")), BackroomsMod.id("level_zero"), 0.075F),
    //        (world, dimensionTypeRegistry, biomeRegistry, structureRegistry, chunkGeneratorSettingsRegistry, noiseSettingsRegistry, registryManager, seed) ->
    //        new DimensionOptions(dimensionTypeRegistry.getOrCreateEntry(world.getDimensionTypeKey()), new LevelZeroChunkGenerator(new LevelZeroBiomeSource(biomeRegistry, seed), seed)), LEVEL_ZERO_EFFECTS));

    //public static final LiminalWorld LEVEL_1 = get("level_1", new LiminalWorld(BackroomsMod.id("level_1"), DimensionType.create(OptionalLong.of(23500), true, false, false, true, 1.0, false, false, true, false, false, 0, 128, 128, TagKey.of(Registry.BLOCK_KEY, BackroomsMod.id("level_1")), BackroomsMod.id("level_1"), 0.075F),
    //        (world, dimensionTypeRegistry, biomeRegistry, structureRegistry, chunkGeneratorSettingsRegistry, noiseSettingsRegistry, registryManager, seed) ->
    //                new DimensionOptions(dimensionTypeRegistry.getOrCreateEntry(world.getDimensionTypeKey()), new LevelOneChunkGenerator(new Level1BiomeSource(biomeRegistry, seed), seed)), LEVEL_ZERO_EFFECTS));

    public static final LiminalWorld LEVEL_0 = registerLevel(BackroomsMod.id("level_0"), LevelZeroChunkGenerator.class, LevelZeroBiomeSource.class);
    public static final LiminalWorld LEVEL_1 = registerLevel(BackroomsMod.id("level_1"), LevelOneChunkGenerator.class, Level1BiomeSource.class);


    public static void init() {
        Registry.register(Registry.BIOME_SOURCE, BackroomsMod.id("level_0_biome_source"), LevelZeroBiomeSource.CODEC);
        Registry.register(Registry.BIOME_SOURCE, BackroomsMod.id("level_1_biome_source"), Level1BiomeSource.CODEC);
        get("level_0_chunk_generator", LevelZeroChunkGenerator.CODEC);
        get("level_1_chunk_generator", LevelOneChunkGenerator.CODEC);
    }

    public static LiminalWorld registerLevel(Identifier name, Class chunkGenerator, Class biomeSource) {
        try {
            Constructor chunkGeneratorConstructor = chunkGenerator.getDeclaredConstructor(BiomeSource.class, long.class);
            chunkGeneratorConstructor.setAccessible(true);
            Constructor biomeSourceConstructor = biomeSource.getDeclaredConstructor(Registry.class, long.class);
            biomeSourceConstructor.setAccessible(true);

            LiminalWorld LEVEL = get(name.getPath(), new LiminalWorld(name, DimensionType.create(OptionalLong.of(23500), true, false, false, true, 1.0, false, false, true, false, false, 0, 128, 128, TagKey.of(Registry.BLOCK_KEY, name), name, 0.075F),
                    (world, dimensionTypeRegistry, biomeRegistry, structureRegistry, chunkGeneratorSettingsRegistry, noiseSettingsRegistry, registryManager, seed) ->
                    {
                        try {
                            return new DimensionOptions(dimensionTypeRegistry.getOrCreateEntry(world.getDimensionTypeKey()), (ChunkGenerator) chunkGeneratorConstructor.newInstance(biomeSourceConstructor.newInstance(biomeRegistry, seed), seed));
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
                            return null;
                        }
                    }, LEVEL_ZERO_EFFECTS));
            return LEVEL;
        } catch (NoSuchMethodException e) {

            throw new RuntimeException(e);
        }
    }

    public static LiminalWorld registerLevelWithEffects(Identifier name, Class<ChunkGenerator> chunkGenerator, Class<Object[]> biomeSource, LiminalEffects effects) {
        try {
            // getting constructors of chunkGenerator and biomeSource
            // related questions if you wanna know how it's working
            // https://www.tutorialspoint.com/java/lang/class_getdeclaredconstructor.htm
            // https://stackoverflow.com/questions/8249050/java-instantiate-class-at-runtime-with-parameters


            Constructor<ChunkGenerator> chunkGeneratorConstructor = chunkGenerator.getDeclaredConstructor(BiomeSource.class, long.class);
            chunkGeneratorConstructor.setAccessible(true);
            Constructor<Object[]> biomeSourceConstructor = biomeSource.getDeclaredConstructor(Registry.class, long.class);
            biomeSourceConstructor.setAccessible(true);

            LiminalWorld LEVEL = get(name.getPath(), new LiminalWorld(name, DimensionType.create(OptionalLong.of(23500), true, false, false, true, 1.0, false, false, true, false, false, 0, 128, 128, TagKey.of(Registry.BLOCK_KEY, name), name, 0.075F),
                    (world, dimensionTypeRegistry, biomeRegistry, structureRegistry, chunkGeneratorSettingsRegistry, noiseSettingsRegistry, registryManager, seed) ->
                    {
                        try {
                            return new DimensionOptions(
                                    dimensionTypeRegistry.getOrCreateEntry(world.getDimensionTypeKey()),
                                    chunkGeneratorConstructor.newInstance(biomeSourceConstructor.newInstance(biomeRegistry, seed), seed)
                            );
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
                            return null;
                        }
                    }, effects));
            return LEVEL;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

}
