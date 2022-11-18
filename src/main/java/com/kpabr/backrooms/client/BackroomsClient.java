package com.kpabr.backrooms.client;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.entity.renderer.living.HoundEntityRenderer;
import com.kpabr.backrooms.init.*;
import com.kpabr.backrooms.particle.FireSaltParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.CrackParticle;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

import java.util.Map;
import java.util.UUID;



public class BackroomsClient implements ClientModInitializer {

	public static final Identifier PacketID = new Identifier(BackroomsMod.ModId, "spawn_packet");


	@Override
	public void onInitializeClient() {
		ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register(((atlasTexture, registry) -> {
			registry.register(new Identifier("minecraft", "particle/flame"));
		}));
		/* Registers our particle client-side.
		 * First argument is our particle's instance, created previously on ExampleMod.
		 * Second argument is the particle's factory. The factory controls how the particle behaves.
		 * In this example, we'll use FlameParticle's Factory.*/
		ParticleFactoryRegistry.getInstance().register(BackroomsParticles.FIRESALT_PARTICLE, new FireSaltParticle.FireSaltFactory());

		EntityRendererRegistry.INSTANCE.register(BackroomsProjectiles.FIRE_SALT_PROJECTILE_ENT_ENTITY_TYPE, (context) ->
				new FlyingItemEntityRenderer(context));
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BackroomsBlocks.FIRESALT_CRYSTAL);
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BackroomsBlocks.TILEMOLD);
		BlockRenderLayerMap.INSTANCE.putBlock(BackroomsBlocks.PYROIL, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(BackroomsBlocks.OFFICE_DOOR, RenderLayer.getTranslucent());
		EntityRendererRegistry.INSTANCE.register(BackroomsEntities.HOUND, HoundEntityRenderer::new);

		//almond water fluid rendering
		FluidRenderHandlerRegistry.INSTANCE.register(BackroomsFluids.ALMOND_WATER_STILL,
				new SimpleFluidRenderHandler(SimpleFluidRenderHandler.WATER_STILL,
						SimpleFluidRenderHandler.WATER_FLOWING,
						SimpleFluidRenderHandler.WATER_OVERLAY, 0xFF0000));
		FluidRenderHandlerRegistry.INSTANCE.register(BackroomsFluids.ALMOND_WATER_FLOWING,
				new SimpleFluidRenderHandler(SimpleFluidRenderHandler.WATER_STILL,
						SimpleFluidRenderHandler.WATER_FLOWING,
						SimpleFluidRenderHandler.WATER_OVERLAY, 0xFF0000));
	}

	public static MinecraftClient getClient() {
		return MinecraftClient.getInstance();
	}


    /*public void receiveEntityPacket() {
        //packets
        ClientSidePacketRegistry.INSTANCE.register(PacketID, (ctx, byteBuf) -> {
            EntityType<?> et = Registry.ENTITY_TYPE.get(byteBuf.readVarInt());
            UUID uuid = byteBuf.readUuid();
            int entityId = byteBuf.readVarInt();
            Vec3d pos = EntitySpawnPacket.PacketBufUtil.readVec3d(byteBuf);
            float pitch = EntitySpawnPacket.PacketBufUtil.readAngle(byteBuf);
            float yaw = EntitySpawnPacket.PacketBufUtil.readAngle(byteBuf);
            ctx.getTaskQueue().execute(() -> {
                if (MinecraftClient.getInstance().world == null)
                    throw new IllegalStateException("Tried to spawn entity in a null world!");
                Entity e = et.create(MinecraftClient.getInstance().world);
                if (e == null)
                    throw new IllegalStateException("Failed to create instance of entity \"" + Registry.ENTITY_TYPE.getId(et) + "\"!");
                e.updateTrackedPosition(pos);
                e.setPos(pos.x, pos.y, pos.z);
                e.setPitch(pitch);
                e.setYaw(yaw);
                e.setId(entityId);
                e.setUuid(uuid);
                MinecraftClient.getInstance().world.addEntity(entityId, e);
            });
        });
    }
    */
}