package com.kpabr.backrooms.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

public class FluorescentLightBlock extends Block {

	public static final BooleanProperty LIT = Properties.LIT;

	public FluorescentLightBlock(Settings settings) {
		super(settings);
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(LIT);
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (random.nextDouble() < 0.1) {
			world.setBlockState(pos, state.cycle(LIT));
			for (Direction dir : Direction.values()) {
				BlockPos blockPos = pos.offset(dir);
				if (world.getBlockState(blockPos).isOf(this)) {
					world.setBlockState(blockPos, world.getBlockState(pos));
				}
			}
		}

	}

}
