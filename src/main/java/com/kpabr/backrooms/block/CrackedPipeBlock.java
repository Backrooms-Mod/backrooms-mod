package com.kpabr.backrooms.block;

import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import com.kpabr.backrooms.block.AbstractPipeBlock;
import com.kpabr.backrooms.block.PipeBlock;

import java.util.Map;

public class CrackedPipeBlock extends AbstractPipeBlock{
	public CrackedPipeBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(NORTH, true).with(EAST, true).with(SOUTH, true).with(WEST, true).with(UP, true).with(DOWN, true).with(WATERLOGGED, false));
	}

	@Override
	protected boolean shouldConnect(BlockState blockState){
		if(blockState.isOf(this)) { return true; }
		if(PipeBlock.class.isInstance(blockState.getBlock())){
			return ((PipeBlock)blockState.getBlock()).crackedPipe==this;
		}
		return false;
	}
}