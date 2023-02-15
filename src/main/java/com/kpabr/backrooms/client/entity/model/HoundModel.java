package com.kpabr.backrooms.client.entity.model;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.entity.HoundEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class HoundModel extends AnimatedGeoModel<HoundEntity> {

    @Override
    public Identifier getModelLocation(HoundEntity object) {
        return new Identifier(BackroomsMod.ModId, "geo/entities/hound.geo.json");
    }

    @Override
    public Identifier getTextureLocation(HoundEntity object) {
        return new Identifier(BackroomsMod.ModId, "textures/entity/hound.png");
    }

    @Override
    public Identifier getAnimationFileLocation(HoundEntity animatable) {
        return new Identifier(BackroomsMod.ModId, "animations/entities/hound.animation.json");
    }
}
