package com.kpabr.backrooms.entity.renderer.living;

import com.kpabr.backrooms.entity.living.WretchLivingEntity;
import com.kpabr.backrooms.entity.models.living.WretchModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class WretchEntityRenderer extends GeoEntityRenderer<WretchLivingEntity> {
    public WretchEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new WretchModel());
    }

}
