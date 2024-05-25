package com.kpabr.backrooms.world.biome.sources;

import com.kpabr.backrooms.init.BackroomsLevels;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil.MultiNoiseSampler;

import java.util.stream.Stream;

public class LevelThreeBiomeSource extends BiomeSource{

    public static final Codec<LevelThreeBiomeSource> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    RegistryOps.createRegistryCodec(Registry.BIOME_KEY)
                            .forGetter((biomeSource) -> biomeSource.BIOME_REGISTRY)
            ).apply(instance, instance.stable(LevelThreeBiomeSource::new)));
    
    private Registry<Biome> BIOME_REGISTRY;
    private final RegistryEntry<Biome> ELECTRICAL_STATION_BIOME;

    public LevelThreeBiomeSource(Registry<Biome> biomeRegistry) {
        super(Stream.of(
                biomeRegistry.getOrCreateEntry(BackroomsLevels.PIPES_BIOME)));
        ELECTRICAL_STATION_BIOME = biomeRegistry.getOrCreateEntry(BackroomsLevels.PIPES_BIOME);
        this.BIOME_REGISTRY = biomeRegistry;
    }

    @Override
    public RegistryEntry<Biome> getBiome(int x, int y, int z, MultiNoiseSampler noise) {
        return ELECTRICAL_STATION_BIOME;
    }

    @Override
    protected Codec<? extends BiomeSource> getCodec() {
        return CODEC;
    }

    @Override
    public BiomeSource withSeed(long seed) {
        return this;
    }
    
}
