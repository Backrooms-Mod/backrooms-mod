package com.kpabr.backrooms.world.biome.sources;

import com.kpabr.backrooms.init.BackroomsLevels;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil.MultiNoiseSampler;

import java.util.stream.Stream;

public class LevelTwoBiomeSource extends BiomeSource {

    public static final Codec<LevelTwoBiomeSource> CODEC = RecordCodecBuilder
            .create((instance) -> instance.group(RegistryOps.getEntryLookupCodec(RegistryKeys.BIOME))
                    .apply(instance, instance.stable(LevelTwoBiomeSource::new)));
    private final RegistryEntry<Biome> PIPES_BIOME;

    public LevelTwoBiomeSource(RegistryEntryLookup<Biome> biomeRegistry) {
        super();
        PIPES_BIOME = biomeRegistry.getOrThrow(BackroomsLevels.PIPES_BIOME);
    }

    @Override
    public RegistryEntry<Biome> getBiome(int x, int y, int z, MultiNoiseSampler noise) {
        return PIPES_BIOME;
    }

    @Override
    protected Codec<LevelTwoBiomeSource> getCodec() {
        return CODEC;
    }

    @Override
    protected Stream<RegistryEntry<Biome>> biomeStream() {
        return Stream.of(PIPES_BIOME);
    }
}
