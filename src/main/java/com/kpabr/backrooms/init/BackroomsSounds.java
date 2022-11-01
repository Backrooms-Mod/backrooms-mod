package com.kpabr.backrooms.init;

import net.fabricmc.api.ModInitializer;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.kpabr.backrooms.util.RegistryHelper.get;

public class BackroomsSounds {
    public static SoundEvent FIRESALT_LAND_EVENT;
    public static SoundEvent HUMBUZZ_LEVEL_0;

    public static SoundEvent HOUND_ATTACK;

    public static void init() {
        HUMBUZZ_LEVEL_0 = get("humbuzz");
        FIRESALT_LAND_EVENT = get("firesalt_land");
        initHoundSounds();
    }

    private static void initHoundSounds() {
        HOUND_ATTACK = get("entity.hound.attack");
    }
}