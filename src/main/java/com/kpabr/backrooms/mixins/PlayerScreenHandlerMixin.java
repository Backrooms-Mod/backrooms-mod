package com.kpabr.backrooms.mixins;

import com.kpabr.backrooms.items.MaskItem;
import com.mojang.datafixers.util.Pair;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerScreenHandler.class)
public abstract class PlayerScreenHandlerMixin {

    @Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/entity/player/PlayerInventory;ZLnet/minecraft/entity/player/PlayerEntity;)V")
    private void reInitHeadSlot(PlayerInventory inventory, boolean onServer, PlayerEntity owner, CallbackInfo ci) {
        Slot slot = new Slot(inventory, 39, 8, 8 + 0 * 18) {
            public int getMaxItemCount() {
                return 1;
            }

            public boolean canInsert(ItemStack stack) {
                return EquipmentSlot.HEAD == LivingEntity.getPreferredEquipmentSlot(stack);
            }

            public boolean canTakeItems(PlayerEntity playerEntity) {
                final ItemStack itemStack = this.getStack();
                return !itemStack.isEmpty() && !playerEntity.isCreative()
                        && (EnchantmentHelper.hasBindingCurse(itemStack)
                                || (itemStack.getItem() instanceof MaskItem && playerEntity.getHealth() <= 5.0F))
                                        ? false
                                        : super.canTakeItems(playerEntity);
            }

            public Pair<Identifier, Identifier> getBackgroundSprite() {
                return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE,
                        PlayerScreenHandlerAccessor.getEmptyArmorSlorTextures()[EquipmentSlot.HEAD.getEntitySlotId()]);
            }
        };
        slot.id = ((ScreenHandlerAccessor) this).getSlots().get(5).id;
        ((ScreenHandlerAccessor) this).getSlots().set(5, slot);
    }
}
