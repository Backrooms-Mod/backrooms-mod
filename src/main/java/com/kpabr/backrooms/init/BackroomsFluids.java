package com.kpabr.backrooms.init;

import java.util.LinkedHashMap;
import java.util.Map;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.fluid.AlmondWaterFluid;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BackroomsFluids {
    private static final Map<Identifier, FlowableFluid> FLUIDS = new LinkedHashMap<>();


    public static final FlowableFluid ALMOND_WATER_STILL = add("almond_water_still", new AlmondWaterFluid.Still());
    public static final FlowableFluid ALMOND_WATER_FLOWING = add("almond_water_flowing", new AlmondWaterFluid.Flowing());

    private static <F extends FlowableFluid> F add(String name, F fluid) {
        FLUIDS.put(new Identifier(BackroomsMod.ModId, name), fluid);
        return fluid;
    }

    public static void init() {
        for (Identifier id : FLUIDS.keySet()) {
            Registry.register(Registry.FLUID, id, FLUIDS.get(id));
        }
    }
}
