package com.kpabr.backrooms.client.entity.model;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.entity.living.WretchEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class WretchModel extends AnimatedGeoModel<WretchEntity> {
    @Override
    public Identifier getModelLocation(WretchEntity object) {
        return BackroomsMod.id("geo/entities/wretch.geo.json");
    }

    @Override
    public Identifier getTextureLocation(WretchEntity object) {
        return BackroomsMod.id("textures/entity/wretch.png");
    }

    @Override
    public Identifier getAnimationFileLocation(WretchEntity animatable) {
        return BackroomsMod.id("animations/entities/wretch.animation.json");
    }
}
