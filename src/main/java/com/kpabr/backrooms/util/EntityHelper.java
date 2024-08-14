package com.kpabr.backrooms.util;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import net.minecraft.util.math.random.Random;

public class EntityHelper {
    public static void teleportToLevel(ServerPlayerEntity entity, ServerWorld world) {
        Random rand = world.getRandom();

        int newX = (rand.nextInt(25) * 16) + rand.nextInt(16);
        int newZ = (rand.nextInt(25) * 16) + rand.nextInt(16);
        int newY = 30;
       
        if (world.getChunkManager().getChunkGenerator().getHeight(newX, newZ, null, world, null) <= newY) {
            newY = world.getChunkManager().getChunkGenerator().getHeight(newX, newZ, null, world, null) - 2;
        }

        BlockPos.Mutable mutBlockPos = new BlockPos(newX, newY, newZ).mutableCopy().move(Direction.DOWN);
        while (!world.isAir(mutBlockPos) || !world.isAir(mutBlockPos.down())) {
            mutBlockPos.move(Direction.SOUTH).move(Direction.EAST);
            if (world.isAir(mutBlockPos.up()) && !world.isAir(mutBlockPos)) {
                mutBlockPos.move(Direction.UP);
            }
        }

        entity.teleport((ServerWorld) world, mutBlockPos.getX() + 0.5, mutBlockPos.getY() + 0.5, mutBlockPos.getZ() + 0.5,
                entity.getYaw(), entity.getPitch());
    }
}
