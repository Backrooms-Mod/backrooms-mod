package com.kpabr.backrooms.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import com.kpabr.backrooms.init.BackroomsBlocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.explosion.Explosion;

public class MachineryBlock extends Block {
	public static final BooleanProperty POWERED = Properties.POWERED;

	public MachineryBlock(Settings settings) {
		super(settings);
		setDefaultState(this.stateManager.getDefaultState().with(POWERED, false));
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
			BlockHitResult hit) {
		state = state.cycle(POWERED);
		world.setBlockState(pos, state, 1, 0);

		if (state.get(POWERED)) {
			for (int i = -14; i <= 14; i++) {
				for (int j = 0; j <= 4; j++) {
					for (int k = -14; k <= 14; k++) {
						BlockPos lightPos = pos.add(i, j, k);
						if (world.getBlockState(lightPos).getBlock() == BackroomsBlocks.REPAIRED_FLUORESCENT_LIGHT) {
							world.setBlockState(lightPos, world.getBlockState(lightPos)
									.with(BooleanProperty.of("powered_by_machinery"), true));
						}
					}
				}
			}
		} else {
			for (int i = -14; i <= 14; i++) {
				for (int j = 0; j <= 4; j++) {
					for (int k = -14; k <= 14; k++) {
						BlockPos lightPos = pos.add(i, j, k);
						if (world.getBlockState(lightPos).getBlock() == BackroomsBlocks.REPAIRED_FLUORESCENT_LIGHT) {
							world.setBlockState(lightPos, world.getBlockState(lightPos)
									.with(BooleanProperty.of("powered_by_machinery"), false));
						}
					}
				}
			}
		}
		return ActionResult.success(world.isClient);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
		stateManager.add(POWERED);
	}

	@Override
	public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
		super.onBroken(world, pos, state);
		for (int i = -14; i <= 14; i++) {
			for (int j = 0; j <= 4; j++) {
				for (int k = -14; k <= 14; k++) {
					BlockPos lightPos = pos.add(i, j, k);
					if (world.getBlockState(lightPos).getBlock() == BackroomsBlocks.REPAIRED_FLUORESCENT_LIGHT) {
						world.setBlockState(lightPos,
								world.getBlockState(lightPos).with(BooleanProperty.of("powered_by_machinery"), false),
								0);
					}
				}
			}
		}
	}

	@Override
	public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
		super.onDestroyedByExplosion(world, pos, explosion);
		for (int i = -14; i <= 14; i++) {
			for (int j = 0; j <= 4; j++) {
				for (int k = -14; k <= 14; k++) {
					BlockPos lightPos = pos.add(i, j, k);
					if (world.getBlockState(lightPos).getBlock() == BackroomsBlocks.REPAIRED_FLUORESCENT_LIGHT) {
						world.setBlockState(lightPos,
								world.getBlockState(lightPos).with(BooleanProperty.of("powered_by_machinery"), false),
								0);
					}
				}
			}
		}
	}
}
