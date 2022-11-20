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

import static java.lang.Double.valueOf;


// used internally

public class BiomeRegistryList {
    public static final Double DEFAULT_CHANCE_VALUE = 2.D; // Use for default biome type
    public TreeMap<Double, RegistryEntry<Biome>> biomeList = new TreeMap<>();

    public Stream<RegistryEntry<Biome>> getBiomeEntries() {
        BackroomsMod.LOGGER.info(this.biomeList.values().stream().toString());
        return this.biomeList.values().stream();
    }

    public RegistryEntry<Biome> findNearest(double key) {
        return biomeList.floorEntry(key).getValue();
    }
    public static BiomeRegistryList from(Registry<Biome> biomeRegistry, BiomeList biomeList) {
        BiomeRegistryList list = new BiomeRegistryList();

        biomeList.getBiomeList().forEach((key, value) -> {
            BackroomsMod.LOGGER.info(value.toString());
            list.biomeList.put(key, biomeRegistry.getOrCreateEntry(value));
        });
        return list;
    }
}

