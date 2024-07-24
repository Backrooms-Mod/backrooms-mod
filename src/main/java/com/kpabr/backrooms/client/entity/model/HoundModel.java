package com.kpabr.backrooms.client.entity.model;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.entity.HoundEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class HoundModel extends GeoModel<HoundEntity> {

    @Override
    public Identifier getModelResource(HoundEntity object) {
        return new Identifier(BackroomsMod.ModId, "geo/entities/hound.geo.json");
    }

    @Override
    public Identifier getTextureResource(HoundEntity object) {
        return new Identifier(BackroomsMod.ModId, "textures/entity/hound.png");
    }

    @Override
    public Identifier getAnimationResource(HoundEntity animatable) {
        return new Identifier(BackroomsMod.ModId, "animations/entities/hound.animation.json");
    }
}
