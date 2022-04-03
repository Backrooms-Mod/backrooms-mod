package com.kpabr.backrooms.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import com.kpabr.backrooms.block.entity.ComputerBlockEntity;
import com.kpabr.backrooms.init.BackroomsBlocks;
import org.jetbrains.annotations.Nullable;

public class ComputerBlock extends HorizontalFacingBlock implements BlockEntityProvider {

    public static final BooleanProperty LIT = Properties.LIT;

    public ComputerBlock(FabricBlockSettings fabricBlockSettings) {
        super(fabricBlockSettings);
        setDefaultState(this.stateManager.getDefaultState().with(LIT, false).with(Properties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(LIT);
        stateManager.add(Properties.HORIZONTAL_FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ComputerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if(type == BackroomsBlocks.COMPUTER_BLOCK_ENTITY) {
            return (theWorld, blockPos, blockState, entity) -> ComputerBlockEntity.tick(theWorld, blockPos, blockState, (ComputerBlockEntity)(entity));
        }
        else
        {
            return null;
        }
    }
}
