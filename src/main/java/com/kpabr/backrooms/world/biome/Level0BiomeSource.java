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

public class Level0BiomeSource extends BaseBiomeSource {
    public static final Codec<Level0BiomeSource> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    RegistryOps.createRegistryCodec(Registry.BIOME_KEY)
                            .forGetter((biomeSource) -> biomeSource.BIOME_REGISTRY),
                    Codec.LONG.fieldOf("seed")
                            .stable()
                            .forGetter((biomeSource) -> biomeSource.seed)
            ).apply(instance, instance.stable(Level0BiomeSource::new)));

    public Level0BiomeSource(Registry<Biome> biomeRegistry, long seed) {
        super(biomeRegistry, seed, new BiomeList()
                .addEntry(BackroomsLevels.CRIMSON_WALLS_BIOME, 0.3)
                .addEntry(BackroomsLevels.DECREPIT_BIOME, 0.4)
                .addEntry(BackroomsLevels.LEVEL_ZERO_NORMAL_BIOME, BiomeRegistryList.DEFAULT_CHANCE_VALUE)
        );
    }

    @Override
    protected Codec<? extends BiomeSource> getCodec() {
        return CODEC;
    }

    @Override
    public BiomeSource withSeed(long seed) {
        return new Level0BiomeSource(BIOME_REGISTRY, seed);
    }
}
