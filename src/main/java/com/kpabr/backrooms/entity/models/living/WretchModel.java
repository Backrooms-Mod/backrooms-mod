package com.kpabr.backrooms.entity.models.living;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.entity.living.WretchLivingEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class WretchModel extends AnimatedGeoModel<WretchLivingEntity> {
    @Override
    public Identifier getModelLocation(WretchLivingEntity object) {
        return BackroomsMod.id("geo/entities/living/wretch.geo.json");
    }

    @Override
    public Identifier getTextureLocation(WretchLivingEntity object) {
        return BackroomsMod.id("textures/entity/living/wretch.png");
    }

    @Override
    public Identifier getAnimationFileLocation(WretchLivingEntity animatable) {
        return BackroomsMod.id("animations/entities/living/wretch.animation.json");
    }
}
