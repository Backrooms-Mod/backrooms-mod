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

public class WoolenCarpetFeature extends Feature<DefaultFeatureConfig> {

	private ImmutableList<Pair<BlockState, OctavePerlinNoiseSampler>> blockstateNoisemap = null;

	public WoolenCarpetFeature(Codec<DefaultFeatureConfig> configCodec) {
		super(configCodec);
	}


	@Override
	public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
		return generate(context.getWorld(), context.getGenerator(), context.getRandom(), context.getOrigin(), context.getConfig());
	}

	public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos pos, DefaultFeatureConfig config) {
		if (blockstateNoisemap == null) {
			blockstateNoisemap = MazeChunkGenerator.createNoise(ImmutableList.of(BackroomsBlocks.WOOLEN_CARPET.getDefaultState(), BackroomsBlocks.WOOLEN_CARPET.getDefaultState(), BackroomsBlocks.MOLDY_WOOLEN_CARPET.getDefaultState()), world.getSeed());
		}

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				BlockPos blockPos = pos.add(x, 0, z);
				BlockState state = this.blockstateNoisemap.stream().max(Comparator.comparing((entry) -> {
					return ((Pair<BlockState, OctavePerlinNoiseSampler>) entry).getRight().sample(blockPos.getX(), blockPos.getY(), blockPos.getZ());
				})).get().getLeft();

				world.setBlockState(blockPos, state, 3);
			}
		}

		return true;
	}

}
