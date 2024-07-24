package com.kpabr.backrooms.init;

import com.kpabr.backrooms.BackroomsMod;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;

public class BackroomsDamageTypes {
    public static final RegistryKey<DamageType> WRETCHED_CYCLE_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE,
            BackroomsMod.id("wretched_cycle"));

    public static DamageSource of(World world, RegistryKey<DamageType> key) {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key));
    }
}
