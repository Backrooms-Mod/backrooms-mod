package com.kpabr.backrooms.init;
import static com.kpabr.backrooms.util.RegistryHelper.get;


import java.util.Optional;
import java.util.OptionalLong;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.world.chunk.TestLevelChunkGenerator;
import com.kpabr.backrooms.world.biome.TestLevelBiome;
import net.ludocrypt.limlib.api.LiminalEffects;
import net.ludocrypt.limlib.api.LiminalWorld;
import net.ludocrypt.limlib.api.render.LiminalBaseEffects;
import net.ludocrypt.limlib.api.sound.ReverbSettings;
import net.minecraft.sound.MusicSound;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;

public class BackroomsLevels {

    public static final RegistryKey<Biome> TEST_LEVEL_BIOME = get("test_level", TestLevelBiome.create());

    //of(new StrongLiminalShader(BackroomsMod.id("communal_corridors")))

    //.of(new LiminalSkyRenderer.SkyboxSky(BackroomsMod.id("textures/sky/snow")))

    //.of(new MusicSound(BackroomsSoundEvents.MUSIC_COMMUNAL_CORRIDORS, 3000, 8000, true))

    public static final LiminalEffects TEST_LEVEL_EFFECTS = new LiminalEffects(Optional.of(new LiminalBaseEffects.SimpleBaseEffects(Optional.empty(), false, "NONE", true, false, true)), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(new ReverbSettings().setDecayTime(2.15F).setDensity(0.725F)));
    public static final LiminalWorld TEST_LEVEL = get("test_level", new LiminalWorld(BackroomsMod.id("test_level"), DimensionType.create(OptionalLong.of(23500), true, false, false, true, 1.0, false, false, true, false, false, 0, 128, 128, TagKey.of(Registry.BLOCK_KEY, BackroomsMod.id("test_level")), BackroomsMod.id("test_level"), 0.075F), (world, dimensionTypeRegistry, biomeRegistry, structureRegistry, chunkGeneratorSettingsRegistry, noiseSettingsRegistry, registryManager, seed) -> new DimensionOptions(dimensionTypeRegistry.getOrCreateEntry(world.getDimensionTypeKey()), new TestLevelChunkGenerator(new FixedBiomeSource(biomeRegistry.getOrCreateEntry(BackroomsLevels.TEST_LEVEL_BIOME)), seed)), TEST_LEVEL_EFFECTS));

    public static void init() {
        get("test_level_chunk_generator", TestLevelChunkGenerator.CODEC);


    }

}
