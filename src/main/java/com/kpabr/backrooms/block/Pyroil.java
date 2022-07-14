package com.kpabr.backrooms.block;

import com.kpabr.backrooms.block.entity.PyroilLineBlockEntity;
import com.kpabr.backrooms.init.BackroomsBlocks;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;


public class Pyroil extends BlockWithEntity implements BlockEntityProvider {
    public Pyroil(Settings settings) {
        super(settings);
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
 
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, BackroomsBlocks.PYREOIL_LINE_BLOCK_ENTITY, (world1, pos, state1, be) -> PyroilLineBlockEntity.tick(world1, pos, state1, be));
    }
}
