package com.kpabr.backrooms.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class MaskItem extends BlockItem implements Equipment {
    private static final UUID HEAD_UUID = UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B");
    private static final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers = ImmutableMultimap
            .<EntityAttribute, EntityAttributeModifier>builder()
            .put(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                    new EntityAttributeModifier(HEAD_UUID, "Mask damage boost", 4,
                            EntityAttributeModifier.Operation.ADDITION))
            .build();

    public MaskItem(Block wallBlock, Settings settings) {
        super(wallBlock, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        final ItemStack stackInHand = player.getStackInHand(hand);
        final ItemStack headSlot = player.getEquippedStack(EquipmentSlot.HEAD);
        if (headSlot.isEmpty()) {
            player.equipStack(EquipmentSlot.HEAD, stackInHand.copy());
            if (!world.isClient()) {
                player.incrementStat(Stats.USED.getOrCreateStat(this));
            }
            stackInHand.setCount(0);
            return TypedActionResult.success(stackInHand, world.isClient());
        } else {
            return TypedActionResult.fail(stackInHand);
        }
    }

    @Nullable
    @Override
    protected BlockState getPlacementState(ItemPlacementContext context) {
        final BlockState blockState = this.getBlock().getPlacementState(context);
        final World world = context.getWorld();
        final BlockPos blockPos = context.getBlockPos();
        final Direction[] placementDirections = context.getPlacementDirections();

        for (Direction direction : placementDirections) {
            if (direction != Direction.UP && direction != Direction.DOWN) {
                if (blockState != null && blockState.canPlaceAt(world, blockPos)) {
                    break;
                }
            }
        }
        return blockState != null && world.canPlace(blockState, blockPos, ShapeContext.absent()) ? blockState : null;
    }

    public EquipmentSlot getSlotType() {
        return EquipmentSlot.HEAD;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == getSlotType() ? attributeModifiers : super.getAttributeModifiers(slot);
    }
}
