package com.kpabr.backrooms.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.LiteralText;
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

	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		state = (BlockState)state.cycle(LIT);
		world.setBlockState(pos, state, 2);

		return ActionResult.success(world.isClient);
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(LIT);
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (random.nextDouble() < 0.1D) {
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
