package com.kpabr.backrooms.mixins;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.datafixers.util.Pair;

import com.kpabr.backrooms.client.render.sky.SkyboxShaders;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.resource.ResourceManager;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Inject(method = "loadShaders", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 53, shift = Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
	private void corners$loadShaders(ResourceManager manager, CallbackInfo ci, List<ShaderProgram> list,
			List<Pair<ShaderProgram, Consumer<ShaderProgram>>> list2) {
		try {
			list2.add(Pair.of(new ShaderProgram(manager, "rendertype_corners_skybox", VertexFormats.POSITION),
					(shader) -> SkyboxShaders.SKYBOX_SHADER = shader));
		} catch (IOException e) {
			list2.forEach((pair) -> {
				pair.getFirst().close();
			});
			throw new RuntimeException("could not reload shaders", e);
		}
	}

}
