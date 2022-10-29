package com.kpabr.backrooms.init;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.effect.RaggedStatusEffect;
import com.kpabr.backrooms.effect.RottenStatusEffect;
import com.kpabr.backrooms.effect.WretchedStatusEffect;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.registry.Registry;

public class BackroomStatusEffects {
    public static StatusEffect RAGGED = new RaggedStatusEffect();
    public static StatusEffect ROTTEN = new RottenStatusEffect();
    public static StatusEffect WRETCHED = new WretchedStatusEffect();

    public static void init() {
        Registry.register(Registry.STATUS_EFFECT, BackroomsMod.id("ragged"), RAGGED);
        Registry.register(Registry.STATUS_EFFECT, BackroomsMod.id("rotten"), ROTTEN);
        Registry.register(Registry.STATUS_EFFECT, BackroomsMod.id("wretched"), WRETCHED);
    }
}
