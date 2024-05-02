package com.kpabr.backrooms.api;

import com.kpabr.backrooms.init.BackroomsLevels;
import com.mojang.serialization.Codec;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class LevelsApi {
    public static RegistryKey<World> addLevel(String namespace, String levelName, String biomeSourceName, Codec<? extends ChunkGenerator> chunkGenerator, Codec<? extends BiomeSource> biomeSource) {
        return BackroomsLevels.addLevel(namespace, levelName, biomeSourceName, chunkGenerator, biomeSource);
    }
}
