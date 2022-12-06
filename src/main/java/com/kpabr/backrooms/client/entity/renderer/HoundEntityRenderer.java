package com.kpabr.backrooms.client.entity.renderer;

import com.kpabr.backrooms.entity.living.HoundLivingEntity;
import com.kpabr.backrooms.client.entity.model.HoundModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class HoundEntityRenderer extends GeoEntityRenderer<HoundLivingEntity> {


    public HoundEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new HoundModel());
    }
}


