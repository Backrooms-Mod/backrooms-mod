package com.kpabr.backrooms.block;

import com.kpabr.backrooms.init.BackroomsBlocks;

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
import net.minecraft.world.WorldAccess;

public class RepairedFluorescentLightBlock extends Block {
	public static final BooleanProperty LIT = Properties.LIT;
	public static final BooleanProperty POWERED = Properties.POWERED;
	public static final BooleanProperty POWERED_BY_MACHINERY = BooleanProperty.of("powered_by_machinery");
	public static final BooleanProperty TURNED_ON_BY_PLAYER = BooleanProperty.of("turned_on_by_player");

	public RepairedFluorescentLightBlock(Settings settings) {
		super(settings);
		this.setDefaultState(getStateManager().getDefaultState()
				.with(POWERED, false)
				.with(LIT, false)
				.with(POWERED_BY_MACHINERY, false)
				.with(TURNED_ON_BY_PLAYER, false));
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
			BlockHitResult hit) {
		if (!world.isClient) {
			if (state.get(POWERED_BY_MACHINERY)) {
				state = state.with(POWERED_BY_MACHINERY, false).with(TURNED_ON_BY_PLAYER, false);
			} else {
				state = state.cycle(TURNED_ON_BY_PLAYER);
			}
			world.setBlockState(pos, state, Block.NOTIFY_LISTENERS);
			checkForLit(state, world, pos);
		}
		return ActionResult.success(world.isClient);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos,
			boolean notify) {
		if (!world.isClient) {
			boolean isPowered = world.isReceivingRedstonePower(pos);
			if (isPowered != state.get(POWERED)) {
				state = state.with(POWERED, isPowered);
				world.setBlockState(pos, state, Block.NOTIFY_LISTENERS);
			}
			checkForLit(state, world, pos);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, net.minecraft.util.math.Direction direction,
			BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		if (!world.isClient()) {
			checkForLit(state, (World) world, pos);
		}
		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		super.onStateReplaced(state, world, pos, newState, moved);
		if (!world.isClient() && newState.getBlock() == BackroomsBlocks.REPAIRED_FLUORESCENT_LIGHT
				&& state.get(BooleanProperty.of("powered_by_machinery")) != newState
						.get(BooleanProperty.of("powered_by_machinery"))) {
			checkForLit(newState, (World) world, pos);
		}
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(LIT);
		builder.add(POWERED);
		builder.add(POWERED_BY_MACHINERY);
		builder.add(TURNED_ON_BY_PLAYER);
	}

	private boolean checkForLit(BlockState state, World world, BlockPos pos) {
		boolean lit = state.get(POWERED) || state.get(POWERED_BY_MACHINERY) || state.get(TURNED_ON_BY_PLAYER);
		world.setBlockState(pos, state.with(LIT, lit), Block.NOTIFY_LISTENERS);

		return lit;
	}
}
