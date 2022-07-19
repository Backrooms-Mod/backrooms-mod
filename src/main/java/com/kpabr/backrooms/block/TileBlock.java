package com.kpabr.backrooms.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class TileBlock extends Block {

    public static final DirectionProperty FACING = Properties.FACING;

    public TileBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.UP));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        BlockState hitState = world.getBlockState(pos.mutableCopy().move(ctx.getSide().getOpposite()));
        Direction hit = ctx.getSide();
        Direction look = ctx.getPlayerLookDirection();

        if (hitState.isOf(this)) {
            Direction stateFace = hitState.get(FACING);

            if (!stateFace.getAxis().equals(hit.getAxis())) {
                if (ctx.getPlayer().isSneaking()) {
                    return this.getDefaultState().with(FACING, stateFace.getOpposite());
                } else {
                    return this.getDefaultState().with(FACING, look.getOpposite());
                }
            } else {
                if (ctx.getPlayer().isSneaking()) {
                    return this.getDefaultState().with(FACING, look.getOpposite());
                } else {
                    return this.getDefaultState().with(FACING, stateFace.getOpposite());
                }
            }
        } else {
            return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
        }
    }

}