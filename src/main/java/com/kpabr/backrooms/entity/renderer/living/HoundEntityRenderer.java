package com.kpabr.backrooms.entity.renderer.living;

import com.kpabr.backrooms.entity.living.HoundLivingEntity;
import com.kpabr.backrooms.entity.models.living.HoundModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class HoundEntityRenderer extends GeoEntityRenderer<HoundLivingEntity> {


    public HoundEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new HoundModel());
    }
}


