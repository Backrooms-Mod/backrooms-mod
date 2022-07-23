package com.kpabr.backrooms.entity.models.living;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.entity.living.HoundLivingEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class HoundModel extends AnimatedGeoModel<HoundLivingEntity> {

    @Override
    public Identifier getModelLocation(HoundLivingEntity object) {
        return new Identifier(BackroomsMod.ModId, "geo/entities/living/hound.geo.json");
    }

    @Override
    public Identifier getTextureLocation(HoundLivingEntity object) {
        return new Identifier(BackroomsMod.ModId, "textures/entity/living/hound.png");
    }

    @Override
    public Identifier getAnimationFileLocation(HoundLivingEntity animatable) {
        return new Identifier(BackroomsMod.ModId, "animations/entities/living/hound.animation.json");
    }
}
