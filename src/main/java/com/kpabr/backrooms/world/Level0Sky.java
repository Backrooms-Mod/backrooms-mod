package com.kpabr.backrooms.world;

import net.minecraft.client.render.SkyProperties;
import net.minecraft.util.math.Vec3d;

public class Level0Sky extends SkyProperties {

    public Level0Sky() {
        super(Float.NaN, false, SkyType.NONE, false, false);
    }

    @Override
    public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
        return color;
    }

    @Override
    public boolean useThickFog(int camX, int camY) {
        return false;
    }

    @Override
    public float[] getFogColorOverride(float skyAngle, float tickDelta) {
        return null;
    }

}