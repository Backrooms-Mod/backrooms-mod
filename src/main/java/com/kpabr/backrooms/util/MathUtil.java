package com.kpabr.backrooms.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

public class MathUtil {
    public static float getYawBetweenEntities(LivingEntity ownerEntity, LivingEntity targetEntity) {
        double d = targetEntity.getX() - ownerEntity.getX();
        double e = targetEntity.getZ() - ownerEntity.getZ();

        float yawAngleBetween = (float) (MathHelper.atan2(e, d) * 57.2957763671875) - 90.0F;
        return yawAngleBetween;
    }
}
