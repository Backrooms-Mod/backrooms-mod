package com.kpabr.backrooms.client;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.entity.renderer.living.HoundEntityRenderer;
import com.kpabr.backrooms.init.BackroomsEntities;
import com.kpabr.backrooms.init.BackroomsProjectiles;
import com.kpabr.backrooms.util.EntitySpawnPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.impl.client.renderer.registry.EntityRendererRegistryImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.SkyProperties;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import com.kpabr.backrooms.init.BackroomsBlocks;
import com.kpabr.backrooms.world.Level0;
import com.kpabr.backrooms.world.Level0Sky;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

import java.util.Map;
import java.util.UUID;

public class BackroomsClient implements ClientModInitializer {

	public static final Identifier PacketID = new Identifier(BackroomsMod.ModId, "spawn_packet");

	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.INSTANCE.register(BackroomsProjectiles.FIRE_SALT_PROJECTILE_ENT_ENTITY_TYPE, (context) ->		//projectile rendering, when porting it to 1.18 just remove .INSTANCE
				new FlyingItemEntityRenderer(context));
		receiveEntityPacket();
		BlockRenderLayerMap.INSTANCE.putBlock(BackroomsBlocks.OFFICE_DOOR, RenderLayer.getTranslucent());
		EntityRendererRegistry.INSTANCE.register(BackroomsEntities.HOUND, HoundEntityRenderer::new);
	}

	public void registerModSkies(Map<Identifier, SkyProperties> map) {
		SkyProperties Level0Sky = new Level0Sky();
		//map.put(Level0.LEVEL_0_ID, Level0Sky);
	}
	public void receiveEntityPacket() {
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
}