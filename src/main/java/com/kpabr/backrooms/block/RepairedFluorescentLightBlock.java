package com.kpabr.backrooms.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RepairedFluorescentLightBlock extends Block {
	public static final BooleanProperty LIT = Properties.LIT;
	public static final BooleanProperty POWERED = Properties.POWERED;

	public RepairedFluorescentLightBlock(Settings settings) {
		super(settings);
		this.setDefaultState(getStateManager().getDefaultState().with(POWERED, false).with(LIT, false));
	}

	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
			state = (BlockState)state.cycle(LIT);
			world.setBlockState(pos, state, 2);

			return ActionResult.success(world.isClient);
	}

	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		if (!world.isClient) {
			boolean bl = world.isReceivingRedstonePower(pos);
			if (bl != (Boolean)state.get(POWERED)) {
				if ((Boolean)state.get(LIT) != bl) {
					state = (BlockState)state.with(LIT, bl);
				}

				world.setBlockState(pos, (BlockState)state.with(POWERED, bl), 2);
			}

		}
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(LIT);
		builder.add(POWERED);
	}
}
