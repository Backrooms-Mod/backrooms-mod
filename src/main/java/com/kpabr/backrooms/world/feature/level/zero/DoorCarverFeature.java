package com.kpabr.backrooms.world.feature.level.zero;

import java.util.Random;

import com.mojang.serialization.Codec;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class DoorCarverFeature extends Feature<DefaultFeatureConfig> {

	public DoorCarverFeature(Codec<DefaultFeatureConfig> configCodec) {
		super(configCodec);
	}

	@Override
	public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
		return generate(context.getWorld(), context.getGenerator(), context.getRandom(), context.getOrigin(), context.getConfig());
	}

	public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos pos, DefaultFeatureConfig config) {

		for (Direction dir : Direction.values()) {
			if (!dir.equals(Direction.UP) && !dir.equals(Direction.DOWN)) {
				if (random.nextBoolean() && random.nextBoolean() && random.nextBoolean()) {
					if (!world.isAir(pos.offset(dir))) {
						createWall(world, random, pos, dir);
					}
				}
			}
		}

		if (!world.isAir(pos.north()) && !world.isAir(pos.east()) && !world.isAir(pos.south()) && !world.isAir(pos.west())) {
			Direction dir = Direction.random(random);
			createWall(world, random, pos, dir.equals(Direction.UP) || dir.equals(Direction.DOWN) ? Direction.NORTH : dir);
		}

		return true;
	}

	public static void createWall(StructureWorldAccess world, Random random, BlockPos pos, Direction dir) {
		int addedOffset = random.nextInt(4) + 2;
		BlockPos.iterate(pos.offset(dir, addedOffset), pos.offset(dir, addedOffset + 1).add(0, 3, 0)).forEach((blockPos) -> {
			world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 3);
		});
	}

}
