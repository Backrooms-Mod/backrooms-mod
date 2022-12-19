package com.kpabr.backrooms.init;

import java.util.ArrayList;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.fluid.AlmondWaterFluid;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BackroomsFluids {
    private static final ArrayList<FluidEntry> FLUIDS = new ArrayList<>();

    public static final FlowableFluid STILL_ALMOND_WATER = add("almond_water", new AlmondWaterFluid.Still());
    public static final FlowableFluid FLOWING_ALMOND_WATER = add("flowing_almond_water", new AlmondWaterFluid.Flowing());

    private static <F extends FlowableFluid> F add(String name, F fluid) {
        FLUIDS.add(new FluidEntry(BackroomsMod.id(name), fluid));
        return fluid;
    }

    public static void init() {
        for (FluidEntry entry : FLUIDS) {
            Registry.register(Registry.FLUID, entry.identifier, entry.fluid);
        }
    }

    private record FluidEntry(Identifier identifier, FlowableFluid fluid) {}
}
