package com.kpabr.backrooms.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;

import com.kpabr.backrooms.init.BackroomsLevels;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.BackgroundRenderer.FogType;
import net.minecraft.client.render.Camera;

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

	@Inject(method = "Lnet/minecraft/client/render/BackgroundRenderer;applyFog(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/BackgroundRenderer$FogType;FZ)V", at = @At("TAIL"))
	private static void corners$applyFog(Camera camera, FogType fogType, float viewDistance, boolean thickFog, CallbackInfo ci) {
		MinecraftClient client = MinecraftClient.getInstance();
		float fogStart = RenderSystem.getShaderFogStart();
		float fogEnd = RenderSystem.getShaderFogEnd();

		/*if (client.world.getRegistryKey().equals(CornerWorld.HOARY_CROSSROADS.getWorldKey())) {
			fogStart = fogStart / 2;
			fogEnd = fogEnd / 2;
			float cameraHeight = (float) (camera.getPos().getY() - client.world.getBottomY());
			float fogScalar = (float) MathHelper.clamp(Math.atan(((cameraHeight - 263.0F) * Math.tan(1)) / 263.0F) + 1, 0.0F, 1.0F);
			RenderSystem.setShaderFogStart(fogStart * fogScalar);
			RenderSystem.setShaderFogEnd(fogEnd * fogScalar);
		}

		 */
	}

}
