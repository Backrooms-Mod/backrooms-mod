package com.kpabr.backrooms.init;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.fluid.AlmondWaterFluid;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BackroomsFluids {
    public static final FlowableFluid ALMOND_WATER_STILL = register("almond_water_still", new AlmondWaterFluid.Still());
    public static final FlowableFluid ALMOND_WATER_FLOWING = register("almond_water_flowing", new AlmondWaterFluid.Flowing());

    private static FlowableFluid register(String name, FlowableFluid flowableFluid) {
        return Registry.register(Registry.FLUID, new Identifier(BackroomsMod.ModId, name), flowableFluid);
    }
}
