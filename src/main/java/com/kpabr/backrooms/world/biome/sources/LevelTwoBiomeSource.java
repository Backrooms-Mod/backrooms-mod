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

public class LevelTwoBiomeSource extends BiomeSource{

    public static final Codec<LevelTwoBiomeSource> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    RegistryOps.createRegistryCodec(Registry.BIOME_KEY)
                            .forGetter((biomeSource) -> biomeSource.BIOME_REGISTRY)
            ).apply(instance, instance.stable(LevelTwoBiomeSource::new)));
    
    private Registry<Biome> BIOME_REGISTRY;
    private final RegistryEntry<Biome> PIPES_BIOME;

    public LevelTwoBiomeSource(Registry<Biome> biomeRegistry) {
        super(Stream.of(
                biomeRegistry.getOrCreateEntry(BackroomsLevels.PIPES_BIOME)));
        PIPES_BIOME = biomeRegistry.getOrCreateEntry(BackroomsLevels.PIPES_BIOME);
        this.BIOME_REGISTRY = biomeRegistry;
    }

    @Override
    public RegistryEntry<Biome> getBiome(int x, int y, int z, MultiNoiseSampler noise) {
        return PIPES_BIOME;
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
