package com.kpabr.backrooms.fluid;

import com.kpabr.backrooms.init.BackroomsBlocks;
import com.kpabr.backrooms.init.BackroomsFluids;
import com.kpabr.backrooms.init.BackroomsItems;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.world.World;

public abstract class AlmondWaterFluid extends DefaultFluid {

    @Override
    public Fluid getStill() {
        return BackroomsFluids.STILL_ALMOND_WATER;
    }

    @Override
    public Fluid getFlowing() {
        return BackroomsFluids.FLOWING_ALMOND_WATER;
    }

    @Override
    public Item getBucketItem() {
        return BackroomsItems.ALMOND_WATER_BUCKET;
    }

    @Override
    public BlockState toBlockState(FluidState fluidState) {
        return BackroomsBlocks.ALMOND_WATER.getDefaultState().with(Properties.LEVEL_15, getBlockStateLevel(fluidState));
    }

    public static class Flowing extends AlmondWaterFluid {
        @Override
        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
            super.appendProperties(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getLevel(FluidState fluidState) {
            return fluidState.get(LEVEL);
        }

        @Override
        public boolean isStill(FluidState fluidState) {
            return false;
        }

        @Override
        protected boolean isInfinite(World world) {
            return false;
        }
    }

    public static class Still extends AlmondWaterFluid {
        @Override
        public int getLevel(FluidState state) {
            return 8;
        }

        @Override
        public boolean isStill(FluidState state) {
            return true;
        }

        @Override
        protected boolean isInfinite(World world) {
            return false;
        }
    }
}