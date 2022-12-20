package com.kpabr.backrooms.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.UUID;

public class MaskItem extends Item implements Wearable {
    private static final UUID HEAD_UUID = UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B");

    private static final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers = ImmutableMultimap.<EntityAttribute, EntityAttributeModifier>builder()
            .put(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                    new EntityAttributeModifier(HEAD_UUID, "Mask damage boost", 4, EntityAttributeModifier.Operation.ADDITION))
            .build();
    public MaskItem(Settings settings) {
        super(settings);
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

    public static EquipmentSlot getSlotType() {
        return EquipmentSlot.HEAD;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == getSlotType() ? attributeModifiers : super.getAttributeModifiers(slot);
    }
}
