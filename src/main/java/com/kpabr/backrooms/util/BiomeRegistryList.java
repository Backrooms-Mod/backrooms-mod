package com.kpabr.backrooms.util;

import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;

import java.util.HashMap;
import java.util.stream.Stream;

public class BiomeRegistryList {
    public static final Double DEFAULT_CHANCE_VALUE = 2.D; // Use for default biome type
    private final HashMap<LevelParameters, RegistryEntry<Biome>> biomeList = new HashMap<>();

    public Stream<RegistryEntry<Biome>> getBiomeEntries() {
        return this.biomeList.values().stream();
    }

    public RegistryEntry<Biome> findNearest(LevelParameters params) {

        LevelParameters mostSimilarKey = null;
        double smallestDistance = Float.MAX_VALUE;

        for (LevelParameters key : biomeList.keySet()) {
            double distance = calculateDistance(key, params);
            distance = distance * key.rareness;
            if (distance < smallestDistance) {
                smallestDistance = distance;
                mostSimilarKey = key;
            }
        }

        return biomeList.get(mostSimilarKey);
    }

    private double calculateDistance(LevelParameters key, LevelParameters params) {
        // Calculate Euclidean distance between key values and params from the input
        double sum = 0;
        sum += Math.pow(key.temperature - params.temperature, 2);
        sum += Math.pow(key.moistness - params.moistness, 2);
        sum += Math.pow(key.integrity - params.integrity, 2);
        sum += Math.pow(key.purity - params.purity, 2);
        sum += Math.pow(key.toxicity - params.toxicity, 2);
        return (double) Math.sqrt(sum);
    }

    public static BiomeRegistryList from(BiomeListBuilder biomeList, RegistryEntryLookup<Biome> biomeRegistry) {
        final BiomeRegistryList list = new BiomeRegistryList();

        biomeList.getBiomeList().forEach((key, value) -> list.biomeList.put(key, biomeRegistry.getOrThrow(value)));
        return list;
    }
}