package com.kpabr.backrooms.client.entity.renderer;

import com.kpabr.backrooms.entity.WretchEntity;
import com.kpabr.backrooms.client.entity.model.WretchModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WretchEntityRenderer extends GeoEntityRenderer<WretchEntity> {

    public WretchEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new WretchModel());
    }
}
