package name.trimsky.lib_ai.example.client;

import name.trimsky.lib_ai.example.entity.ExampleEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class ExampleEntityRenderer extends GeoEntityRenderer<ExampleEntity> {
    public ExampleEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new ExampleEntityModel());
        this.shadowRadius = 0.25f;
    }
}
