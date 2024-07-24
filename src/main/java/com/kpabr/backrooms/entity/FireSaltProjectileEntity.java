package com.kpabr.backrooms.entity;

import static com.kpabr.backrooms.init.BackroomsItems.FIRESALT;
import static com.kpabr.backrooms.init.BackroomsParticles.FIRESALT_PARTICLE;
import static com.kpabr.backrooms.init.BackroomsProjectiles.FIRE_SALT_PROJECTILE_ENTITY_TYPE;
import static com.kpabr.backrooms.init.BackroomsSounds.FIRESALT_LAND_EVENT;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class FireSaltProjectileEntity extends ThrownItemEntity {

    public FireSaltProjectileEntity(EntityType<? extends FireSaltProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public FireSaltProjectileEntity(World world, LivingEntity owner) {
        super(FIRE_SALT_PROJECTILE_ENTITY_TYPE, owner, world);
    }

    @Override
    protected Item getDefaultItem() {
        return FIRESALT;
    }

    @Override
    public boolean isOnFire() {
        return true;
    }

    @Environment(EnvType.CLIENT)
    private ParticleEffect getParticleParameters() { // particles WIP
        return FIRESALT_PARTICLE;
    }

    @Environment(EnvType.CLIENT)
    public void handleStatus(byte status) { // particles WIP
        if (status == 3) {
            ParticleEffect particleEffect = this.getParticleParameters();
            for (int i = 0; i < 8; ++i) {
                this.getWorld().addParticle(particleEffect, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
            }
        }
    }

    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();

        this.getWorld().playSound(null, entity.getBlockPos(), FIRESALT_LAND_EVENT, SoundCategory.BLOCKS, 1f, 1f);
        entity.setOnFire(true);
        this.getWorld().createExplosion(this, (double) entity.getBlockX(), (double) entity.getBlockY() + 0.5,
                (double) entity.getBlockZ(), 0.5f, true, World.ExplosionSourceType.NONE);
    }

    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getWorld().isClient()) {
            this.getWorld().sendEntityStatus(this, (byte) 3); // particles
            this.getWorld().playSound(null, this.getBlockPos(), FIRESALT_LAND_EVENT, SoundCategory.BLOCKS, 1f, 1f);
            this.kill();
            this.getWorld().createExplosion(this, (double) this.getBlockX(), (double) this.getBlockY() + 0.5,
                    (double) this.getBlockZ(), 0.5f, true, World.ExplosionSourceType.NONE);
        }
    }
}