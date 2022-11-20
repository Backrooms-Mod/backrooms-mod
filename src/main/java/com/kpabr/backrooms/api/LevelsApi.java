package com.kpabr.backrooms.api;

import com.kpabr.backrooms.init.BackroomsLevels;
import net.ludocrypt.limlib.api.LiminalWorld;
import net.minecraft.util.Identifier;

public class LevelsApi {
    public static LiminalWorld registerLevel(Identifier name, Class chunkGenerator, Class biomeSource) {
        return BackroomsLevels.registerLevel(name, chunkGenerator, biomeSource);
    }

}
