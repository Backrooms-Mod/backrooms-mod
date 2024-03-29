package com.kpabr.backrooms.util;

import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;

import java.util.TreeMap;
import java.util.stream.Stream;

public class BiomeRegistryList {
    public static final Double DEFAULT_CHANCE_VALUE = 2.D; // Use for default biome type
    private final TreeMap<Double, RegistryEntry<Biome>> biomeList = new TreeMap<>();

    public Stream<RegistryEntry<Biome>> getBiomeEntries() {
        return this.biomeList.values().stream();
    }

    public RegistryEntry<Biome> findNearest(double key) {
        return biomeList.ceilingEntry(key).getValue();
    }

    public static BiomeRegistryList from(Registry<Biome> biomeRegistry, BiomeListBuilder biomeList) {
        final BiomeRegistryList list = new BiomeRegistryList();

        biomeList.getBiomeList().forEach((key, value) ->
            list.biomeList.put(key, biomeRegistry.getOrCreateEntry(value))
        );
        return list;
    }
}