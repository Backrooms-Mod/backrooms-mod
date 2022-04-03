package com.kpabr.backrooms.world.feature.level.zero;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import com.kpabr.backrooms.block.TileBlock;
import com.kpabr.backrooms.init.BackroomsBlocks;

import java.util.Random;

public class FluorescentLightFeature extends Feature<DefaultFeatureConfig> {

	public FluorescentLightFeature(Codec<DefaultFeatureConfig> configCodec) {
		super(configCodec);
	}

	@Override
	public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
		return generate(context.getWorld(), context.getGenerator(), context.getRandom(), context.getOrigin(), context.getConfig());
	}


	public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos pos, DefaultFeatureConfig config) {
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				if (x % 4 == 0 && z % 4 == 0) {
					if(world.isAir(pos.add(x + 1, -1, z)) && world.isAir(pos.add(x + 2, -1, z))) {
						world.setBlockState(pos.add(x + 1, 0, z), BackroomsBlocks.FLUORESCENT_LIGHT.getDefaultState(), 2);
						world.setBlockState(pos.add(x + 2, 0, z), BackroomsBlocks.FLUORESCENT_LIGHT.getDefaultState(), 2);
					}
				}
			}
		}
		return true;
	}

}
