package com.kpabr.backrooms.api;

import com.kpabr.backrooms.init.BackroomsLevels;
import com.kpabr.backrooms.world.biome.BaseBiomeSource;
import net.ludocrypt.limlib.api.LiminalWorld;
import net.ludocrypt.limlib.api.world.AbstractNbtChunkGenerator;

public class LevelsApi {
    public static<T extends AbstractNbtChunkGenerator, S extends BaseBiomeSource> LiminalWorld addLevel(String name, Class<T> chunkGenerator, Class<S> biomeSource) {
        return BackroomsLevels.addLevel(name, chunkGenerator, biomeSource);
    }
}
