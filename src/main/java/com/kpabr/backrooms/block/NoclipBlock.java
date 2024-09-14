package com.kpabr.backrooms.block;

import com.kpabr.backrooms.util.EntityHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NoclipBlock extends Block {
    public NoclipBlock(Settings settings) {
        super(settings);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        super.onEntityCollision(state, world, pos, entity);

        if (world.isClient) {
            return;
        }

        if (entity.isPlayer()) {
            World portalBlockWorld = entity.getServer().getWorld(
                    RegistryKey.of(
                            RegistryKeys.WORLD,
                            new Identifier("backrooms:level_1")
                    )
            );

            EntityHelper.teleportToLevel((ServerPlayerEntity) entity, portalBlockWorld);
        }
    }

}
