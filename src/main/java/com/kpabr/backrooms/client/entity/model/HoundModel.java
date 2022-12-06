package com.kpabr.backrooms.client.entity.model;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.entity.living.HoundLivingEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class HoundModel extends AnimatedGeoModel<HoundLivingEntity> {

    @Override
    public Identifier getModelLocation(HoundLivingEntity object) {
        return new Identifier(BackroomsMod.ModId, "geo/entities/hound.geo.json");
    }

    @Override
    public Identifier getTextureLocation(HoundLivingEntity object) {
        return new Identifier(BackroomsMod.ModId, "textures/entity/hound.png");
    }

    @Override
    public Identifier getAnimationFileLocation(HoundLivingEntity animatable) {
        return new Identifier(BackroomsMod.ModId, "animations/entities/hound.animation.json");
    }
}
