package com.kpabr.backrooms.util;

import com.kpabr.backrooms.BackroomsMod;
import net.fabricmc.loader.impl.game.minecraft.MinecraftGameProvider;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Stream;


// used internally

public class BiomeRegistryList {
    public SortedMap<Double, RegistryEntry<Biome>> biomeList = new TreeMap<>();

    private RegistryEntry<Biome> defaultBiome;

    public void addEntry(RegistryEntry<Biome> biome, double chance) {
        this.biomeList.put(chance, biome);
    }

    public SortedMap<Double, RegistryEntry<Biome>> getBiomeList() {
        return this.biomeList;
    }

    public Stream<RegistryEntry<Biome>> getBiomeEntries() {
        BackroomsMod.LOGGER.info(this.biomeList.values().stream().toString());
        return this.biomeList.values().stream();
    }

    public Set<Double> getChances() {
        return this.biomeList.keySet();
    }

    public RegistryEntry<Biome> findNearest(double n) {
        double max = 2;

        for(double chance : getChances()) {
            if(chance < max && n <= chance) {
                max = chance;
            }
        }

        return biomeList.get(max);
    }

    public static BiomeRegistryList from(Registry<Biome> biomeRegistry, BiomeList biomeList) {
        BiomeRegistryList temp = new BiomeRegistryList();

        for (Map.Entry<Double, RegistryKey<Biome>> entry : biomeList.getBiomeList().entrySet()) {
            BackroomsMod.LOGGER.info(entry.getValue().toString());
            temp.addEntry(biomeRegistry.getOrCreateEntry(entry.getValue()), entry.getKey());
        }
        return temp;
    }

}

