package com.kpabr.backrooms.init;
import net.minecraft.sound.SoundEvent;
import static com.kpabr.backrooms.util.RegistryHelper.get;
public class BackroomsSoundEvents {
    public static SoundEvent HUMBUZZ_LEVEL_0;
    public static void init() {
        HUMBUZZ_LEVEL_0 = get("humbuzz");
    }

}
