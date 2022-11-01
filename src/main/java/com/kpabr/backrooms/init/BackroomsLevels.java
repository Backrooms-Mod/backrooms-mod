package com.kpabr.backrooms.init;
import static com.kpabr.backrooms.util.RegistryHelper.get;


import java.util.Optional;
import java.util.OptionalLong;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.world.biome.LevelZeroBiomeSource;
import com.kpabr.backrooms.world.biome.CrimsonHallsBiome;
import com.kpabr.backrooms.world.chunk.LevelZeroChunkGenerator;
import com.kpabr.backrooms.world.biome.LevelZeroNormalBiome;
import net.ludocrypt.limlib.api.LiminalEffects;
import net.ludocrypt.limlib.api.LiminalWorld;
import net.ludocrypt.limlib.api.render.LiminalBaseEffects;
import net.ludocrypt.limlib.api.sound.ReverbSettings;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;

public class BackroomsLevels {

    public static final RegistryKey<Biome> LEVEL_ZERO_NORMAL_BIOME = get("normal_biome", LevelZeroNormalBiome.create());
    public static final RegistryKey<Biome> CRIMSON_WALLS_BIOME = get("crimson_walls", CrimsonHallsBiome.create());

    public static final LiminalEffects LEVEL_ZERO_EFFECTS = new LiminalEffects(Optional.of(new LiminalBaseEffects.SimpleBaseEffects(Optional.empty(), false, "NONE", true, false, true)), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(new ReverbSettings().setDecayTime(2.15F).setDensity(0.725F)));
    public static final LiminalWorld TEST_LEVEL = get("level_zero", new LiminalWorld(BackroomsMod.id("level_zero"), DimensionType.create(OptionalLong.of(23500), true, false, false, true, 1.0, false, false, true, false, false, 0, 128, 128, TagKey.of(Registry.BLOCK_KEY, BackroomsMod.id("level_zero")), BackroomsMod.id("level_zero"), 0.075F),
            (world, dimensionTypeRegistry, biomeRegistry, structureRegistry, chunkGeneratorSettingsRegistry, noiseSettingsRegistry, registryManager, seed) ->
            new DimensionOptions(dimensionTypeRegistry.getOrCreateEntry(world.getDimensionTypeKey()), new LevelZeroChunkGenerator(new LevelZeroBiomeSource(biomeRegistry, seed), seed)), LEVEL_ZERO_EFFECTS));


    public static void init() {
        Registry.register(Registry.BIOME_SOURCE, BackroomsMod.id("level_zero_biome_source"), LevelZeroBiomeSource.CODEC);
        get("level_zero_chunk_generator", LevelZeroChunkGenerator.CODEC);
    }

}
