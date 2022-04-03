package com.kpabr.backrooms.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.block.ComputerBlock;
import com.kpabr.backrooms.init.BackroomsBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;

public class ComputerBlockEntity extends BlockEntity {

    public ComputerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public ComputerBlockEntity(BlockPos pos, BlockState state) {
        super(BackroomsBlocks.COMPUTER_BLOCK_ENTITY, pos, state);
    }
    

    public static void tick(World world, BlockPos pos, BlockState state, ComputerBlockEntity blockEntity) {
        if(world.isReceivingRedstonePower(pos)) {
            System.out.println("ON");
            world.setBlockState(pos, state.with(ComputerBlock.LIT, true));
        }
        else
        {
            System.out.println("OFF");
            world.setBlockState(pos, state.with(ComputerBlock.LIT, false));
        }
    }

}
