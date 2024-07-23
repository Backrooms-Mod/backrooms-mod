package com.kpabr.backrooms.block;

import net.minecraft.util.math.random.Random;

import com.kpabr.backrooms.init.BackroomsBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class MoldyTileBlock extends Block {
	public static final int GROW_CHANCE = 5;

	public MoldyTileBlock(Settings settings) {
		super(settings);
	}

	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.DESTROY;
	}

	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (random.nextInt(GROW_CHANCE) == 0) {
			final Direction direction = Direction.random(random);
			final BlockPos blockPos = pos.offset(direction);
			final BlockState blockState = world.getBlockState(blockPos);

			if (canGrowIn(blockState)) {
				BlockState blockState2 = BackroomsBlocks.TILEMOLD.getDefaultState()
						.with(TilemoldBlock.FACING, direction)
						.with(TilemoldBlock.WATERLOGGED, blockState.getFluidState().getFluid() == Fluids.WATER);
				world.setBlockState(blockPos, blockState2);
			}
		}
	}

	public static boolean canGrowIn(BlockState state) {
		return state.isAir() || state.isOf(Blocks.WATER) && state.getFluidState().getLevel() == 8;
	}
}
