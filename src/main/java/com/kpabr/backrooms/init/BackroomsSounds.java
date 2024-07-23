package com.kpabr.backrooms.init;

import net.minecraft.sound.SoundEvent;

import static com.kpabr.backrooms.util.RegistryHelper.get;

public class BackroomsSounds {
    public static SoundEvent FIRESALT_LAND_EVENT;
    public static SoundEvent HUMBUZZ_LEVEL_0;

    public static SoundEvent HOUND_IDLE;
    public static SoundEvent HOUND_ATTACK;
    public static SoundEvent HOUND_HURT;
    public static SoundEvent HOUND_DEATH;

    public static void init() {
        HUMBUZZ_LEVEL_0 = get("humbuzz");
        FIRESALT_LAND_EVENT = get("firesalt_land");
        // TODO: make loop sound for level two, add in json;
        initHoundSounds();
    }

    private static void initHoundSounds() {
        HOUND_IDLE = get("entity.hound.idle");
        HOUND_ATTACK = get("entity.hound.attack");
        HOUND_HURT = get("entity.hound.hurt");
        HOUND_DEATH = get("entity.hound.death");
    }
}