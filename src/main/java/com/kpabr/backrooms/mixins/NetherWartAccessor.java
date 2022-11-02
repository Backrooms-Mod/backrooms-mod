package com.kpabr.backrooms.mixins;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Items.class)
public interface NetherWartAccessor {
    @Accessor("NETHER_WART")
    public static Item getNetherWart() {
        throw new AssertionError();
    }
}
