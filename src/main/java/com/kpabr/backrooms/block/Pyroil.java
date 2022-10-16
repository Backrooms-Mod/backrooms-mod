package com.kpabr.backrooms.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.block.entity.PyroilLineBlockEntity;
import com.kpabr.backrooms.init.BackroomsBlocks;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class Pyroil extends BlockWithEntity implements BlockEntityProvider {
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_NORTH = Properties.NORTH_WIRE_CONNECTION;
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_EAST = Properties.EAST_WIRE_CONNECTION;
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_SOUTH = Properties.SOUTH_WIRE_CONNECTION;
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_WEST = Properties.WEST_WIRE_CONNECTION;
    public static final Map<Direction, EnumProperty<WireConnection>> DIRECTION_TO_WIRE_CONNECTION_PROPERTY = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, WIRE_CONNECTION_NORTH, Direction.EAST, WIRE_CONNECTION_EAST, Direction.SOUTH, WIRE_CONNECTION_SOUTH, Direction.WEST, WIRE_CONNECTION_WEST));;
    public static final Map<BlockState, VoxelShape> SHAPES = Maps.newHashMap();

    public static final Map<Direction, VoxelShape> field_24414 = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.createCuboidShape(3.0, 0.0, 0.0, 13.0, 1.0, 13.0), Direction.SOUTH, Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 16.0), Direction.EAST, Block.createCuboidShape(3.0, 0.0, 3.0, 16.0, 1.0, 13.0), Direction.WEST, Block.createCuboidShape(0.0, 0.0, 3.0, 13.0, 1.0, 13.0)));
    public static final Map<Direction, VoxelShape> field_24415 = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, VoxelShapes.union(field_24414.get(Direction.NORTH), Block.createCuboidShape(3.0, 0.0, 0.0, 13.0, 16.0, 1.0)), Direction.SOUTH, VoxelShapes.union(field_24414.get(Direction.SOUTH), Block.createCuboidShape(3.0, 0.0, 15.0, 13.0, 16.0, 16.0)), Direction.EAST, VoxelShapes.union(field_24414.get(Direction.EAST), Block.createCuboidShape(15.0, 0.0, 3.0, 16.0, 16.0, 13.0)), Direction.WEST, VoxelShapes.union(field_24414.get(Direction.WEST), Block.createCuboidShape(0.0, 0.0, 3.0, 1.0, 16.0, 13.0))));

    private final BlockState dotState;

    public static final VoxelShape DOT_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 13.0);
    public Pyroil(Settings settings) {
        super(settings);
        this.setDefaultState(stateManager.getDefaultState().with(WIRE_CONNECTION_NORTH, WireConnection.NONE).with(WIRE_CONNECTION_EAST, WireConnection.NONE).with(WIRE_CONNECTION_SOUTH, WireConnection.NONE).with(WIRE_CONNECTION_WEST, WireConnection.NONE));
        this.dotState = (getDefaultState().with(WIRE_CONNECTION_NORTH, WireConnection.SIDE)).with(WIRE_CONNECTION_EAST, WireConnection.SIDE).with(WIRE_CONNECTION_SOUTH, WireConnection.SIDE).with(WIRE_CONNECTION_WEST, WireConnection.SIDE);

        for (BlockState blockState : this.getStateManager().getStates()) {
            SHAPES.put(blockState, this.getShapeForState(blockState));
        }
    }

    private VoxelShape getShapeForState(BlockState state) {
        VoxelShape voxelShape = DOT_SHAPE;

        for (Direction direction : Direction.Type.HORIZONTAL) {
            WireConnection wireConnection = state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction));
            if (wireConnection == WireConnection.SIDE) {
                voxelShape = VoxelShapes.union(voxelShape, field_24414.get(direction));
            } else if (wireConnection == WireConnection.UP) {
                voxelShape = VoxelShapes.union(voxelShape, field_24415.get(direction));
            }
        }
        return voxelShape;
    }

    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.isOf(state.getBlock()) && !world.isClient) {

            for (Direction direction : Direction.Type.VERTICAL) {
                world.updateNeighborsAlways(pos.offset(direction), this);
            }

            this.updateOffsetNeighbors(world, pos);
        }
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES.get(state);
    }

    private void updateOffsetNeighbors(World world, BlockPos pos) {
        Iterator<Direction> var3 = Direction.Type.HORIZONTAL.iterator();

        Direction direction;
        while(var3.hasNext()) {
            direction = (Direction)var3.next();
            this.updateNeighbors(world, pos.offset(direction));
        }

        var3 = Direction.Type.HORIZONTAL.iterator();

        while(var3.hasNext()) {
            direction = (Direction)var3.next();
            BlockPos blockPos = pos.offset(direction);
            if (world.getBlockState(blockPos).isSolidBlock(world, blockPos)) {
                this.updateNeighbors(world, blockPos.up());
            } else {
                this.updateNeighbors(world, blockPos.down());
            }
        }

    }


    private void updateNeighbors(World world, BlockPos pos) {
        if (world.getBlockState(pos).isOf(this)) {
            world.updateNeighborsAlways(pos, this);
            Direction[] var3 = Direction.values();

            for (Direction direction : var3) {
                world.updateNeighborsAlways(pos.offset(direction), this);
            }

        }
    }

    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!moved && !state.isOf(newState.getBlock())) {
            super.onStateReplaced(state, world, pos, newState, moved);
            if (!world.isClient) {
                Direction[] var6 = Direction.values();
                int var7 = var6.length;

                for (Direction direction : var6) {
                    world.updateNeighborsAlways(pos.offset(direction), this);
                }

                this.updateOffsetNeighbors(world, pos);
            }
        }
    }

    public void prepare(BlockState state, WorldAccess world, BlockPos pos, int flags, int maxUpdateDepth) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (Direction direction : Direction.Type.HORIZONTAL) {
            WireConnection wireConnection = state.get( DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction));
            if (wireConnection != WireConnection.NONE && !world.getBlockState(mutable.set(pos, direction)).isOf(this)) {
                mutable.move(Direction.DOWN);
                BlockState blockState = world.getBlockState(mutable);
                BlockPos blockPos = mutable.offset(direction.getOpposite());
                BlockState blockState2 = blockState.getStateForNeighborUpdate(direction.getOpposite(), world.getBlockState(blockPos), world, mutable, blockPos);
                replace(blockState, blockState2, world, mutable, flags, maxUpdateDepth);

                mutable.set(pos, direction).move(Direction.UP);
                BlockState blockState3 = world.getBlockState(mutable);
                BlockPos blockPos2 = mutable.offset(direction.getOpposite());
                BlockState blockState4 = blockState3.getStateForNeighborUpdate(direction.getOpposite(), world.getBlockState(blockPos2), world, mutable, blockPos2);
                replace(blockState3, blockState4, world, mutable, flags, maxUpdateDepth);
            }
        }

    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN) {
            return state;
        } else if (direction == Direction.UP) {
            return this.getPlacementState(world, state, pos);
        } else {
            WireConnection wireConnection = this.getRenderConnectionType(world, pos, direction);
            return wireConnection.isConnected() == state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction)).isConnected() && !isFullyConnected(state) ? state.with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), wireConnection) : this.getPlacementState(world, this.dotState.with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), wireConnection), pos);
        }
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getPlacementState(ctx.getWorld(), this.dotState, ctx.getBlockPos());
    }

    private BlockState getPlacementState(BlockView world, BlockState state, BlockPos pos) {
        boolean bl = isNotConnected(state);
        state = this.getDefaultWireState(world, this.getDefaultState(), pos);
        if (!bl || !isNotConnected(state)) {
            boolean bl2 = state.get(WIRE_CONNECTION_NORTH).isConnected();
            boolean bl3 = state.get(WIRE_CONNECTION_SOUTH).isConnected();
            boolean bl4 = state.get(WIRE_CONNECTION_EAST).isConnected();
            boolean bl5 = state.get(WIRE_CONNECTION_WEST).isConnected();
            boolean bl6 = !bl2 && !bl3;
            boolean bl7 = !bl4 && !bl5;
            if (!bl5 && bl6) {
                state = state.with(WIRE_CONNECTION_WEST, WireConnection.SIDE);
            }

            if (!bl4 && bl6) {
                state = state.with(WIRE_CONNECTION_EAST, WireConnection.SIDE);
            }

            if (!bl2 && bl7) {
                state = state.with(WIRE_CONNECTION_NORTH, WireConnection.SIDE);
            }

            if (!bl3 && bl7) {
                state = state.with(WIRE_CONNECTION_SOUTH, WireConnection.SIDE);
            }

        }
        return state;
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (player.getAbilities().allowModifyWorld) {
            if (isFullyConnected(state) || isNotConnected(state)) {
                BackroomsMod.LOGGER.info("standing here...");
                BlockState blockState = isFullyConnected(state) ? this.getDefaultState() : this.dotState;
                blockState = this.getPlacementState(world, blockState, pos);
                if (blockState != state) {
                    world.setBlockState(pos, blockState, 3);
                    BackroomsMod.LOGGER.info("i realized");
                    this.updateForNewState(world, pos, state, blockState);
                    BackroomsMod.LOGGER.info("that you was just like meee");
                    return ActionResult.SUCCESS;
                }
            }
        }

        return ActionResult.PASS;
    }

    private void updateForNewState(World world, BlockPos pos, BlockState oldState, BlockState newState) {
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos blockPos = pos.offset(direction);
            if (oldState.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction)).isConnected() != newState.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction)).isConnected() && world.getBlockState(blockPos).isSolidBlock(world, blockPos)) {
                world.updateNeighborsExcept(blockPos, newState.getBlock(), direction.getOpposite());
            }
        }
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        switch (mirror) {
            case LEFT_RIGHT:
                return state.with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_SOUTH)).with(WIRE_CONNECTION_SOUTH, state.get(WIRE_CONNECTION_NORTH));
            case FRONT_BACK:
                return state.with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_WEST)).with(WIRE_CONNECTION_WEST, state.get(WIRE_CONNECTION_EAST));
            default:
                return super.mirror(state, mirror);
        }
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        switch (rotation) {
            case CLOCKWISE_180:
                return state.with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_SOUTH)).with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_WEST)).with(WIRE_CONNECTION_SOUTH, state.get(WIRE_CONNECTION_NORTH)).with(WIRE_CONNECTION_WEST, state.get(WIRE_CONNECTION_EAST));
            case COUNTERCLOCKWISE_90:
                return state.with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_EAST)).with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_SOUTH)).with(WIRE_CONNECTION_SOUTH, state.get(WIRE_CONNECTION_WEST)).with(WIRE_CONNECTION_WEST, state.get(WIRE_CONNECTION_NORTH));
            case CLOCKWISE_90:
                return (state.with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_WEST))).with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_NORTH)).with(WIRE_CONNECTION_SOUTH, state.get(WIRE_CONNECTION_EAST)).with(WIRE_CONNECTION_WEST, state.get(WIRE_CONNECTION_SOUTH));
            default:
                return state;
        }
    }

    private static boolean isFullyConnected(BlockState state) {
        return state.get(WIRE_CONNECTION_NORTH).isConnected() && state.get(WIRE_CONNECTION_SOUTH).isConnected() && state.get(WIRE_CONNECTION_EAST).isConnected() && state.get(WIRE_CONNECTION_WEST).isConnected();
    }

    private static boolean isNotConnected(BlockState state) {
        return !state.get(WIRE_CONNECTION_NORTH).isConnected() && !state.get(WIRE_CONNECTION_SOUTH).isConnected() && !state.get(WIRE_CONNECTION_EAST).isConnected() && !state.get(WIRE_CONNECTION_WEST).isConnected();
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if (!world.isClient) {
            if (state.canPlaceAt(world, pos)) {
            } else {
                dropStacks(state, world, pos);
                world.removeBlock(pos, false);
            }

        }
    }


    private WireConnection getRenderConnectionType(BlockView world, BlockPos pos, Direction direction) {
        return this.getRenderConnectionType(world, pos, direction, !world.getBlockState(pos.up()).isSolidBlock(world, pos));
    }

    private WireConnection getRenderConnectionType(BlockView world, BlockPos pos, Direction direction, boolean bl) {
        BackroomsMod.LOGGER.info("Uh oh");
        BlockPos blockPos = pos.offset(direction);
        BlockState blockState = world.getBlockState(blockPos);
        if (bl) {
            BackroomsMod.LOGGER.info("Uh oh 1");
            boolean bl2 = this.canRunOnTop(world, blockPos, blockState);
            if (bl2 && connectsTo(world.getBlockState(blockPos.up()))) {
                if (blockState.isSideSolidFullSquare(world, blockPos, direction.getOpposite())) {
                    BackroomsMod.LOGGER.info("Uh oh 2");
                    return WireConnection.UP;
                }
                BackroomsMod.LOGGER.info("Uh oh 3");
                return WireConnection.SIDE;
            }
        }

        return !connectsTo(blockState, direction) && (blockState.isSolidBlock(world, blockPos) || !connectsTo(world.getBlockState(blockPos.down()))) ? WireConnection.NONE : WireConnection.SIDE;
    }

    private BlockState getDefaultWireState(BlockView world, BlockState state, BlockPos pos) {
        boolean bl = !world.getBlockState(pos.up()).isSolidBlock(world, pos);

        for (Direction direction : Direction.Type.HORIZONTAL) {
            if (!state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction)).isConnected()) {
                WireConnection wireConnection = this.getRenderConnectionType(world, pos, direction, bl);
                state = state.with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), wireConnection);
            }
        }

        return state;
    }
    private boolean canRunOnTop(BlockView world, BlockPos pos, BlockState floor) {
        return floor.isSideSolidFullSquare(world, pos, Direction.UP) || floor.isOf(Blocks.HOPPER);
    }

    protected static boolean connectsTo(BlockState state) {
        return connectsTo(state, null);
    }

    protected static boolean connectsTo(BlockState state, @Nullable Direction dir) {
        if (state.isOf(BackroomsBlocks.PYROIL)) {
            return true;
        } else {
            return state.emitsRedstonePower() && dir != null;
        }
    }



    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PyroilLineBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WIRE_CONNECTION_NORTH, WIRE_CONNECTION_EAST, WIRE_CONNECTION_SOUTH, WIRE_CONNECTION_WEST);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, BackroomsBlocks.PYROIL_LINE_BLOCK_ENTITY, PyroilLineBlockEntity::tick);
    }
}