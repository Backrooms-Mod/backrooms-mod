package com.kpabr.backrooms.mixins;

import com.kpabr.backrooms.config.BackroomsConfig;
import com.kpabr.backrooms.init.BackroomsLevels;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.kpabr.backrooms.util.EntityHelper.teleportToLevel;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract @Nullable MinecraftServer getServer();

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void backroomsTick(CallbackInfo ci) {
        World world = ((Entity) (Object) this).getWorld();
        Entity entity = ((Entity) (Object) this);

        if (entity instanceof ServerPlayerEntity) {
            if (!world.isClient) {
                if (isInsideHardBlocks(entity)
                        && !((ServerPlayerEntity) entity).isCreative()
                        && world.random.nextDouble() < BackroomsConfig.getInstance().suffocationChance) {
                    World levelZero = getServer().getWorld(BackroomsLevels.LEVEL_0_WORLD_KEY);

                    RegistryKey<World> worldKey = world.getRegistryKey();
                    if (worldKey == World.OVERWORLD) {
                        teleportToLevel((ServerPlayerEntity) entity, levelZero, 30);
                    } else if (worldKey == levelZero.getRegistryKey()) {
                        teleportToLevel((ServerPlayerEntity) entity,
                                getServer().getWorld(BackroomsLevels.LEVEL_1_WORLD_KEY), 30);
                    }
                }
            }
        }
    }

    public boolean isInsideHardBlocks(Entity entity) {
        if (entity.noClip) {
            return false;
        }
        float f = ((EntityAccessor) entity).getDimension().width * 0.8f;
        Box box = Box.of(entity.getEyePos(), f, 1.0E-6, f);

        return BlockPos.stream(box).anyMatch(pos -> {
            BlockState blockState = entity.getWorld().getBlockState(pos);
            return !blockState.isAir()
                    // default checks copied from isInsideWall() method
                    && blockState.shouldSuffocate(entity.getWorld(), pos)
                    && VoxelShapes.matchesAnywhere(blockState.getCollisionShape(entity.getWorld(), pos)
                            .offset(pos.getX(), pos.getY(), pos.getZ()), VoxelShapes.cuboid(box), BooleanBiFunction.AND)
                    // if block isn't falling(so it's not gravel or sand)
                    && !(blockState.getBlock() instanceof FallingBlock);
        });
    }

}
