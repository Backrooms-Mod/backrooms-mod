package com.kpabr.backrooms.init;

import com.kpabr.backrooms.entity.living.HoundLivingEntity;
import com.kpabr.backrooms.entity.living.WretchEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BackroomsEntities {
    public static void init() {
        FabricDefaultAttributeRegistry.register(WRETCH, WretchEntity.createWretchAttributes());
        FabricDefaultAttributeRegistry.register(HOUND, HoundLivingEntity.createHoundAttributes());
    }

    public static final EntityType<HoundLivingEntity> HOUND = Registry.register(Registry.ENTITY_TYPE,
            new Identifier("backrooms", "hound"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, HoundLivingEntity::new).dimensions(EntityDimensions.fixed(1f, 1f)).build()
    );

    public static final EntityType<WretchEntity> WRETCH = Registry.register(Registry.ENTITY_TYPE,
            new Identifier("backrooms", "wretch"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, WretchEntity::new).dimensions(EntityDimensions.fixed(0.75f, 1.85f)).build()
    );
}
