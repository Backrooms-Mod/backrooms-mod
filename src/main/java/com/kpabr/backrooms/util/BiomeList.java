package com.kpabr.backrooms.util;

import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

import java.util.*;
import java.util.stream.Stream;

public class BiomeList {
    public SortedMap<Double, RegistryKey<Biome>> biomeList = new TreeMap<>();

    public BiomeList addEntry(RegistryKey<Biome> biome, double chance) {
        this.biomeList.put(chance, biome);
        return this;
    }

    public SortedMap<Double, RegistryKey<Biome>> getBiomeList() {
        return this.biomeList;
    }

    public Stream<RegistryKey<Biome>> getBiomes() {
        return this.biomeList.values().stream();
    }

    public Set<Double> getChances() {
        return this.biomeList.keySet();
    }

}

