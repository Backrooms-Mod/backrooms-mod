package com.kpabr.backrooms.init;

import net.minecraft.entity.damage.DamageSource;

public class BackroomsDamageSource extends DamageSource {
    public static final DamageSource WRETCHED_CYCLE_DEATH = new BackroomsDamageSource("wretched_cycle");

    public BackroomsDamageSource(String name) {
        super(name);
        this.setBypassesArmor();
        this.setOutOfWorld();
    }
}
