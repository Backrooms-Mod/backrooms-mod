package com.kpabr.backrooms.init;

import net.fabricmc.api.ModInitializer;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BackroomsSounds implements ModInitializer {
    public static final Identifier FIRESALT_LAND = new Identifier("backrooms:firesalt_land");
    public static SoundEvent FIRESALT_LAND_EVENT = new SoundEvent(FIRESALT_LAND);

    @Override
    public void onInitialize() {
        Registry.register(Registry.SOUND_EVENT, FIRESALT_LAND, FIRESALT_LAND_EVENT);
    }
}