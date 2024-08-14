package com.kpabr.backrooms.block;

import com.kpabr.backrooms.init.BackroomsLevels;
import com.kpabr.backrooms.util.EntityHelper;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;

public class PortalBlock extends Block {
    public static final IntProperty LEVEL = IntProperty.of("backrooms_level", 0, BackroomsLevels.LEVELS_AMOUNT);

    public PortalBlock() {
        super(FabricBlockSettings.copyOf(Blocks.END_PORTAL));
        this.setDefaultState(this.stateManager.getDefaultState().with(LEVEL, 0));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        super.onEntityCollision(state, world, pos, entity);

        if (world.isClient) {
            return;
        }

        if (entity.isPlayer()) {
            ServerWorld portalBlockWorld = entity.getServer().getWorld(
                    RegistryKey.of(
                            RegistryKeys.WORLD,
                            new Identifier("backrooms:level_" + state.get(LEVEL))));

            EntityHelper.teleportToLevel((ServerPlayerEntity) entity, portalBlockWorld);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(LEVEL);
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit) {
        if (player.hasPermissionLevel(4) && (player.isCreative() || player.isSpectator())) {
            int level = world.getBlockState(pos).get(LEVEL);

            if (level == BackroomsLevels.LEVELS_AMOUNT - 1) {
                level = 0;
            } else {
                level += 1;
            }

            world.setBlockState(pos, state.with(LEVEL, level));
            player.sendMessage(Text.of("Stored level has been set to " + level), true);

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }
}
