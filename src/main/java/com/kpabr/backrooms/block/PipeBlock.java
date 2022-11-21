package com.kpabr.backrooms.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

import java.util.Map;

public class PipeBlock extends Block {
	public static final BooleanProperty NORTH;
	public static final BooleanProperty EAST;
	public static final BooleanProperty SOUTH;
	public static final BooleanProperty WEST;
	public static final BooleanProperty UP;
	public static final BooleanProperty DOWN;
	private static final Map<Direction, BooleanProperty> FACING_PROPERTIES;

	public PipeBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(NORTH, true).with(EAST, true).with(SOUTH, true).with(WEST, true).with(UP, true).with(DOWN, true));
	}

	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockView blockView = ctx.getWorld();
		BlockPos blockPos = ctx.getBlockPos();
		return this.getDefaultState().with(DOWN, blockView.getBlockState(blockPos.down()).isOf(this)).with(UP, blockView.getBlockState(blockPos.up()).isOf(this)).with(NORTH, blockView.getBlockState(blockPos.north()).isOf(this)).with(EAST, blockView.getBlockState(blockPos.east()).isOf(this)).with(SOUTH, blockView.getBlockState(blockPos.south()).isOf(this)).with(WEST, blockView.getBlockState(blockPos.west()).isOf(this));
	}

	@SuppressWarnings("deprecation")
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		return neighborState.isOf(this) ? state.with(FACING_PROPERTIES.get(direction), true) : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@SuppressWarnings("deprecation")
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING_PROPERTIES.get(rotation.rotate(Direction.NORTH)), state.get(NORTH)).with(FACING_PROPERTIES.get(rotation.rotate(Direction.SOUTH)), state.get(SOUTH)).with(FACING_PROPERTIES.get(rotation.rotate(Direction.EAST)), state.get(EAST)).with(FACING_PROPERTIES.get(rotation.rotate(Direction.WEST)), state.get(WEST)).with(FACING_PROPERTIES.get(rotation.rotate(Direction.UP)), state.get(UP)).with(FACING_PROPERTIES.get(rotation.rotate(Direction.DOWN)), state.get(DOWN));
	}

	@SuppressWarnings("deprecation")
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.with(FACING_PROPERTIES.get(mirror.apply(Direction.NORTH)), state.get(NORTH)).with(FACING_PROPERTIES.get(mirror.apply(Direction.SOUTH)), state.get(SOUTH)).with(FACING_PROPERTIES.get(mirror.apply(Direction.EAST)), state.get(EAST)).with(FACING_PROPERTIES.get(mirror.apply(Direction.WEST)), state.get(WEST)).with(FACING_PROPERTIES.get(mirror.apply(Direction.UP)), state.get(UP)).with(FACING_PROPERTIES.get(mirror.apply(Direction.DOWN)), state.get(DOWN));
	}

	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
	}

	static {
		NORTH = ConnectingBlock.NORTH;
		EAST = ConnectingBlock.EAST;
		SOUTH = ConnectingBlock.SOUTH;
		WEST = ConnectingBlock.WEST;
		UP = ConnectingBlock.UP;
		DOWN = ConnectingBlock.DOWN;
		FACING_PROPERTIES = ConnectingBlock.FACING_PROPERTIES;
	}
}
