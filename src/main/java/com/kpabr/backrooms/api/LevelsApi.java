package com.kpabr.backrooms.api;

import com.kpabr.backrooms.init.BackroomsLevels;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class LevelsApi {
    public static void registerLevel(Identifier name, Class chunkGenerator, Class biomeSource) {
        BackroomsLevels.registerLevel(name, chunkGenerator, biomeSource);
    }

}
