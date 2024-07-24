package com.kpabr.backrooms.particle;

import com.kpabr.backrooms.init.BackroomsItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DefaultParticleType;

@Environment(EnvType.CLIENT)
public class FireSaltParticle extends SpriteBillboardParticle {
    private final float sampleU;
    private final float sampleV;

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.TERRAIN_SHEET;
    }

    protected FireSaltParticle(ClientWorld world, double x, double y, double z, ItemStack stack) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.setSprite(MinecraftClient.getInstance().getItemRenderer().getModel(stack, world, null, 0).getParticleSprite());
        this.gravityStrength = 1.0f;
        this.scale /= 2.0f;
        this.sampleU = this.random.nextFloat() * 3.0f;
        this.sampleV = this.random.nextFloat() * 3.0f;
    }

    @Override
    protected float getMinU() {
        return this.sprite.getFrameU((this.sampleU + 1.0f) * 4.0f);
    }

    @Override
    protected float getMaxU() {
        return this.sprite.getFrameU(this.sampleU * 4.0f);
    }

    @Override
    protected float getMinV() {
        return this.sprite.getFrameV(this.sampleV * 4.0f);
    }

    @Override
    protected float getMaxV() {
        return this.sprite.getFrameV((this.sampleV + 1.0f) * 4.0f);
    }

    @Environment(value = EnvType.CLIENT)
    public static class FireSaltFactory
            implements ParticleFactory<DefaultParticleType> {
        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new FireSaltParticle(clientWorld, d, e, f, new ItemStack(BackroomsItems.FIRESALT));
        }
    }
}
