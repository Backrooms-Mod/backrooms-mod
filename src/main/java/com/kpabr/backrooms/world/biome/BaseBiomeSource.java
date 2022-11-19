package com.kpabr.backrooms.world.biome;


import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.init.BackroomsLevels;
import com.kpabr.backrooms.util.BiomeList;
import com.kpabr.backrooms.util.BiomeRegistryList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;

import java.util.Stack;

public abstract class BaseBiomeSource extends BiomeSource {
    // BIOME SOURCE for all the biomes in the level zero dimension!
    // To add new biome you should make a new parameter in both constructors
    // and create new variable containing new biome

    private final SimplexNoiseSampler noise;
    protected final long seed;

    protected final BiomeRegistryList biomeList;
    protected Registry<Biome> BIOME_REGISTRY;

    public BaseBiomeSource(Registry<Biome> biomeRegistry, long seed, BiomeList biomeList) {
        this(seed, BiomeRegistryList.from(biomeRegistry, biomeList));
        this.BIOME_REGISTRY = biomeRegistry;
    }

    protected BaseBiomeSource(long seed, BiomeRegistryList biomeList) {
        super(biomeList.getBiomeEntries());

        this.seed = seed;
        this.biomeList = biomeList;

        ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(seed));
        this.noise = new SimplexNoiseSampler(chunkRandom);
    }

    @Override
    public RegistryEntry<Biome> getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise) {
        double noiseAt = BaseBiomeSource.getNoiseAt(this.noise, x, y, z);
        return biomeList.findNearest(noiseAt);
    }

    public boolean matches(long seed) {
        return this.seed == seed;
    }

    public static double getNoiseAt(SimplexNoiseSampler perlinNoiseSampler, int x, int y, int z) {
        double n = perlinNoiseSampler.sample(x*0.01, y*0.01, z*0.01);

        //Transform the range to [0.0, 1.0], supposing that the range of Noise2D is [-1.0, 1.0]
        n += 1.0;
        n /= 2.0;
        return n;
    }
}