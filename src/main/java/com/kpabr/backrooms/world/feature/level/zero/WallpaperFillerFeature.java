package com.kpabr.backrooms.world.feature.level.zero;

import java.util.Comparator;
import java.util.Random;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;

import net.minecraft.world.gen.feature.util.FeatureContext;
import com.kpabr.backrooms.init.BackroomsBlocks;
import com.kpabr.backrooms.world.chunk.MazeChunkGenerator;
import net.minecraft.block.BlockState;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class WallpaperFillerFeature extends Feature<DefaultFeatureConfig> {

	private ImmutableList<Pair<BlockState, OctavePerlinNoiseSampler>> blockstateNoisemap = null;

	public WallpaperFillerFeature(Codec<DefaultFeatureConfig> configCodec) {
		super(configCodec);
	}

	@Override
	public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
		return generate(context.getWorld(), context.getGenerator(), context.getRandom(), context.getOrigin(), context.getConfig());
	}


	public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos pos, DefaultFeatureConfig config) {
		if (blockstateNoisemap == null) {
			blockstateNoisemap = MazeChunkGenerator.createNoise(ImmutableList.of(BackroomsBlocks.PATTERNED_WALLPAPER.getDefaultState(), BackroomsBlocks.STRIPED_WALLPAPER.getDefaultState(), BackroomsBlocks.DOTTED_WALLPAPER.getDefaultState(), BackroomsBlocks.BLANK_WALLPAPER.getDefaultState(), BackroomsBlocks.STRIPED_WALLPAPER.getDefaultState(), BackroomsBlocks.BLANK_WALLPAPER.getDefaultState(), BackroomsBlocks.DOTTED_WALLPAPER.getDefaultState(), BackroomsBlocks.PATTERNED_WALLPAPER.getDefaultState(), BackroomsBlocks.PLASTERWALL.getDefaultState()), world.getSeed() ^ -4);
		}

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				BlockPos blockPosXZ = pos.add(x, 0, z);
				BlockState state = this.blockstateNoisemap.stream().max(Comparator.comparing((entry) -> {
					return entry.getRight().sample(blockPosXZ.getX() / 5, 100, blockPosXZ.getZ() / 5);
				})).get().getLeft();
				for (int y = 0; y < 4; y++) {
					BlockPos blockPos = blockPosXZ.add(0, y, 0);
					if (!world.isAir(blockPos)) {
						world.setBlockState(blockPos, state, 3);
					}
				}
			}
		}
		return true;
	}

}
