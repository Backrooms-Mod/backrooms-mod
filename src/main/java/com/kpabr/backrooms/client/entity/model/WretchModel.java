package com.kpabr.backrooms.client.entity.model;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.entity.WretchEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class WretchModel extends GeoModel<WretchEntity> {
    @Override
    public Identifier getModelResource(WretchEntity object) {
        return BackroomsMod.id("geo/entities/wretch.geo.json");
    }

    @Override
    public Identifier getTextureResource(WretchEntity object) {
        return BackroomsMod.id("textures/entity/wretch.png");
    }

    @Override
    public Identifier getAnimationResource(WretchEntity animatable) {
        return BackroomsMod.id("animations/entities/wretch.animation.json");
    }
}
