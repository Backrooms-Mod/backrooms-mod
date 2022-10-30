package com.kpabr.backrooms.world.biome;

import com.google.common.collect.ImmutableList;
import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.init.BackroomsLevels;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;

public class LevelZeroBiomeSource extends BiomeSource {
    // BIOME SOURCE for all the biomes in the backrooms test dimension!
    // To add new biome you should make a new parameter in both constructors
    // and create new variable containing new biome

    public static final Codec<LevelZeroBiomeSource> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(RegistryOps.createRegistryCodec(Registry.BIOME_KEY).forGetter((biomeSource) ->
                    biomeSource.BIOME_REGISTRY), Codec.LONG.fieldOf("seed").stable().forGetter((biomeSource) ->
                    biomeSource.seed)).apply(instance, instance.stable(LevelZeroBiomeSource::new)));
    private final SimplexNoiseSampler noise;
    private final long seed;
    private final RegistryEntry<Biome> crimsonWallsBiome;
    private final RegistryEntry<Biome> normalBiome;

    private Registry<Biome> BIOME_REGISTRY;

    public LevelZeroBiomeSource(Registry<Biome> biomeRegistry, long seed) {
        this(seed, biomeRegistry.getOrCreateEntry(BackroomsLevels.LEVEL_ZERO_NORMAL_BIOME), biomeRegistry.getOrCreateEntry(BackroomsLevels.CRIMSON_WALLS_BIOME));
        this.BIOME_REGISTRY = biomeRegistry;
    }

    private LevelZeroBiomeSource(long seed, RegistryEntry<Biome> normalBiome, RegistryEntry<Biome> crimsonWallsBiome) {
        super(ImmutableList.of(normalBiome, crimsonWallsBiome));
        this.seed = seed;
        this.normalBiome = normalBiome;
        this.crimsonWallsBiome = crimsonWallsBiome;
        ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(seed));
        this.noise = new SimplexNoiseSampler(chunkRandom);
    }

    @Override
    protected Codec<? extends BiomeSource> getCodec() {
        return CODEC;
    }

    @Override
    public BiomeSource withSeed(long seed) {
        return new LevelZeroBiomeSource(seed, this.normalBiome, this.crimsonWallsBiome);
    }

    // Also you should use this method
    @Override
    public RegistryEntry<Biome> getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise) {
        int i = x >> 2;
        int j = z >> 2;
        float noiseAt = LevelZeroBiomeSource.getNoiseAt(this.noise, i, j);
        BackroomsMod.LOGGER.info(String.valueOf(noiseAt));
        if (noiseAt > 40.0f) {
            return this.crimsonWallsBiome;
        }
        return this.normalBiome;
    }

    public boolean matches(long seed) {
        return this.seed == seed;
    }

    public static float getNoiseAt(SimplexNoiseSampler simplexNoiseSampler, int i, int j) {
        int k = i / 2;
        int l = j / 2;

        float f = MathHelper.sqrt(i * i + j * j) * 8.0f;
        f = MathHelper.clamp(f, -100.0f, 80.0f);
        return f;
    }
}

