package com.kpabr.backrooms.init;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.entity.living.HoundLivingEntity;
import com.kpabr.backrooms.entity.living.WretchLivingEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BackroomsEntities {

    public static final EntityType<HoundLivingEntity> HOUND = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier(BackroomsMod.ModId, "hound"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, HoundLivingEntity::new).dimensions(EntityDimensions.fixed(1f, 1f)).build());

    public static final EntityType<WretchLivingEntity> WRETCHED = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier(BackroomsMod.ModId, "wretch"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, WretchLivingEntity::new).dimensions(EntityDimensions.fixed(0.75f, 0.75f)).build()
    );

    public static void init(){
        FabricDefaultAttributeRegistry.register(HOUND, HoundLivingEntity.createHoundAttributes());
        FabricDefaultAttributeRegistry.register(WRETCHED, WretchLivingEntity.createWretchedAttributes());
    }
}
