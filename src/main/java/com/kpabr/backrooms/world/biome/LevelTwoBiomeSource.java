package com.kpabr.backrooms.world.biome;

import com.kpabr.backrooms.init.BackroomsLevels;
import com.kpabr.backrooms.util.BiomeListBuilder;
import com.kpabr.backrooms.util.BiomeRegistryList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;

public class LevelTwoBiomeSource extends BaseBiomeSource {
    public static final Codec<LevelTwoBiomeSource> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    RegistryOps.createRegistryCodec(Registry.BIOME_KEY)
                            .forGetter((biomeSource) -> biomeSource.BIOME_REGISTRY),
                    Codec.LONG.fieldOf("seed")
                            .stable()
                            .forGetter((biomeSource) -> biomeSource.seed)
            ).apply(instance, instance.stable(LevelTwoBiomeSource::new)));


    public LevelTwoBiomeSource(Registry<Biome> biomes, long seed) {
        super(biomes, seed, new BiomeListBuilder()
                .addBiome(BackroomsLevels.PIPES_BIOME, BiomeRegistryList.DEFAULT_CHANCE_VALUE)
        );
    }

    @Override
    public BiomeSource withSeed(long seed) {
        return new LevelTwoBiomeSource(BIOME_REGISTRY, seed);
    }
    @Override
    protected Codec<? extends BiomeSource> getCodec() {
        return CODEC;
    }
}
