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

import java.util.Map;

public class CrackedCopperPipeBlock extends Block implements Waterloggable {
	public static final BooleanProperty NORTH = ConnectingBlock.NORTH;
	public static final BooleanProperty EAST = ConnectingBlock.EAST;
	public static final BooleanProperty SOUTH = ConnectingBlock.SOUTH;
	public static final BooleanProperty WEST = ConnectingBlock.WEST;
	public static final BooleanProperty UP = ConnectingBlock.UP;
	public static final BooleanProperty DOWN = ConnectingBlock.DOWN;
	private static final Map<Direction, BooleanProperty> FACING_PROPERTIES = ConnectingBlock.FACING_PROPERTIES;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

	public CrackedCopperPipeBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(NORTH, true).with(EAST, true).with(SOUTH, true)
				.with(WEST, true).with(UP, true).with(DOWN, true).with(WATERLOGGED, false));
	}

	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockView blockView = ctx.getWorld();
		BlockPos blockPos = ctx.getBlockPos();
		return this.getDefaultState()
				.with(DOWN, blockView.getBlockState(blockPos.down()).isOf(this))
				.with(UP, blockView.getBlockState(blockPos.up()).isOf(this))
				.with(NORTH, blockView.getBlockState(blockPos.north()).isOf(this))
				.with(EAST, blockView.getBlockState(blockPos.east()).isOf(this))
				.with(SOUTH, blockView.getBlockState(blockPos.south()).isOf(this))
				.with(WEST, blockView.getBlockState(blockPos.west()).isOf(this))
				.with(WATERLOGGED, blockView.getFluidState(blockPos).getFluid() == Fluids.WATER);
	}

	@SuppressWarnings("deprecation")
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
			WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		if (state.get(WATERLOGGED)) {
			world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}
		if (neighborState.isOf(this))
			return state.with(FACING_PROPERTIES.get(direction), true);
		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state
				.with(FACING_PROPERTIES.get(rotation.rotate(Direction.NORTH)), state.get(NORTH))
				.with(FACING_PROPERTIES.get(rotation.rotate(Direction.SOUTH)), state.get(SOUTH))
				.with(FACING_PROPERTIES.get(rotation.rotate(Direction.EAST)), state.get(EAST))
				.with(FACING_PROPERTIES.get(rotation.rotate(Direction.WEST)), state.get(WEST))
				.with(FACING_PROPERTIES.get(rotation.rotate(Direction.DOWN)), state.get(DOWN))
				.with(FACING_PROPERTIES.get(rotation.rotate(Direction.UP)), state.get(UP));
	}

	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state
				.with(FACING_PROPERTIES.get(mirror.apply(Direction.NORTH)), state.get(NORTH))
				.with(FACING_PROPERTIES.get(mirror.apply(Direction.SOUTH)), state.get(SOUTH))
				.with(FACING_PROPERTIES.get(mirror.apply(Direction.EAST)), state.get(EAST))
				.with(FACING_PROPERTIES.get(mirror.apply(Direction.WEST)), state.get(WEST))
				.with(FACING_PROPERTIES.get(mirror.apply(Direction.DOWN)), state.get(DOWN))
				.with(FACING_PROPERTIES.get(mirror.apply(Direction.UP)), state.get(UP));
	}

	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST, WATERLOGGED);
	}

	@SuppressWarnings("deprecation")
	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx) {
		VoxelShape finalShape = VoxelShapes.cuboid(0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
		if (state.get(NORTH))
			finalShape = VoxelShapes.combine(finalShape, VoxelShapes.cuboid(0.25f, 0.25f, 0.0f, 0.75f, 0.75f, 0.75f),
					BooleanBiFunction.OR);
		if (state.get(SOUTH))
			finalShape = VoxelShapes.combine(finalShape, VoxelShapes.cuboid(0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 1.0f),
					BooleanBiFunction.OR);
		if (state.get(EAST))
			finalShape = VoxelShapes.combine(finalShape, VoxelShapes.cuboid(0.25f, 0.25f, 0.25f, 1.0f, 0.75f, 0.75f),
					BooleanBiFunction.OR);
		if (state.get(WEST))
			finalShape = VoxelShapes.combine(finalShape, VoxelShapes.cuboid(0.0f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f),
					BooleanBiFunction.OR);
		if (state.get(DOWN))
			finalShape = VoxelShapes.combine(finalShape, VoxelShapes.cuboid(0.25f, 0.0f, 0.25f, 0.75f, 0.75f, 0.75f),
					BooleanBiFunction.OR);
		if (state.get(UP))
			finalShape = VoxelShapes.combine(finalShape, VoxelShapes.cuboid(0.25f, 0.25f, 0.25f, 0.75f, 1.0f, 0.75f),
					BooleanBiFunction.OR);

		return finalShape;
	}
}