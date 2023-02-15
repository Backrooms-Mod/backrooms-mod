package name.trimsky.lib_ai.example.client;

import name.trimsky.lib_ai.example.LibAIMod;
import name.trimsky.lib_ai.example.entity.ExampleEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ExampleEntityModel extends AnimatedGeoModel<ExampleEntity> {
    private static final Identifier modelId = new Identifier(LibAIMod.modID, "geo/example_entity.geo.json");
    private static final Identifier textureId = new Identifier(LibAIMod.modID, "textures/entity/example_entity.png");
    private static final Identifier animationId = new Identifier(LibAIMod.modID, "animations/example_entity.animation.json");

    @Override
    public Identifier getModelLocation(ExampleEntity object) {
        return modelId;
    }

    @Override
    public Identifier getTextureLocation(ExampleEntity object) {
        return textureId;
    }

    @Override
    public Identifier getAnimationFileLocation(ExampleEntity animatable) {
        return animationId;
    }
}