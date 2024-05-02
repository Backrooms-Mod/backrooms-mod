package com.kpabr.backrooms.util;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

import java.util.*;
import java.util.stream.Stream;

public class BiomeListBuilderNew {
    private final HashMap<LevelParameters, RegistryKey<Biome>> biomeList = new HashMap<>();

    public BiomeListBuilderNew addBiome(RegistryKey<Biome> biome, LevelParameters parameters) {
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
