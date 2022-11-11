package com.kpabr.backrooms.world.biome;

import com.google.common.collect.ImmutableList;
import com.kpabr.backrooms.init.BackroomsLevels;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;


public class Level1BiomeSource extends BiomeSource {
    // BIOME SOURCE for all the biomes in level 1

    public static final Codec<Level1BiomeSource> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(RegistryOps.createRegistryCodec(Registry.BIOME_KEY).forGetter((biomeSource) ->
                    biomeSource.BIOME_REGISTRY), Codec.LONG.fieldOf("seed").stable().forGetter((biomeSource) ->
                    biomeSource.seed)).apply(instance, instance.stable(Level1BiomeSource::new)));
    private final PerlinNoiseSampler noise;
    private final long seed;
    private final RegistryEntry<Biome> cementWallsBiome;
    private final RegistryEntry<Biome> parkingGarageBiome;
    private final RegistryEntry<Biome> warehouseBiome;

    private Registry<Biome> BIOME_REGISTRY;

    public Level1BiomeSource(Registry<Biome> biomeRegistry, long seed) {
        this(seed, biomeRegistry.getOrCreateEntry(BackroomsLevels.CEMENT_WALLS_BIOME), biomeRegistry.getOrCreateEntry(BackroomsLevels.PARKING_GARAGE_BIOME), biomeRegistry.getOrCreateEntry(BackroomsLevels.WAREHOUSE_BIOME));
        this.BIOME_REGISTRY = biomeRegistry;
    }

    private Level1BiomeSource(long seed, RegistryEntry<Biome> cementWallsBiome, RegistryEntry<Biome> parkingGarageBiome, RegistryEntry<Biome> warehouseBiome) {
        super(ImmutableList.of(cementWallsBiome, parkingGarageBiome, warehouseBiome));
        this.seed = seed;
        this.cementWallsBiome = cementWallsBiome;
        this.parkingGarageBiome = parkingGarageBiome;
        this.warehouseBiome = warehouseBiome;
        ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(seed));
        this.noise = new PerlinNoiseSampler(chunkRandom);
    }

    @Override
    protected Codec<? extends BiomeSource> getCodec() {
        return CODEC;
    }

    @Override
    public BiomeSource withSeed(long seed) {
        return new Level1BiomeSource(seed, this.cementWallsBiome, this.parkingGarageBiome, this.warehouseBiome);
    }

    // Also you should use this method
    @Override
    public RegistryEntry<Biome> getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise) {
        double noiseAt = Level1BiomeSource.getNoiseAt(this.noise, x, y, z);
        // Higher values = often spawn of biome
        if (noiseAt <= 0.3) {
            return this.warehouseBiome;
        }
        if (noiseAt <= 0.6) {
            return this.parkingGarageBiome;
        }

        return this.cementWallsBiome;
    }


    public boolean matches(long seed) {
        return this.seed == seed;
    }

    public static double getNoiseAt(PerlinNoiseSampler perlinNoiseSampler, int x, int y, int z) {

        double n = perlinNoiseSampler.sample(x*0.01, y*0.01, z*0.01);

        //Transform the range to [0.0, 1.0], supposing that the range of Noise2D is [-1.0, 1.0]
        n += 1.0;
        n /= 2.0;

        return MathHelper.clamp(n, 0.0, 1.5);
    }
}

