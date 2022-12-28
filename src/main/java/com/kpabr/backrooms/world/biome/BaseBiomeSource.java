package com.kpabr.backrooms.world.biome;

import com.kpabr.backrooms.util.BiomeListBuilder;
import com.kpabr.backrooms.util.BiomeRegistryList;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;

public abstract class BaseBiomeSource extends BiomeSource {

    private final SimplexNoiseSampler noise;
    protected final long seed;

    protected final BiomeRegistryList biomeList;
    protected Registry<Biome> BIOME_REGISTRY;

    public BaseBiomeSource(Registry<Biome> biomeRegistry, long seed, BiomeListBuilder biomeList) {
        this(seed, BiomeRegistryList.from(biomeRegistry, biomeList));
        this.BIOME_REGISTRY = biomeRegistry;
    }

    protected BaseBiomeSource(long seed, BiomeRegistryList biomeList) {
        super(biomeList.getBiomeEntries());

        this.seed = seed;
        this.biomeList = biomeList;

        final ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(seed));
        this.noise = new SimplexNoiseSampler(chunkRandom);
    }

    @Override
    public RegistryEntry<Biome> getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise) {
        double noiseAt = getNoiseAt(this.noise, x, y, z);
        return biomeList.findNearest(noiseAt);
    }

    public static double getNoiseAt(SimplexNoiseSampler perlinNoiseSampler, int x, int y, int z) {
        double n = perlinNoiseSampler.sample(x*0.01, y*0.01, z*0.01);

        //Transform the range to [0.0, 1.0], supposing that the range of Noise2D is [-1.0, 1.0]
        n += 1.0;
        n /= 2.0;
        return n;
    }
}