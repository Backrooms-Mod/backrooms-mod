package com.kpabr.backrooms.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import com.kpabr.backrooms.block.entity.ComputerBlockEntity;
import com.kpabr.backrooms.block.entity.PortalSpawnerBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import com.kpabr.backrooms.init.BackroomsBlocks;
import org.jetbrains.annotations.Nullable;

public class PortalSpawnerBlock extends Block implements BlockEntityProvider {

	public PortalSpawnerBlock(Settings settings) {
		super(settings);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new PortalSpawnerBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		if(type == BackroomsBlocks.PORTAL_SPAWNER_BLOCK_ENTITY) {
			return (theWorld, blockPos, blockState, entity) -> PortalSpawnerBlockEntity.tick(theWorld, blockPos);
		}
		else return null;
	}

}
