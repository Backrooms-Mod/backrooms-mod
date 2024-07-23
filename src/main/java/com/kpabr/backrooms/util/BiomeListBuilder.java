package com.kpabr.backrooms.util;

import net.minecraft.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

import java.util.*;
import java.util.stream.Stream;

public class BiomeListBuilder {
    private final HashMap<LevelParameters, RegistryKey<Biome>> biomeList = new HashMap<>();

    public BiomeListBuilder addBiome(RegistryKey<Biome> biome, LevelParameters parameters) {
        this.biomeList.put(parameters, biome);
        return this;
    }

    public HashMap<LevelParameters, RegistryKey<Biome>> getBiomeList() {
        return this.biomeList;
    }

    public Stream<RegistryKey<Biome>> getBiomes() {
        return this.biomeList.values().stream();
    }
}
