package com.kpabr.backrooms.init;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.entity.FireSaltProjectileEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class BackroomsProjectiles {

    public static final EntityType<FireSaltProjectileEntity> FIRE_SALT_PROJECTILE_ENTITY_TYPE = FabricEntityTypeBuilder
            .<FireSaltProjectileEntity>create(SpawnGroup.MISC, FireSaltProjectileEntity::new)
            .dimensions(EntityDimensions.fixed(0.5F, 0.5F))
            .trackRangeBlocks(4)
            .trackedUpdateRate(10)
            .build();

    public static void init() {
        Registry.register(
                Registries.ENTITY_TYPE,
                BackroomsMod.id("fire_salt_projectile"),
                FIRE_SALT_PROJECTILE_ENTITY_TYPE);
    }
}
