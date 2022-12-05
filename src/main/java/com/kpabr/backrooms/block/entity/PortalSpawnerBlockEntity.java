package com.kpabr.backrooms.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import com.kpabr.backrooms.init.BackroomsBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;

public class PortalSpawnerBlockEntity extends BlockEntity {

	public PortalSpawnerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public PortalSpawnerBlockEntity(BlockPos pos, BlockState state) {
		super(BackroomsBlocks.PORTAL_SPAWNER_BLOCK_ENTITY, pos, state);
	}

	public static void tick(World world, BlockPos pos) {
		world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.FORCE_STATE, 0);
	}

}
