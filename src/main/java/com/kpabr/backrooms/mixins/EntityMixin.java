package com.kpabr.backrooms.mixins;


import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.config.BackroomsConfig;
import com.kpabr.backrooms.init.BackroomsLevels;
import com.kpabr.backrooms.world.biome.LevelZeroBiomeSource;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract @Nullable MinecraftServer getServer();

    @Inject(method="tick", at=@At(value = "HEAD"))
    private void backrooms_tick(CallbackInfo ci) {
        World world = ((Entity) (Object) this).world;
        Entity entity = ((Entity) (Object) this);
        if (entity instanceof ServerPlayerEntity) {
            if (!world.isClient) {
                if (isInsideHardBlocks(entity) && world.getRegistryKey() == World.OVERWORLD && !((ServerPlayerEntity) entity).isCreative()) {
                    if (world.random.nextDouble() < BackroomsConfig.getInstance().suffocationChance) {
                        teleportToLevel((ServerPlayerEntity) entity, getServer().getWorld(BackroomsLevels.TEST_LEVEL.getWorldKey()));
                    }
                }
            }
        }
    }

    public boolean isInsideHardBlocks(Entity entity) {
        if (entity.noClip) {
            return false;
        }
        float f = ((EntityAccessor)entity).getDimension().width * 0.8f;
        Box box = Box.of(entity.getEyePos(), f, 1.0E-6, f);

        return BlockPos.stream(box).anyMatch(pos -> {
            BlockState blockState = entity.world.getBlockState(pos);
            return !blockState.isAir()
                    // default checks copied from isInsideWall() method
                    && blockState.shouldSuffocate(entity.world, pos)
                    && VoxelShapes.matchesAnywhere(blockState.getCollisionShape(entity.world, pos).offset(pos.getX(), pos.getY(), pos.getZ()), VoxelShapes.cuboid(box), BooleanBiFunction.AND)
                    // if block isn't falling(so it's not gravel or sand)
                    && !(blockState.getBlock() instanceof FallingBlock);
        });
    }

    private static void teleportToLevel(ServerPlayerEntity entity, World world) {
        Random rand = world.getRandom();

        int newX = (rand.nextInt(25) * 16) + rand.nextInt(16);
        int newZ = (rand.nextInt(25) * 16) + rand.nextInt(16);
        int newY = 30;

        BlockPos.Mutable mutBlockPos = new BlockPos(newX, newY, newZ).mutableCopy().move(Direction.DOWN);
        while (!world.isAir(mutBlockPos) && world.getBlockState(mutBlockPos) != null) {
            mutBlockPos.move(Direction.SOUTH).move(Direction.EAST);
            if (world.isAir(mutBlockPos.up()) && !world.isAir(mutBlockPos)) {
                mutBlockPos.move(Direction.UP);
            }
        }

        entity.teleport((ServerWorld) world, mutBlockPos.getX() + 0.5, mutBlockPos.getY(), mutBlockPos.getZ() + 0.5, entity.getYaw(), entity.getPitch());
    }
}
