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

public class LevelThreeBiomeSource extends BiomeSource {

    public static final Codec<LevelThreeBiomeSource> CODEC = RecordCodecBuilder
            .create((instance) -> instance.group(RegistryOps.getEntryLookupCodec(RegistryKeys.BIOME))
                    .apply(instance, instance.stable(LevelThreeBiomeSource::new)));
    private final RegistryEntry<Biome> ELECTRICAL_STATION_BIOME;

    public LevelThreeBiomeSource(RegistryEntryLookup<Biome> biomeRegistry) {
        super();
        ELECTRICAL_STATION_BIOME = biomeRegistry.getOrThrow(BackroomsLevels.ELECTRICAL_STATION_BIOME);
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
    protected Stream<RegistryEntry<Biome>> biomeStream() {
        return Stream.of(ELECTRICAL_STATION_BIOME);
    }
}
