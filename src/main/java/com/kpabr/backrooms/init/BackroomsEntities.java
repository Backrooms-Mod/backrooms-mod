package com.kpabr.backrooms.init;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.entity.living.HoundEntity;
import com.kpabr.backrooms.entity.living.WretchEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;

public class BackroomsEntities {
    private static final ArrayList<EntityEntry> ENTITIES = new ArrayList<>();
    public static final EntityType<HoundEntity> HOUND = add("hound",
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, HoundEntity::new).dimensions(EntityDimensions.fixed(1f, 1f)).build());

    public static final EntityType<WretchEntity> WRETCH = add("wretch",
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, WretchEntity::new).dimensions(EntityDimensions.fixed(0.75f, 1.85f)).build());

    private static<E extends LivingEntity> EntityType<E> add(String name, EntityType<E> entity) {
        ENTITIES.add(new EntityEntry(BackroomsMod.id(name), entity));
        return entity;
    }

    public static void init() {
        for (EntityEntry entry : ENTITIES) {
            Registry.register(Registry.ENTITY_TYPE, entry.identifier, entry.entity);
        }

        FabricDefaultAttributeRegistry.register(WRETCH, WretchEntity.createWretchAttributes());
        FabricDefaultAttributeRegistry.register(HOUND, HoundEntity.createHoundAttributes());
    }

    private record EntityEntry(Identifier identifier, EntityType<? extends LivingEntity> entity) {}
}
