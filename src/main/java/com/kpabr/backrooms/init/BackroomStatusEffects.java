package com.kpabr.backrooms.init;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.effect.RaggedStatusEffect;
import com.kpabr.backrooms.effect.RottenStatusEffect;
import com.kpabr.backrooms.effect.WretchedStatusEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BackroomStatusEffects {
    public static final StatusEffect RAGGED = new RaggedStatusEffect();
    public static final StatusEffect ROTTEN = new RottenStatusEffect();
    public static final StatusEffect WRETCHED = new WretchedStatusEffect();

    public static void init() {
        Registry.register(Registry.STATUS_EFFECT, new Identifier(BackroomsMod.ModId, "ragged"), RAGGED);
        Registry.register(Registry.STATUS_EFFECT, new Identifier(BackroomsMod.ModId, "rotten"), ROTTEN);
        Registry.register(Registry.STATUS_EFFECT, new Identifier(BackroomsMod.ModId, "wretched"), WRETCHED);
    }
}
