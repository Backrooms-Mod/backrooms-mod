package com.kpabr.backrooms.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;

import com.kpabr.backrooms.block.entity.MaskBlockEntity;
import com.kpabr.backrooms.init.BackroomsBlocks;
import com.kpabr.backrooms.init.BackroomsItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Wearable;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import javax.annotation.Nullable;

public class MaskBlock extends BlockWithEntity implements Wearable {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    private static final Map<Direction, VoxelShape> HARLEQUIN_OUTLINE_SHAPE = Maps.newEnumMap(ImmutableMap.of(
            Direction.NORTH, Block.createCuboidShape(4.0, 3.0, 14.0, 12.0, 13.0, 16.0),
            Direction.SOUTH, Block.createCuboidShape(4.0, 3.0, 0.0, 12.0, 13.0, 2.0),
            Direction.EAST, Block.createCuboidShape(0.0, 3.0, 4.0, 2.0, 13.0, 12.0),
            Direction.WEST, Block.createCuboidShape(14.0, 3.0, 4.0, 16.0, 13.0, 12.0)
    ));
    private static final Map<Direction, VoxelShape> COLOMBINA_OUTLINE_SHAPE = Maps.newEnumMap(ImmutableMap.of(
            Direction.NORTH, Block.createCuboidShape(3.0, 5.0, 15.0, 13.0, 10.0, 16.0),
            Direction.SOUTH, Block.createCuboidShape(3.0, 5.0, 0.0, 13.0, 10.0, 1.0),
            Direction.EAST, Block.createCuboidShape(0.0, 5.0, 10.0, 1.0, 10.0, 13.0),
            Direction.WEST, Block.createCuboidShape(15.0, 5.0, 3.0, 16.0, 10.0, 13.0)
    ));
    private static final Map<Direction, VoxelShape> SOCK_BUSKIN_OUTLINE_SHAPE = Maps.newEnumMap(ImmutableMap.of(
            Direction.NORTH, VoxelShapes.union(
                    Block.createCuboidShape(3.0, 0.0, 13.0, 13.0, 7.0, 16.0),
                    Block.createCuboidShape(2.0, 7.0, 13.0, 14.0, 16.0, 16.0)
            ),
            Direction.SOUTH, VoxelShapes.union(
                    Block.createCuboidShape(3.0, 0.0, 0.0, 13.0, 7.0, 3.0),
                    Block.createCuboidShape(2.0, 7.0, 0.0, 12.0, 16.0, 3.0)
            ),
            Direction.EAST, VoxelShapes.union(
                    Block.createCuboidShape(0.0, 0.0, 3.0, 3.0, 7.0, 13.0),
                    Block.createCuboidShape(0.0, 7.0, 2.0, 3.0, 16.0, 14.0)
            ),
            Direction.WEST, VoxelShapes.union(
                    Block.createCuboidShape(13.0, 0.0, 3.0, 16.0, 7.0, 13.0),
                    Block.createCuboidShape(13.0, 7.0, 2.0, 16.0, 16.0, 14.0)
            )
    ));
    private final MaskType maskType;

    public MaskBlock(MaskType maskType, Block.Settings settings) {
        super(settings);
        this.maskType = maskType;
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
        player.incrementStat(Stats.MINED.getOrCreateStat(this));
        player.addExhaustion(0.005F);
        dropStack(world, pos, switch (maskType) {
            case COLOMBINA -> new ItemStack(BackroomsItems.COLOMBINA_MASK);
            case HARLEQUIN -> new ItemStack(BackroomsItems.HARLEQUIN_MASK);
            case SOCK_BUSKIN -> new ItemStack(BackroomsItems.SOCK_BUSKIN_MASK);
        });
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch(maskType) {
            case COLOMBINA -> COLOMBINA_OUTLINE_SHAPE.get(state.get(FACING));
            case HARLEQUIN -> HARLEQUIN_OUTLINE_SHAPE.get(state.get(FACING));
            case SOCK_BUSKIN -> SOCK_BUSKIN_OUTLINE_SHAPE.get(state.get(FACING));
        };
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        final Direction[] directions = ctx.getPlacementDirections();
        final World world = ctx.getWorld();
        final BlockPos pos = ctx.getBlockPos();
        BlockState newBlockState = this.getDefaultState();

        for (Direction direction : directions) {
            if (direction.getAxis().isHorizontal()) {
                final Direction oppositeDirection = direction.getOpposite();
                newBlockState = newBlockState.with(FACING, oppositeDirection);
                if (!world.getBlockState(pos.offset(direction)).canReplace(ctx)) {
                    return newBlockState;
                }
            }
        }
        return null;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MaskBlockEntity(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return !world.isClient ? null : checkType(type, BackroomsBlocks.MASK, MaskBlockEntity::tick);
    }
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public MaskType getMaskType() {
        return this.maskType;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return state.isSolidBlock(world, pos);
    }

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    public enum MaskType {
        COLOMBINA("colombina_mask"),
        HARLEQUIN("harlequin_mask"),
        SOCK_BUSKIN("sock_buskin_mask");

        public final String maskKey;

        MaskType(String maskKey) {
            this.maskKey = maskKey;
        }
    }
}
