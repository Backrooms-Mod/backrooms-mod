package com.kpabr.backrooms.mixins;

import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerScreenHandler.class)
public interface PlayerScreenHandlerAccessor {

    @Accessor("EMPTY_ARMOR_SLOT_TEXTURES")
    static Identifier[] getEmptyArmorSlorTextures() {
        throw new AssertionError();
    }
}
