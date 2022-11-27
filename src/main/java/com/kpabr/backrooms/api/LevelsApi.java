package com.kpabr.backrooms.api;

import com.kpabr.backrooms.init.BackroomsLevels;
import net.ludocrypt.limlib.api.LiminalWorld;

public class LevelsApi {
    public static LiminalWorld registerLevel(String name, Class chunkGenerator, Class biomeSource) {
        return BackroomsLevels.registerLevel(name, chunkGenerator, biomeSource);
    }

}
