package com.kpabr.backrooms.init;

import net.minecraft.entity.damage.DamageSource;

public class BackroomsDamageSource extends DamageSource {
    public static final BackroomsDamageSource WRETCHED_CYCLE_DEATH = new BackroomsDamageSource("wretched_cycle");

    private BackroomsDamageSource(String name) {
        super(name);
        this.setBypassesArmor();
        this.setOutOfWorld();
    }
}
