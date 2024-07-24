package com.kpabr.backrooms.mixins;

import com.kpabr.backrooms.init.BackroomStatusEffects;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private int scaledWidth;
    @Shadow
    private int scaledHeight;
    @Shadow
    public float vignetteDarkness;
    private static final Identifier VIGNETTE_TEXTURE = new Identifier("textures/misc/vignette.png");

    @Environment(EnvType.CLIENT)
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableBlend()V", shift = At.Shift.AFTER), slice = @Slice(from = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableBlend()V"), to = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getLastFrameDuration()F")))
    private void renderWretchedVignette(DrawContext context, float tickDelta, CallbackInfo ci) {
        boolean wretchedCycleEffects = client.player.hasStatusEffect(BackroomStatusEffects.ROTTEN)
                || client.player.hasStatusEffect(BackroomStatusEffects.WRETCHED);
        if (wretchedCycleEffects) {
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR,
                    GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
            RenderSystem.setShaderColor(0f, 1f, 1f, 1f);
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderTexture(0, VIGNETTE_TEXTURE);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
            bufferBuilder.vertex(0.0, this.scaledHeight, -180.0).texture(0.0f, 1.0f).next();
            bufferBuilder.vertex(this.scaledWidth, this.scaledHeight, -180.0).texture(1.0f, 1.0f).next();
            bufferBuilder.vertex(this.scaledWidth, 0.0, -180.0).texture(1.0f, 0.0f).next();
            bufferBuilder.vertex(0.0, 0.0, -180.0).texture(0.0f, 0.0f).next();
            tessellator.draw();
        }

    }
}
