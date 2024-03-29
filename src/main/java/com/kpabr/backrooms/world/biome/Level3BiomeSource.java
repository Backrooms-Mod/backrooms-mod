package com.kpabr.backrooms.world.biome;

import com.kpabr.backrooms.init.BackroomsLevels;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

import java.util.stream.Stream;

public class Level3BiomeSource extends BiomeSource {
    public static final Codec<Level3BiomeSource> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    RegistryOps.createRegistryCodec(Registry.BIOME_KEY)
                            .forGetter((biomeSource) -> biomeSource.BIOME_REGISTRY),
                    Codec.LONG.fieldOf("seed")
                            .stable()
                            .forGetter((biomeSource) -> biomeSource.seed)
            ).apply(instance, instance.stable(Level3BiomeSource::new)));


    private final long seed;
    protected Registry<Biome> BIOME_REGISTRY;
    private final RegistryEntry<Biome> ELECTRICAL_STATION_BIOME;

    public Level3BiomeSource(Registry<Biome> registry, long seed) {
        super(Stream.of(
                registry.getOrCreateEntry(BackroomsLevels.ELECTRICAL_STATION_BIOME)));
        ELECTRICAL_STATION_BIOME = registry.getOrCreateEntry(BackroomsLevels.ELECTRICAL_STATION_BIOME);
        this.seed = seed;
    }

    @Override
    public RegistryEntry<Biome> getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise) {
        return ELECTRICAL_STATION_BIOME;
    }

    public BiomeSource withSeed(long seed) {
        return new Level3BiomeSource(BIOME_REGISTRY, seed);
    }

    @Override
    protected Codec<Level3BiomeSource> getCodec() {
        return CODEC;
    }
}
