package com.kpabr.backrooms.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.DyeColor;

public class CarpetingBlock extends Block {
    public CarpetingBlock(DyeColor color) {
        super(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL).mapColor(color));
    }
}
