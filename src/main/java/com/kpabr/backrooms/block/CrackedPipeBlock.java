package com.kpabr.backrooms.block;

import net.minecraft.block.*;

public class CrackedPipeBlock extends AbstractPipeBlock {
	public CrackedPipeBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(NORTH, true).with(EAST, true).with(SOUTH, true)
				.with(WEST, true).with(UP, true).with(DOWN, true).with(WATERLOGGED, false));
	}

	@Override
	protected boolean shouldConnect(BlockState blockState) {
		if (blockState.isOf(this)) {
			return true;
		}
		if (PipeBlock.class.isInstance(blockState.getBlock())) {
			return ((PipeBlock) blockState.getBlock()).crackedPipe == this;
		}
		return false;
	}
}