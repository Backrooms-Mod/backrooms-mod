package com.kpabr.backrooms.block;

import net.minecraft.util.math.random.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class FluorescentLightBlock extends Block {
	public static final BooleanProperty LIT = Properties.LIT;

	public FluorescentLightBlock(Settings settings) {
		super(settings);
	}

	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
			BlockHitResult hit) {
		state = state.cycle(LIT);
		world.setBlockState(pos, state, 1, 0);

		return ActionResult.success(world.isClient);
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(LIT);
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (random.nextDouble() < 0.1D) {
			world.setBlockState(pos, state.cycle(LIT), 1, 0);
			final BlockState newState = world.getBlockState(pos);
			for (Direction dir : Direction.values()) {
				final BlockPos currentDirectionBlock = pos.offset(dir);
				if (world.getBlockState(currentDirectionBlock).isOf(this)) {
					world.setBlockState(currentDirectionBlock, newState, 1, 0);
				}
			}
		}
	}
}
