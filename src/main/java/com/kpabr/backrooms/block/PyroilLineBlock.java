package com.kpabr.backrooms.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.kpabr.backrooms.block.entity.PyroilLineBlockEntity;
import com.kpabr.backrooms.init.BackroomsBlocks;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Random;

public class PyroilLineBlock extends BlockWithEntity {
    private static final EnumProperty<WireConnection> WIRE_CONNECTION_NORTH = Properties.NORTH_WIRE_CONNECTION;
    private static final EnumProperty<WireConnection> WIRE_CONNECTION_EAST = Properties.EAST_WIRE_CONNECTION;
    private static final EnumProperty<WireConnection> WIRE_CONNECTION_SOUTH = Properties.SOUTH_WIRE_CONNECTION;
    private static final EnumProperty<WireConnection> WIRE_CONNECTION_WEST = Properties.WEST_WIRE_CONNECTION;
    private static final Map<Direction, EnumProperty<WireConnection>> DIRECTION_TO_WIRE_CONNECTION_PROPERTY = Maps
            .newEnumMap(ImmutableMap.of(Direction.NORTH, WIRE_CONNECTION_NORTH, Direction.EAST, WIRE_CONNECTION_EAST,
                    Direction.SOUTH, WIRE_CONNECTION_SOUTH, Direction.WEST, WIRE_CONNECTION_WEST));
    private static final Map<BlockState, VoxelShape> SHAPES = Maps.newHashMap();

    // Idk what does that mean, TODO:
    private static final Map<Direction, VoxelShape> field_24414 = Maps.newEnumMap(ImmutableMap.of(
            Direction.NORTH, Block.createCuboidShape(3.0, 0.0, 0.0, 13.0, 1.0, 13.0),
            Direction.SOUTH, Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 16.0),
            Direction.EAST, Block.createCuboidShape(3.0, 0.0, 3.0, 16.0, 1.0, 13.0),
            Direction.WEST, Block.createCuboidShape(0.0, 0.0, 3.0, 13.0, 1.0, 13.0)));
    private static final Map<Direction, VoxelShape> field_24415 = Maps.newEnumMap(ImmutableMap.of(
            Direction.NORTH,
            VoxelShapes.union(field_24414.get(Direction.NORTH),
                    Block.createCuboidShape(3.0, 0.0, 0.0, 13.0, 16.0, 1.0)),
            Direction.SOUTH,
            VoxelShapes.union(field_24414.get(Direction.SOUTH),
                    Block.createCuboidShape(3.0, 0.0, 15.0, 13.0, 16.0, 16.0)),
            Direction.EAST,
            VoxelShapes.union(field_24414.get(Direction.EAST),
                    Block.createCuboidShape(15.0, 0.0, 3.0, 16.0, 16.0, 13.0)),
            Direction.WEST, VoxelShapes.union(field_24414.get(Direction.WEST),
                    Block.createCuboidShape(0.0, 0.0, 3.0, 1.0, 16.0, 13.0))));

    private static final Vec3d[] COLORS = Util.make(new Vec3d[16], (vec3ds) -> {
        for (int i = 0; i < 16; ++i) {
            float r = (float) i / 15.0F;
            float g = r * 0.6F + (r > 0.0F ? 0.4F : 0.3F);
            float h = MathHelper.clamp(r * r * 0.7F - 0.5F, 0.0F, 1.0F);
            float j = MathHelper.clamp(r * r * 0.6F - 0.7F, 0.0F, 1.0F);
            vec3ds[i] = new Vec3d(g, h, j);
        }
    });

    private final BlockState dotState;
    public static final VoxelShape DOT_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 13.0);

    public PyroilLineBlock(Settings settings) {
        super(settings);
        this.setDefaultState(stateManager.getDefaultState()
                .with(WIRE_CONNECTION_NORTH, WireConnection.NONE)
                .with(WIRE_CONNECTION_EAST, WireConnection.NONE)
                .with(WIRE_CONNECTION_SOUTH, WireConnection.NONE)
                .with(WIRE_CONNECTION_WEST, WireConnection.NONE));
        this.dotState = getDefaultState()
                .with(WIRE_CONNECTION_NORTH, WireConnection.SIDE)
                .with(WIRE_CONNECTION_EAST, WireConnection.SIDE)
                .with(WIRE_CONNECTION_SOUTH, WireConnection.SIDE)
                .with(WIRE_CONNECTION_WEST, WireConnection.SIDE);

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

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.isOf(state.getBlock()) && !world.isClient) {
            for (Direction direction : Direction.Type.VERTICAL) {
                world.updateNeighborsAlways(pos.offset(direction), this);
            }
            this.updateOffsetNeighbors(world, pos);
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES.get(state);
    }

    private void updateOffsetNeighbors(World world, BlockPos pos) {
        for (Direction direction : Direction.Type.HORIZONTAL) {
            this.updateNeighbors(world, pos.offset(direction));
        }

        for (Direction direction : Direction.Type.HORIZONTAL) {
            final BlockPos blockPos = pos.offset(direction);
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

            for (Direction direction : Direction.values()) {
                world.updateNeighborsAlways(pos.offset(direction), this);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!moved && !state.isOf(newState.getBlock())) {
            super.onStateReplaced(state, world, pos, newState, moved);
            if (!world.isClient) {
                for (Direction direction : Direction.values()) {
                    world.updateNeighborsAlways(pos.offset(direction), this);
                }
                this.updateOffsetNeighbors(world, pos);
            }
        }
    }

    @Override
    public void prepare(BlockState state, WorldAccess world, BlockPos pos, int flags, int maxUpdateDepth) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (Direction direction : Direction.Type.HORIZONTAL) {
            WireConnection wireConnection = state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction));
            if (wireConnection != WireConnection.NONE && !world.getBlockState(mutable.set(pos, direction)).isOf(this)) {
                mutable.move(Direction.DOWN);
                BlockState blockState = world.getBlockState(mutable);
                BlockPos blockPos = mutable.offset(direction.getOpposite());
                BlockState blockState2 = blockState.getStateForNeighborUpdate(direction.getOpposite(),
                        world.getBlockState(blockPos), world, mutable, blockPos);
                replace(blockState, blockState2, world, mutable, flags, maxUpdateDepth);

                mutable.set(pos, direction).move(Direction.UP);
                BlockState blockState3 = world.getBlockState(mutable);
                BlockPos blockPos2 = mutable.offset(direction.getOpposite());
                BlockState blockState4 = blockState3.getStateForNeighborUpdate(direction.getOpposite(),
                        world.getBlockState(blockPos2), world, mutable, blockPos2);
                replace(blockState3, blockState4, world, mutable, flags, maxUpdateDepth);
            }
        }

    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
            WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN) {
            return state;
        } else if (direction == Direction.UP) {
            return this.getPlacementState(world, state, pos);
        } else {
            final WireConnection wireConnection = this.getRenderConnectionType(world, pos, direction);
            return wireConnection.isConnected() == state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction))
                    .isConnected()
                    &&
                    !isFullyConnected(state)
                            ? state.with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), wireConnection)
                            : this.getPlacementState(world, this.dotState
                                    .with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), wireConnection), pos);
        }
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getPlacementState(ctx.getWorld(), this.dotState, ctx.getBlockPos());
    }

    private BlockState getPlacementState(BlockView world, BlockState state, BlockPos pos) {
        boolean notConnected = isNotConnected(state);
        state = this.getDefaultWireState(world, this.getDefaultState(), pos);
        if (!notConnected || !isNotConnected(state)) {
            boolean isNorthConnected = state.get(WIRE_CONNECTION_NORTH).isConnected();
            boolean isSouthConnected = state.get(WIRE_CONNECTION_SOUTH).isConnected();
            boolean isEastConnected = state.get(WIRE_CONNECTION_EAST).isConnected();
            boolean isWestConnected = state.get(WIRE_CONNECTION_WEST).isConnected();
            boolean areNorthSouthConnected = !isNorthConnected && !isSouthConnected;
            boolean areEastWestConnected = !isEastConnected && !isWestConnected;

            if (!isWestConnected && areNorthSouthConnected) {
                state = state.with(WIRE_CONNECTION_WEST, WireConnection.SIDE);
            }
            if (!isEastConnected && areNorthSouthConnected) {
                state = state.with(WIRE_CONNECTION_EAST, WireConnection.SIDE);
            }
            if (!isNorthConnected && areEastWestConnected) {
                state = state.with(WIRE_CONNECTION_NORTH, WireConnection.SIDE);
            }
            if (!isSouthConnected && areEastWestConnected) {
                state = state.with(WIRE_CONNECTION_SOUTH, WireConnection.SIDE);
            }

        }
        return state;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit) {
        if (player.getAbilities().allowModifyWorld) {
            if (isFullyConnected(state) || isNotConnected(state)) {
                BlockState blockState = isFullyConnected(state) ? this.getDefaultState() : this.dotState;
                blockState = this.getPlacementState(world, blockState, pos);
                if (blockState != state) {
                    world.setBlockState(pos, blockState, 3);
                    this.updateForNewState(world, pos, state, blockState);
                    return ActionResult.SUCCESS;
                }
            }
        }
        return ActionResult.PASS;
    }

    private void updateForNewState(World world, BlockPos pos, BlockState oldState, BlockState newState) {
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos blockPos = pos.offset(direction);
            if (oldState.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction)).isConnected() != newState
                    .get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction)).isConnected()
                    && world.getBlockState(blockPos).isSolidBlock(world, blockPos)) {
                world.updateNeighborsExcept(blockPos, newState.getBlock(), direction.getOpposite());
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        if (mirror == BlockMirror.LEFT_RIGHT)
            return state
                    .with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_SOUTH))
                    .with(WIRE_CONNECTION_SOUTH, state.get(WIRE_CONNECTION_NORTH));
        else if (mirror == BlockMirror.FRONT_BACK)
            return state
                    .with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_WEST))
                    .with(WIRE_CONNECTION_WEST, state.get(WIRE_CONNECTION_EAST));

        return super.mirror(state, mirror);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return switch (rotation) {
            case CLOCKWISE_180 -> state
                    .with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_SOUTH))
                    .with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_WEST))
                    .with(WIRE_CONNECTION_SOUTH, state.get(WIRE_CONNECTION_NORTH))
                    .with(WIRE_CONNECTION_WEST, state.get(WIRE_CONNECTION_EAST));
            case COUNTERCLOCKWISE_90 -> state
                    .with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_EAST))
                    .with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_SOUTH))
                    .with(WIRE_CONNECTION_SOUTH, state.get(WIRE_CONNECTION_WEST))
                    .with(WIRE_CONNECTION_WEST, state.get(WIRE_CONNECTION_NORTH));
            case CLOCKWISE_90 -> state
                    .with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_WEST))
                    .with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_NORTH))
                    .with(WIRE_CONNECTION_SOUTH, state.get(WIRE_CONNECTION_EAST))
                    .with(WIRE_CONNECTION_WEST, state.get(WIRE_CONNECTION_SOUTH));
            default -> state;
        };
    }

    private static boolean isFullyConnected(BlockState state) {
        return state.get(WIRE_CONNECTION_NORTH).isConnected() &&
                state.get(WIRE_CONNECTION_SOUTH).isConnected() &&
                state.get(WIRE_CONNECTION_EAST).isConnected() &&
                state.get(WIRE_CONNECTION_WEST).isConnected();
    }

    private static boolean isNotConnected(BlockState state) {
        return !state.get(WIRE_CONNECTION_NORTH).isConnected() &&
                !state.get(WIRE_CONNECTION_SOUTH).isConnected() &&
                !state.get(WIRE_CONNECTION_EAST).isConnected() &&
                !state.get(WIRE_CONNECTION_WEST).isConnected();
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos,
            boolean notify) {
        if (!world.isClient && !state.canPlaceAt(world, pos)) {
            dropStacks(state, world, pos);
            world.removeBlock(pos, false);
        }
    }

    private WireConnection getRenderConnectionType(BlockView world, BlockPos pos, Direction direction) {
        return this.getRenderConnectionType(world, pos, direction,
                !world.getBlockState(pos.up()).isSolidBlock(world, pos));
    }

    private WireConnection getRenderConnectionType(BlockView world, BlockPos pos, Direction direction, boolean bl) {
        BlockPos blockPos = pos.offset(direction);
        BlockState blockState = world.getBlockState(blockPos);
        if (bl) {
            boolean onTop = this.canRunOnTop(world, blockPos, blockState);
            if (onTop && connectsTo(world.getBlockState(blockPos.up()))) {
                if (blockState.isSideSolidFullSquare(world, blockPos, direction.getOpposite())) {
                    return WireConnection.UP;
                }
                return WireConnection.SIDE;
            }
        }
        return !connectsTo(blockState, direction)
                && (blockState.isSolidBlock(world, blockPos) || !connectsTo(world.getBlockState(blockPos.down())))
                        ? WireConnection.NONE
                        : WireConnection.SIDE;
    }

    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        BlockState blockState = world.getBlockState(blockPos);
        return this.canRunOnTop(world, blockPos, blockState);
    }

    private BlockState getDefaultWireState(BlockView world, BlockState state, BlockPos pos) {
        final boolean noBlocksOnTop = !world.getBlockState(pos.up()).isSolidBlock(world, pos);

        for (Direction direction : Direction.Type.HORIZONTAL) {
            if (!state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction)).isConnected()) {
                WireConnection wireConnection = this.getRenderConnectionType(world, pos, direction, noBlocksOnTop);
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
        return state.isOf(BackroomsBlocks.PYROIL)
                || dir != null && state.emitsRedstonePower();
    }

    private void addPoweredParticles(World world, Random random, BlockPos pos, Vec3d color, Direction direction,
            Direction direction2, float f, float g) {
        float h = g - f;
        if (!(random.nextFloat() >= 0.1F * h)) {
            float j = f + h * random.nextFloat();
            double d = 0.5 + (double) (0.4375F * (float) direction.getOffsetX())
                    + (double) (j * (float) direction2.getOffsetX());
            double e = 0.5 + (double) (0.4375F * (float) direction.getOffsetY())
                    + (double) (j * (float) direction2.getOffsetY());
            double k = 0.5 + (double) (0.4375F * (float) direction.getOffsetZ())
                    + (double) (j * (float) direction2.getOffsetZ());
            world.addParticle(ParticleTypes.FLAME, (double) pos.getX() + d, (double) pos.getY() + e,
                    (double) pos.getZ() + k, 0.0, 0.0, 0.0);
        }
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {

        for (Direction direction : Direction.Type.HORIZONTAL) {
            WireConnection wireConnection = state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction));
            switch (wireConnection) {
                case UP:
                    this.addPoweredParticles(world, random, pos, COLORS[random.nextInt(2, 16)], direction, Direction.UP,
                            -0.5F, 0.5F);
                case SIDE:
                    this.addPoweredParticles(world, random, pos, COLORS[random.nextInt(2, 16)], Direction.DOWN,
                            direction, 0.0F, 0.5F);
                    break;
                case NONE:
                    this.addPoweredParticles(world, random, pos, COLORS[random.nextInt(2, 16)], Direction.DOWN,
                            direction, 0.0F, 0.3F);
            }
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
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state,
            BlockEntityType<T> type) {
        return checkType(type, BackroomsBlocks.PYROIL_LINE_BLOCK_ENTITY, PyroilLineBlockEntity::tick);
    }
}