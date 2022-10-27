package com.kpabr.backrooms.init;

import com.kpabr.backrooms.BackroomsMod;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.registry.Registry;

public class BackroomsParticles {
    public static final DefaultParticleType FIRESALT_PARTICLE = FabricParticleTypes.simple();

    public static void init() {
        Registry.register(Registry.PARTICLE_TYPE, BackroomsMod.id("firesalt"), FIRESALT_PARTICLE);
    }
}
