package com.kpabr.backrooms.world.biome.sources;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.init.BackroomsLevels;
import com.kpabr.backrooms.util.BiomeListBuilderNew;
import com.kpabr.backrooms.util.BiomeRegistryList;
import com.kpabr.backrooms.util.BiomeRegistryListNew;
import com.kpabr.backrooms.util.LevelParameters;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil.MultiNoiseSampler;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LevelZeroBiomeSource extends BiomeSource {

    public static final Codec<LevelZeroBiomeSource> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    RegistryOps.createRegistryCodec(Registry.BIOME_KEY)
                            .forGetter((biomeSource) -> biomeSource.BIOME_REGISTRY)
            ).apply(instance, instance.stable(LevelZeroBiomeSource::new)));


    private final BiomeRegistryListNew biomeList;
    
    private SimplexNoiseSampler temperatureNoiseSampler;
    private SimplexNoiseSampler moistnessNoiseSampler;
    private SimplexNoiseSampler integrityNoiseSampler;
    private SimplexNoiseSampler purityNoiseSampler;
    private SimplexNoiseSampler toxicityNoiseSampler;
    private boolean isNoiseInitialized = false;
    
    private Registry<Biome> BIOME_REGISTRY;

    public LevelZeroBiomeSource(Registry<Biome> biomeRegistry) {
        this(biomeRegistry, BiomeRegistryListNew.from(biomeRegistry, new BiomeListBuilderNew()
        .addBiome(BackroomsLevels.CRIMSON_WALLS_BIOME, new LevelParameters(0.6, 0.65, 0.75, 0.45, 0.15, 1))
        .addBiome(BackroomsLevels.DECREPIT_BIOME, new LevelParameters(0.55, 0.7, 0.5, 0.3, 0.25, 1.2))
        .addBiome(BackroomsLevels.MEGALOPHOBIA_BIOME, new LevelParameters(0.45, 0.55, 0.65, 0.4, 0.05, 1))
        .addBiome(BackroomsLevels.YELLOW_WALLS_BIOME, new LevelParameters(0.5, 0.4, 0.75, 0.45, 0.05, 0.9))));
        this.BIOME_REGISTRY = biomeRegistry;
    }

    private LevelZeroBiomeSource(Registry<Biome> biomeRegistry, BiomeRegistryListNew biomeList) {
        super(biomeList.getBiomeEntries());
        this.biomeList = biomeList;
    }

    @Override
    public RegistryEntry<Biome> getBiome(int x, int y, int z, MultiNoiseSampler noise) {
        if (!this.isNoiseInitialized) {

            long seed = BackroomsLevels.LEVEL_0_WORLD.getSeed();
            Random randomGenerator = new Random(seed);
            
            // Generate five different random seeds based on the world seed
            long[] randomSeeds = new long[5];
            for (int i = 0; i < 5; i++) {
                randomSeeds[i] = randomGenerator.nextLong();
            }

            this.temperatureNoiseSampler = new SimplexNoiseSampler(new ChunkRandom(new AtomicSimpleRandom(randomSeeds[0])));
            
            this.moistnessNoiseSampler = new SimplexNoiseSampler(new ChunkRandom(new AtomicSimpleRandom(randomSeeds[1])));
            
            this.integrityNoiseSampler = new SimplexNoiseSampler(new ChunkRandom(new AtomicSimpleRandom(randomSeeds[2])));
            
            this.purityNoiseSampler = new SimplexNoiseSampler(new ChunkRandom(new AtomicSimpleRandom(randomSeeds[3])));
            
            this.toxicityNoiseSampler = new SimplexNoiseSampler(new ChunkRandom(new AtomicSimpleRandom(randomSeeds[4])));
            this.isNoiseInitialized = true;
        }
        
        double temperatureNoiseAt = getNoiseAt(this.temperatureNoiseSampler, x, y, z);
        double moistnessNoiseAt = getNoiseAt(this.moistnessNoiseSampler, x, y, z);
        double integrityNoiseAt = getNoiseAt(this.integrityNoiseSampler, x, y, z);
        double purityNoiseAt = getNoiseAt(this.purityNoiseSampler, x, y, z);
        double toxicityNoiseAt = getNoiseAt(this.toxicityNoiseSampler, x, y, z);
        
        return biomeList.findNearest(new LevelParameters(temperatureNoiseAt, moistnessNoiseAt, integrityNoiseAt, purityNoiseAt, toxicityNoiseAt, 0d));
    }

    public static double getNoiseAt(SimplexNoiseSampler perlinNoiseSampler, int x, int y, int z) {
        double n = perlinNoiseSampler.sample(x*0.01, y*0.01, z*0.01);

        //Transform the range to [0.0, 1.0], supposing that the range of Noise2D is [-1.0, 1.0]
        n += 1.0;
        n /= 2.0;
        
        return n;
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
