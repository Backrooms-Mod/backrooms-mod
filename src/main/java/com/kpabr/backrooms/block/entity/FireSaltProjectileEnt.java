package com.kpabr.backrooms.block.entity;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.block.Pyroil;
import com.kpabr.backrooms.init.BackroomsBlocks;
import com.kpabr.backrooms.init.BackroomsItems;
import com.kpabr.backrooms.init.BackroomsProjectiles;
import com.kpabr.backrooms.init.BackroomsSounds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class FireSaltProjectileEnt extends ThrownItemEntity {

    public FireSaltProjectileEnt(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public FireSaltProjectileEnt(World world, LivingEntity owner) {
        super(BackroomsProjectiles.FIRE_SALT_PROJECTILE_ENT_ENTITY_TYPE, owner, world);
    }

    public FireSaltProjectileEnt(World world, double x, double y, double z) {
        super(BackroomsProjectiles.FIRE_SALT_PROJECTILE_ENT_ENTITY_TYPE, x, y, z, world);
    }

    @Override
    protected Item getDefaultItem() {
        return BackroomsItems.FIRESALT;
    }

    @Environment(EnvType.CLIENT)
    private ParticleEffect getParticleParameters() { // particles WIP
        ItemStack itemStack = this.getItem();
        return itemStack.isEmpty() ? ParticleTypes.ITEM_SNOWBALL : new ItemStackParticleEffect(ParticleTypes.ITEM, itemStack);  //placeholder
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


        world.playSound(null, entity.getBlockPos(), BackroomsSounds.FIRESALT_LAND_EVENT, SoundCategory.BLOCKS, 1f, 1f);
        entity.setOnFire(true);
        entity.damage(DamageSource.thrownProjectile(this, this.getOwner()), 4.0f);
        this.world.createExplosion(this, entity.getBlockX(), entity.getBlockY() + 0.5, entity.getBlockZ(), 0.5f, true, Explosion.DestructionType.BREAK);

    }


    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.world.isClient) { // checks if the world isn't client
            this.world.sendEntityStatus(this, (byte) 3); // particles
            world.playSound(null, this.getBlockPos(), BackroomsSounds.FIRESALT_LAND_EVENT, SoundCategory.BLOCKS, 1f, 1f);
            boolean firePlease = false;
            if(hitResult instanceof BlockHitResult) {
                BlockState hitBlock = world.getBlockState(((BlockHitResult) hitResult).getBlockPos());
                BackroomsMod.LOGGER.info(hitBlock.getBlock().getTranslationKey());
                if(hitBlock.getBlock() instanceof Pyroil) {
                    if (!hitBlock.isAir()) {
                        firePlease = true;

                    }
                }
            }
            this.kill();
            this.world.createExplosion(this, this.getBlockX(), this.getBlockY() + 0.5, this.getBlockZ(), 0.5f, true, Explosion.DestructionType.BREAK);
            if(firePlease) {
                world.setBlockState(((BlockHitResult) hitResult).getBlockPos(), Blocks.FIRE.getDefaultState());
            }
        }
    }
   /* @Override
    public Packet createSpawnPacket() {
        return this.createSpawnPacket();
    }
    */
}