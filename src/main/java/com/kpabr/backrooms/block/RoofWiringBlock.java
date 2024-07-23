package com.kpabr.backrooms.block;

import net.minecraft.block.*;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Type;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import java.util.Map;

public class RoofWiringBlock extends Block implements Waterloggable {
	public static final BooleanProperty NORTH = ConnectingBlock.NORTH;
	public static final BooleanProperty EAST = ConnectingBlock.EAST;
	public static final BooleanProperty SOUTH = ConnectingBlock.SOUTH;
	public static final BooleanProperty WEST = ConnectingBlock.WEST;
	private static final Map<Direction, BooleanProperty> FACING_PROPERTIES = ConnectingBlock.FACING_PROPERTIES;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

	public RoofWiringBlock(Settings settings) {
		super(settings);
		this.setDefaultState(
				(BlockState) ((BlockState) ((BlockState) ((BlockState) ((BlockState) ((BlockState) this.stateManager
						.getDefaultState()).with(NORTH, false)).with(EAST, false)).with(SOUTH, false))
						.with(WEST, false)).with(WATERLOGGED, false));
	}

	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return true;
	}

	public boolean canConnect(BlockState state, boolean neighborIsFullSquare, Direction dir) {
		Block block = state.getBlock();
		boolean bl = block instanceof RoofWiringBlock;
		return bl;
	}

	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		BlockPos blockPos = pos.up();
		BlockState blockState = world.getBlockState(blockPos);
		return blockState.isSideSolidFullSquare(world, blockPos, Direction.DOWN);
	}

	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockView blockView = ctx.getWorld();
		BlockPos blockPos = ctx.getBlockPos();
		FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
		BlockPos blockPos2 = blockPos.north();
		BlockPos blockPos3 = blockPos.east();
		BlockPos blockPos4 = blockPos.south();
		BlockPos blockPos5 = blockPos.west();
		BlockState blockState = blockView.getBlockState(blockPos2);
		BlockState blockState2 = blockView.getBlockState(blockPos3);
		BlockState blockState3 = blockView.getBlockState(blockPos4);
		BlockState blockState4 = blockView.getBlockState(blockPos5);
		return (BlockState) ((BlockState) ((BlockState) ((BlockState) ((BlockState) super.getPlacementState(ctx).with(
				NORTH,
				this.canConnect(blockState, blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.SOUTH),
						Direction.SOUTH)))
				.with(EAST, this.canConnect(blockState2,
						blockState2.isSideSolidFullSquare(blockView, blockPos3, Direction.WEST), Direction.WEST)))
				.with(SOUTH, this.canConnect(blockState3,
						blockState3.isSideSolidFullSquare(blockView, blockPos4, Direction.NORTH), Direction.NORTH)))
				.with(WEST, this.canConnect(blockState4,
						blockState4.isSideSolidFullSquare(blockView, blockPos5, Direction.EAST), Direction.EAST)))
				.with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
	}

	@SuppressWarnings("deprecation")
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
			WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		if (direction == Direction.UP && !this.canPlaceAt(state, world, pos)) {
			return Blocks.AIR.getDefaultState();
		}

		if ((Boolean) state.get(WATERLOGGED)) {
			world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return direction.getAxis().getType() == Type.HORIZONTAL
				? (BlockState) state
						.with(FACING_PROPERTIES.get(direction),
								this.canConnect(neighborState,
										neighborState.isSideSolidFullSquare(world, neighborPos,
												direction.getOpposite()),
										direction.getOpposite()))
				: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	public BlockState rotate(BlockState state, BlockRotation rotation) {
		switch (rotation) {
			case CLOCKWISE_180:
				return (BlockState) ((BlockState) ((BlockState) ((BlockState) state.with(NORTH,
						(Boolean) state.get(SOUTH))).with(EAST, (Boolean) state.get(WEST)))
						.with(SOUTH, (Boolean) state.get(NORTH))).with(WEST, (Boolean) state.get(EAST));
			case COUNTERCLOCKWISE_90:
				return (BlockState) ((BlockState) ((BlockState) ((BlockState) state.with(NORTH,
						(Boolean) state.get(EAST))).with(EAST, (Boolean) state.get(SOUTH)))
						.with(SOUTH, (Boolean) state.get(WEST))).with(WEST, (Boolean) state.get(NORTH));
			case CLOCKWISE_90:
				return (BlockState) ((BlockState) ((BlockState) ((BlockState) state.with(NORTH,
						(Boolean) state.get(WEST))).with(EAST, (Boolean) state.get(NORTH)))
						.with(SOUTH, (Boolean) state.get(EAST))).with(WEST, (Boolean) state.get(SOUTH));
			default:
				return state;
		}
	}

	@SuppressWarnings("deprecation")
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		switch (mirror) {
			case LEFT_RIGHT:
				return (BlockState) ((BlockState) state.with(NORTH, (Boolean) state.get(SOUTH))).with(SOUTH,
						(Boolean) state.get(NORTH));
			case FRONT_BACK:
				return (BlockState) ((BlockState) state.with(EAST, (Boolean) state.get(WEST))).with(WEST,
						(Boolean) state.get(EAST));
			default:
				return super.mirror(state, mirror);
		}
	}

	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return Block.createCuboidShape(0, 14, 0, 16, 16.0, 16);
	}

	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(new Property[] { NORTH, EAST, WEST, SOUTH, WATERLOGGED });
	}
}