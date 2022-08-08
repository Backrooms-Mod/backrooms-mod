package com.kpabr.backrooms.init;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.block.Block;


public class BackroomsFlammableBlocks {

    public static void init() {
        FlammableBlockRegistry.getDefaultInstance().add(BackroomsBlocks.PYROIL, 1, 100);
    }
}

