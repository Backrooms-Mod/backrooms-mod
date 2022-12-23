package com.kpabr.backrooms.util;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

import java.util.*;
import java.util.stream.Stream;

public class BiomeListBuilder {
    private final TreeMap<Double, RegistryKey<Biome>> biomeList = new TreeMap<>();

    /**
     *
     * @param chance takes double value from 0.0 to 1.0, if less than 0.0 or more than 1.0 it will convert value to 2.0
     */
    public BiomeListBuilder addBiome(RegistryKey<Biome> biome, double chance) {
        if(chance > 1 || chance < 0) {
            this.biomeList.put(2.0, biome);
        } else {
            this.biomeList.put(chance, biome);
        }
        return this;
    }

    public TreeMap<Double, RegistryKey<Biome>> getBiomeList() {
        return this.biomeList;
    }

    public Stream<RegistryKey<Biome>> getBiomes() {
        return this.biomeList.values().stream();
    }
}
