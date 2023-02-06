package com.kpabr.backrooms.entity.projectile;

import static com.kpabr.backrooms.init.BackroomsItems.FIRESALT;
import static com.kpabr.backrooms.init.BackroomsParticles.FIRESALT_PARTICLE;
import static com.kpabr.backrooms.init.BackroomsProjectiles.FIRE_SALT_PROJECTILE_ENTITY_TYPE;
import static com.kpabr.backrooms.init.BackroomsSounds.FIRESALT_LAND_EVENT;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

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
                this.world.addParticle(particleEffect, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
            }
        }
    }

    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();

        world.playSound(null, entity.getBlockPos(), FIRESALT_LAND_EVENT, SoundCategory.BLOCKS, 1f, 1f);
        entity.setOnFire(true);
        entity.damage(DamageSource.thrownProjectile(this, this.getOwner()), 4.0f);
        this.world.createExplosion(this, entity.getBlockX(), entity.getBlockY() + 0.5, entity.getBlockZ(), 0.5f, true, Explosion.DestructionType.BREAK);
    }


    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.world.isClient) {
            this.world.sendEntityStatus(this, (byte) 3); // particles
            world.playSound(null, this.getBlockPos(), FIRESALT_LAND_EVENT, SoundCategory.BLOCKS, 1f, 1f);
            this.kill();
            this.world.createExplosion(this, this.getBlockX(), this.getBlockY() + 0.5, this.getBlockZ(), 0.5f, true, Explosion.DestructionType.BREAK);
        }
    }
}