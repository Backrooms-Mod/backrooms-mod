package com.kpabr.backrooms.world.biome;

import com.kpabr.backrooms.init.BackroomsLevels;
import com.kpabr.backrooms.util.BiomeList;
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

    public LevelTwoBiomeSource(Registry<Biome> biomeRegistry, long seed) {
        super(biomeRegistry, seed, new BiomeList()
                .addEntry(BackroomsLevels.PIPES_BIOME, BiomeRegistryList.DEFAULT_CHANCE_VALUE)
        );
    }

    @Override
    protected Codec<? extends BiomeSource> getCodec() {
        return CODEC;
    }

    @Override
    public BiomeSource withSeed(long seed) {
        return new LevelTwoBiomeSource(BIOME_REGISTRY, seed);
    }
}
