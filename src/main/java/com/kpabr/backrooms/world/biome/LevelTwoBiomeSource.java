package com.kpabr.backrooms.world.biome;

import com.kpabr.backrooms.init.BackroomsLevels;
import com.kpabr.backrooms.util.CorridorDirection;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

public class LevelTwoBiomeSource extends BiomeSource {
    public static final Codec<LevelTwoBiomeSource> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    RegistryOps.createRegistryCodec(Registry.BIOME_KEY)
                            .forGetter((biomeSource) -> biomeSource.BIOME_REGISTRY),
                    Codec.LONG.fieldOf("seed")
                            .stable()
                            .forGetter((biomeSource) -> biomeSource.seed)
            ).apply(instance, instance.stable(LevelTwoBiomeSource::new)));


    private final long seed;
    protected Registry<Biome> BIOME_REGISTRY;
    private final SimplexNoiseSampler xPlaneNoise;
    private final SimplexNoiseSampler zPlaneNoise;
    public final ArrayList<CorridorDirection> zeroDirections = new ArrayList<>(Arrays.asList(
            CorridorDirection.EAST_WEST,
            CorridorDirection.NORTH_SOUTH)
    );
    private final RegistryEntry<Biome> PIPES_BIOME;
    private final RegistryEntry<Biome> COLD_PIPES_BIOME;
    private final RegistryEntry<Biome> HOT_PIPES_BIOME;
    private final RegistryEntry<Biome> EMPTY_BIOME;

    public LevelTwoBiomeSource(Registry<Biome> registry, long seed) {
        super(Stream.of(
                registry.getOrCreateEntry(BackroomsLevels.PIPES_BIOME),
                registry.getOrCreateEntry(BackroomsLevels.EMPTY_BIOME),
                registry.getOrCreateEntry(BackroomsLevels.HOT_PIPES_BIOME),
                registry.getOrCreateEntry(BackroomsLevels.COLD_PIPES_BIOME)));
        PIPES_BIOME = registry.getOrCreateEntry(BackroomsLevels.PIPES_BIOME);
        HOT_PIPES_BIOME = registry.getOrCreateEntry(BackroomsLevels.HOT_PIPES_BIOME);
        COLD_PIPES_BIOME = registry.getOrCreateEntry(BackroomsLevels.COLD_PIPES_BIOME);
        EMPTY_BIOME = registry.getOrCreateEntry(BackroomsLevels.EMPTY_BIOME);
        final ChunkRandom xPlaneRandom = new ChunkRandom(new AtomicSimpleRandom(seed));
        final ChunkRandom zPlaneRandom = new ChunkRandom(new AtomicSimpleRandom(seed+1));
        this.seed = seed;
        this.xPlaneNoise = new SimplexNoiseSampler(xPlaneRandom);
        this.zPlaneNoise = new SimplexNoiseSampler(zPlaneRandom);
    }

    @Override
    public RegistryEntry<Biome> getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise) {
        if(z == 0 || x == 0) {
            return PIPES_BIOME;
        }
        final double xNoise = xPlaneNoise.sample(x, 0), zNoise = zPlaneNoise.sample(z, 0);
        if(xNoise < -0.5) {
            // Minimum value of 3 chunks before new corridor
            if(Math.abs(x) - 3 <= 0) {
                return EMPTY_BIOME;
            }
            final int chunksToCheckBeforeCurrentChunk = 3;
            // If 4 <= x <= 6, only chunks in [4; x) bounds will be checked
            // Else if -6 <= x <= -4, only chunks in [4; x) bounds will be checked
            if(x > 0) {
                for (int i = Math.max(x - chunksToCheckBeforeCurrentChunk, 4); i < x; i++) {
                    if (xPlaneNoise.sample(i, 0) < -0.5) {
                        return EMPTY_BIOME;
                    }
                }
            } else {
                for (int i = Math.min(x + chunksToCheckBeforeCurrentChunk, -4); i < x; i++) {
                    if (xPlaneNoise.sample(i, 0) < -0.5) {
                        return EMPTY_BIOME;
                    }
                }
            }

            if(xNoise < -0.75) return COLD_PIPES_BIOME;
            return PIPES_BIOME;
        }
        else if(zNoise > 0.5) {
            if(Math.abs(z) - 3 <= 0) {
                return EMPTY_BIOME;
            }

            final int chunksToCheckBeforeCurrentChunk = 3;
            // If 4 <= z <= 6, only chunks in [4; z) bounds will be checked
            // Else if -6 <= z <= -4, only chunks in [4; z) bounds will be checked
            if(z > 0) {
                for (int i = Math.max(z - chunksToCheckBeforeCurrentChunk, 4); i < z; i++) {
                    if (zPlaneNoise.sample(i, 0) > 0.5) {
                        return EMPTY_BIOME;
                    }
                }
            } else {
                for (int i = Math.min(z + chunksToCheckBeforeCurrentChunk, -4); i < z; i++) {
                    if (zPlaneNoise.sample(i, 0) > 0.5) {
                        return EMPTY_BIOME;
                    }
                }
            }

            if(zNoise > 0.75) return HOT_PIPES_BIOME;
            return PIPES_BIOME;
        }

        return EMPTY_BIOME;
    }

    public BiomeSource withSeed(long seed) {
        return new LevelTwoBiomeSource(BIOME_REGISTRY, seed);
    }

    @Override
    protected Codec<? extends BiomeSource> getCodec() {
        return CODEC;
    }
}
