package com.kpabr.backrooms.mixins;

import java.util.HashMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.kpabr.backrooms.access.ContainsSkyboxBlocksAccess;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.util.math.BlockPos;

@Mixin(ChunkBuilder.ChunkData.class)
public class ChunkBuilderChunkDataMixin implements ContainsSkyboxBlocksAccess {

	@Unique
	private final HashMap<BlockPos, BlockState> skyboxBlocks = new HashMap<BlockPos, BlockState>();

	@Override
	public HashMap<BlockPos, BlockState> getSkyboxBlocks() {
		return skyboxBlocks;
	}

}
