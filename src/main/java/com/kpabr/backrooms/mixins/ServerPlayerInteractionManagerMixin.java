package com.kpabr.backrooms.mixins;

import com.kpabr.backrooms.init.BackroomStatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class ServerPlayerInteractionManagerMixin {
    @Shadow @Final
    protected ServerPlayerEntity player;

    @Inject(method = "setGameMode(Lnet/minecraft/world/GameMode;Lnet/minecraft/world/GameMode;)V", at = @At("HEAD"))
    private void removeWretchedCycleEffects(GameMode gameMode, GameMode previousGameMode, CallbackInfo ci) {
        if(previousGameMode.isSurvivalLike() && (gameMode == GameMode.CREATIVE || gameMode == GameMode.SPECTATOR)) {
            player.removeStatusEffect(BackroomStatusEffects.RAGGED);
            player.removeStatusEffect(BackroomStatusEffects.ROTTEN);
            player.removeStatusEffect(BackroomStatusEffects.WRETCHED);
        }
    }
}
