package com.kpabr.backrooms.init;
import net.minecraft.sound.SoundEvent;
import static com.kpabr.backrooms.util.RegistryHelper.get;
public class BackroomsSoundEvents {

    //public static final SoundEvent MUSIC_COMMUNAL_CORRIDORS = get("music.communal_corridors");

    public static SoundEvent HUMBUZZ_LEVEL_0;

    public static void init() {
        HUMBUZZ_LEVEL_0 = get("humbuzz");
    }

    // Radio
    //public static final SoundEvent RADIO_COMMUNAL_CORRIDORS = get("radio.communal_corridors");


    // Ambient
    //public static final SoundEvent BIOME_LOOP_COMMUNAL_CORRIDORS = get("biome.communal_corridors.loop");

}
