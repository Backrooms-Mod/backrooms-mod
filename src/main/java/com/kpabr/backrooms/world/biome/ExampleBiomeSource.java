package com.kpabr.backrooms.world.biome;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.init.BackroomsLevels;
import com.kpabr.backrooms.util.BiomeList;
import com.kpabr.backrooms.util.BiomeRegistryList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;

public class ExampleBiomeSource extends BaseBiomeSource {
    public static final Codec<ExampleBiomeSource> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(RegistryOps.createRegistryCodec(Registry.BIOME_KEY).forGetter((biomeSource) ->
                    biomeSource.BIOME_REGISTRY), Codec.LONG.fieldOf("seed").stable().forGetter((biomeSource) ->
                    biomeSource.seed)).apply(instance, instance.stable(ExampleBiomeSource::new)));

    public ExampleBiomeSource(Registry<Biome> biomeRegistry, long seed) {
        super(biomeRegistry, seed, new
                BiomeList()
                .addEntry(BackroomsLevels.CRIMSON_WALLS_BIOME, 0.3)
                .addEntry(BackroomsLevels.LEVEL_ZERO_NORMAL_BIOME, 3)
        );
    }

    @Override
    protected Codec<? extends BiomeSource> getCodec() {
        return CODEC;
    }

    @Override
    public BiomeSource withSeed(long seed) {
        return new ExampleBiomeSource(BIOME_REGISTRY, seed);
    }
}
