package com.kpabr.backrooms.client;

import com.kpabr.backrooms.client.entity.renderer.HoundEntityRenderer;
import com.kpabr.backrooms.client.entity.renderer.WretchEntityRenderer;
import com.kpabr.backrooms.init.*;
import com.kpabr.backrooms.particle.FireSaltParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.util.Identifier;

public class BackroomsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {

		// almond water fluid rendering
		FluidRenderHandlerRegistry.INSTANCE.register(BackroomsFluids.STILL_ALMOND_WATER,
				BackroomsFluids.FLOWING_ALMOND_WATER,
				new SimpleFluidRenderHandler(
						new Identifier("minecraft:block/water_still"),
						new Identifier("minecraft:block/water_flow"),
						0xE0E0FF));

		ParticleFactoryRegistry.getInstance().register(BackroomsParticles.FIRESALT_PARTICLE,
				new FireSaltParticle.FireSaltFactory());

		EntityRendererRegistry.register(BackroomsProjectiles.FIRE_SALT_PROJECTILE_ENTITY_TYPE,
				FlyingItemEntityRenderer::new);
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
				BackroomsBlocks.FIRESALT_CRYSTAL,
				BackroomsBlocks.TILEMOLD,
				BackroomsBlocks.PYROIL,
				BackroomsBlocks.ROOF_WIRING);
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(),
				BackroomsBlocks.OFFICE_DOOR,
				BackroomsBlocks.HARLEQUIN_MASK,
				BackroomsBlocks.COLOMBINA_MASK,
				BackroomsBlocks.PLATE_DOOR);
		BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(),
				BackroomsFluids.STILL_ALMOND_WATER,
				BackroomsFluids.FLOWING_ALMOND_WATER);

		BlockRenderLayerMap.INSTANCE.putBlock(BackroomsBlocks.PLATE_DOOR, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(BackroomsBlocks.OFFICE_DOOR, RenderLayer.getCutout());


		EntityRendererRegistry.register(BackroomsEntities.HOUND, HoundEntityRenderer::new);
		EntityRendererRegistry.register(BackroomsEntities.WRETCH, WretchEntityRenderer::new);
	}
}