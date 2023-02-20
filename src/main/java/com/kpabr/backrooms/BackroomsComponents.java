package com.kpabr.backrooms;

import com.kpabr.backrooms.component.PlayerWretchedComponent;
import com.kpabr.backrooms.component.WretchedComponent;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;

public final class BackroomsComponents implements EntityComponentInitializer {
    public static final ComponentKey<WretchedComponent> WRETCHED =
            ComponentRegistryV3.INSTANCE.getOrCreate(BackroomsMod.id("wretched"), WretchedComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        // Add the component to every PlayerEntity instance, and always copy it
        registry.registerForPlayers(WRETCHED, player -> new PlayerWretchedComponent(), RespawnCopyStrategy.ALWAYS_COPY);
    }
}