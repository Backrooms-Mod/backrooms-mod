package com.kpabr.backrooms.init;

import com.kpabr.backrooms.entity.living.HoundLivingEntity;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.chunk.ChunkGenerator;


public class BackroomsEntities {

    public static final EntityType<HoundLivingEntity> HOUND = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier("backrooms", "hound"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, HoundLivingEntity::new).dimensions(EntityDimensions.fixed(1f, 1f)).build());

    public static void init(){
        FabricDefaultAttributeRegistry.register(HOUND, HoundLivingEntity.createHoundAttributes());
    }
}
