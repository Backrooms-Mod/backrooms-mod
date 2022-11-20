package com.kpabr.backrooms.client.render.sky;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.kpabr.backrooms.config.BackroomsConfig;
import net.ludocrypt.limlib.api.render.LiminalShaderApplier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class StrongLiminalShader extends LiminalShaderApplier.SimpleShader {

    public static final Codec<StrongLiminalShader> CODEC = RecordCodecBuilder.create((instance) ->
         instance.group(
                 Identifier.CODEC.fieldOf("shader")
                         .stable()
                         .forGetter((shader) -> shader.getShaderId())
         ).apply(instance, instance.stable(StrongLiminalShader::new)));

    public StrongLiminalShader(Identifier shader) {
        super(shader);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldRender(MinecraftClient client, float tickDelta) {
        return !BackroomsConfig.getInstance().disableStrongShaders;
    }

    @Override
    public Codec<? extends LiminalShaderApplier> getCodec() {
        return CODEC;
    }

}