package name.trimsky.lib_ai.example;

import name.trimsky.lib_ai.LibAI;
import name.trimsky.lib_ai.example.client.ExampleEntityRenderer;
import name.trimsky.lib_ai.example.entity.ExampleEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LibAIMod implements ModInitializer, ClientModInitializer {
	static final public String modID = "lib_ai_example";
	public static final Logger LOGGER = LoggerFactory.getLogger(modID);
	public static boolean DISABLE_IN_DEV_ENVIRONMENT = false;

	public static EntityType<ExampleEntity> EXAMPLE_ENTITY;

	@Override
	public void onInitialize() {
		LibAI.initialize();
		if(!DISABLE_IN_DEV_ENVIRONMENT && FabricLoader.getInstance().isDevelopmentEnvironment()) {
			EXAMPLE_ENTITY = FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, ExampleEntity::new).dimensions(EntityDimensions.fixed(1f, 1f)).build();
			Registry.register(Registry.ENTITY_TYPE, new Identifier(modID, "example_entity"), EXAMPLE_ENTITY);
			FabricDefaultAttributeRegistry.register(EXAMPLE_ENTITY, ExampleEntity.createMobAttributes());
		}
	}

	@Override
	public void onInitializeClient() {
		if(!LibAIMod.DISABLE_IN_DEV_ENVIRONMENT && FabricLoader.getInstance().isDevelopmentEnvironment()) {
			EntityRendererRegistry.register(LibAIMod.EXAMPLE_ENTITY, ExampleEntityRenderer::new);
		}
	}
}
