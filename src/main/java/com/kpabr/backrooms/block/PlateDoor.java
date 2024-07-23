package com.kpabr.backrooms.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.DoorBlock;

public class PlateDoor extends DoorBlock {
    public PlateDoor(FabricBlockSettings settings) {
        super(settings, BlockSetType.IRON);
    }
}
