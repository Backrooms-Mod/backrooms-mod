package com.kpabr.backrooms.init;
import static com.kpabr.backrooms.util.RegistryHelper.get;
import static com.kpabr.backrooms.util.RegistryHelper.getMaze;


import java.util.Optional;
import java.util.OptionalLong;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.init.BackroomsSoundEvents;
import com.kpabr.backrooms.world.chunk.CommunalCorridorsChunkGenerator;
import com.kpabr.backrooms.client.render.sky.StrongLiminalShader;
import com.kpabr.backrooms.config.BackroomsConfig;
import com.kpabr.backrooms.world.biome.CommunalCorridorsBiome;
import com.kpabr.backrooms.world.chunk.CommunalCorridorsChunkGenerator;
import net.ludocrypt.limlib.api.LiminalEffects;
import net.ludocrypt.limlib.api.LiminalWorld;
import net.ludocrypt.limlib.api.render.LiminalBaseEffects;
import net.ludocrypt.limlib.api.render.LiminalShaderApplier;
import net.ludocrypt.limlib.api.render.LiminalSkyRenderer;
import net.ludocrypt.limlib.api.sound.ReverbSettings;
import net.minecraft.sound.MusicSound;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;


import net.minecraft.sound.MusicSound;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;

import java.util.Optional;
import java.util.OptionalLong;

public class BackroomsLevels {

    public static final RegistryKey<Biome> COMMUNAL_CORRIDORS_BIOME = get("communal_corridors", CommunalCorridorsBiome.create());

    //of(new StrongLiminalShader(BackroomsMod.id("communal_corridors")))

    public static final LiminalEffects COMMUNAL_CORRIDORS_EFFECTS = new LiminalEffects(Optional.of(new LiminalBaseEffects.SimpleBaseEffects(Optional.empty(), false, "NONE", true, false, false)), Optional.empty(), Optional.of(new LiminalSkyRenderer.SkyboxSky(BackroomsMod.id("textures/sky/snow"))), Optional.of(new MusicSound(BackroomsSoundEvents.MUSIC_COMMUNAL_CORRIDORS, 3000, 8000, true)), Optional.of(new ReverbSettings().setDecayTime(2.15F).setDensity(0.725F)));
    public static final LiminalWorld COMMUNAL_CORRIDORS = get("communal_corridors", new LiminalWorld(BackroomsMod.id("communal_corridors"), DimensionType.create(OptionalLong.of(23500), true, false, false, true, 1.0, false, false, true, false, false, 0, 128, 128, TagKey.of(Registry.BLOCK_KEY, BackroomsMod.id("communal_corridors")), BackroomsMod.id("communal_corridors"), 0.075F), (world, dimensionTypeRegistry, biomeRegistry, structureRegistry, chunkGeneratorSettingsRegistry, noiseSettingsRegistry, registryManager, seed) -> new DimensionOptions(dimensionTypeRegistry.getOrCreateEntry(world.getDimensionTypeKey()), new CommunalCorridorsChunkGenerator(new FixedBiomeSource(biomeRegistry.getOrCreateEntry(BackroomsLevels.COMMUNAL_CORRIDORS_BIOME)), seed)), COMMUNAL_CORRIDORS_EFFECTS));

    public static void init() {
        get("communal_corridors_chunk_generator", CommunalCorridorsChunkGenerator.CODEC);


    }

}
