package com.kpabr.backrooms.init;

import com.kpabr.backrooms.entity.projectile.FireSaltProjectileEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BackroomsProjectiles implements ModInitializer { //placeholder

    public static final EntityType<FireSaltProjectileEntity> FIRE_SALT_PROJECTILE_ENT_ENTITY_TYPE = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier("backrooms", "fire_salt_projectile"),
            FabricEntityTypeBuilder
                    .<FireSaltProjectileEntity>create(SpawnGroup.MISC, FireSaltProjectileEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5F, 0.5F))
                    .trackRangeBlocks(4)
                    .trackedUpdateRate(10)
                    .build()
    );


    @Override
    public void onInitialize() {
    }
    public static void init() {

    }
}
