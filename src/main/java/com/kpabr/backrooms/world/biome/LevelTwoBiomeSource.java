package com.kpabr.backrooms.world.biome;

import com.kpabr.backrooms.init.BackroomsLevels;
import com.kpabr.backrooms.util.BiomeListBuilder;
import com.kpabr.backrooms.util.BiomeRegistryList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;

public class LevelTwoBiomeSource extends BiomeSource {
    public static final Codec<LevelTwoBiomeSource> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    RegistryOps.createRegistryCodec(Registry.BIOME_KEY)
                            .forGetter((biomeSource) -> biomeSource.BIOME_REGISTRY),
                    Codec.LONG.fieldOf("seed")
                            .stable()
                            .forGetter((biomeSource) -> biomeSource.seed)
            ).apply(instance, instance.stable(LevelTwoBiomeSource::new)));

    private final SimplexNoiseSampler noise;
    protected final long seed;

    protected final BiomeRegistryList biomeList;
    protected Registry<Biome> BIOME_REGISTRY;

    public Level0BiomeSource(Registry<Biome> biomeRegistry, long seed) {
        super(biomeRegistry, seed, new BiomeListBuilder() // Custom needed
        );
    }

    @Override
    public BiomeSource withSeed(long seed) {
        return new Level0BiomeSource(BIOME_REGISTRY, seed);
    }


    public LevelTwoBiomeSource(Registry<Biome> biomeRegistry, long seed, BiomeListBuilder biomeList) {
        this(seed, BiomeRegistryList.from(biomeRegistry, biomeList));
        this.BIOME_REGISTRY = biomeRegistry;
    }

    protected LevelTwoBiomeSource(long seed, BiomeRegistryList biomeList) {
        super(biomeList.getBiomeEntries());

        this.seed = seed;
        this.biomeList = biomeList;

        final ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(seed));
        this.noise = new SimplexNoiseSampler(chunkRandom);
    }
    @Override
    protected Codec<? extends BiomeSource> getCodec() {
        return CODEC;
    }

    @Override
    public RegistryEntry<Biome> getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise) {
        return null;
    }
}
