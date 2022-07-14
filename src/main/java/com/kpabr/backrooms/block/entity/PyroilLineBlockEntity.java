package com.kpabr.backrooms.block.entity;

import com.kpabr.backrooms.init.BackroomsBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PyroilLineBlockEntity extends BlockEntity {

    private long Timer = 10L;
    public PyroilLineBlockEntity(BlockPos pos, BlockState state) {
        super(BackroomsBlocks.PYREOIL_LINE_BLOCK_ENTITY, pos, state);
    }

    public static boolean isAroundFire(World world, BlockPos center) {
        return BlockPos.stream(center.add(-1, -1, -1), center.add(1, 1, 1)).anyMatch((pos) -> world.getBlockState(pos).isIn(BlockTags.FIRE));
    }


    public static void tick(World world, BlockPos pos, BlockState state, PyroilLineBlockEntity blockEntity) {
    if(isAroundFire(world, pos)){
        if(world.getBlockState(pos.add(0,1,0)).getBlock() == Blocks.AIR || world.getBlockState(pos.add(0,1,0)).getBlock() == Blocks.CAVE_AIR || world.getBlockState(pos.add(0,1,0)).getBlock() == Blocks.VOID_AIR){
            if(--blockEntity.Timer <= 0L){
                world.setBlockState(pos.add(0, 1, 0), Blocks.FIRE.getDefaultState());
            }
        }
    }
    }
}