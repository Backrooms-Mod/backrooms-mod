package com.kpabr.backrooms.mixins;

import com.kpabr.backrooms.init.BackroomsItems;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.recipe.BrewingRecipeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrewingRecipeRegistry.class)
public class BrewingRecipeRegistryMixin {

    @Inject(method = "registerPotionRecipe", at = @At("HEAD"))
    private static void replaceNetherWartByTilemoldLump(Potion input, Item item, Potion output, CallbackInfo ci) {
        if(NetherWartAccessor.getNetherWart().equals(item)) {
            BrewingRecipeRegistryInvoker.invokeRegisterPotionRecipe(input, BackroomsItems.TILEMOLD_LUMP, output);
        }
    }
}
