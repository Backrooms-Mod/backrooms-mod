package com.kpabr.backrooms.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.kpabr.backrooms.init.BackroomsLevels;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;

@Mixin(BackgroundRenderer.class)
public abstract class BackgroundRendererMixin {

	@ModifyVariable(method = "render", at = @At(value = "STORE", ordinal = 2), index = 7)
	private static float corners$modifySkyColor(float in) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world.getRegistryKey().equals(BackroomsLevels.LEVEL_0_WORLD_KEY)) {
			return 1.0F;
		}
		return in;
	}
}
