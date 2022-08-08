package com.kpabr.backrooms.entity.models.living;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.entity.living.HoundLivingEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class HoundModel extends AnimatedGeoModel<HoundLivingEntity> {
    @Override
    public Identifier getModelResource(HoundLivingEntity object) {
        return new Identifier(BackroomsMod.ModId, "geo/entities/living/hound.geo.json");
    }

    @Override
    public Identifier getTextureResource(HoundLivingEntity object) {
        return new Identifier(BackroomsMod.ModId, "textures/entity/living/hound.png");
    }

    @Override
    public Identifier getAnimationResource(HoundLivingEntity animatable) {
        return new Identifier(BackroomsMod.ModId, "animations/entities/living/hound.animation.json");
    }
}
