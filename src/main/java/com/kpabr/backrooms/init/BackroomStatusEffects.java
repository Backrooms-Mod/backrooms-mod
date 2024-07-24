package com.kpabr.backrooms.init;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.effect.RaggedStatusEffect;
import com.kpabr.backrooms.effect.RottenStatusEffect;
import com.kpabr.backrooms.effect.WretchedStatusEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class BackroomStatusEffects {
    public static final StatusEffect RAGGED = new RaggedStatusEffect();
    public static final StatusEffect ROTTEN = new RottenStatusEffect();
    public static final StatusEffect WRETCHED = new WretchedStatusEffect();

    public static void init() {
        Registry.register(Registries.STATUS_EFFECT, BackroomsMod.id("ragged"), RAGGED);
        Registry.register(Registries.STATUS_EFFECT, BackroomsMod.id("rotten"), ROTTEN);
        Registry.register(Registries.STATUS_EFFECT, BackroomsMod.id("wretched"), WRETCHED);
    }
}
