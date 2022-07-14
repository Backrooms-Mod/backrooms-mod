package com.kpabr.backrooms.init;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.tag.Tag;


public class BackroomsFlammableBlocks implements ModInitializer {
    @Override
    public void onInitialize() {
        FlammableBlockRegistry.getDefaultInstance().add(BackroomsBlocks.PYROIL, 90, 0);
    }
}

