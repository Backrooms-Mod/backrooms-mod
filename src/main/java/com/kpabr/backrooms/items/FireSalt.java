package com.kpabr.backrooms.items;

import com.kpabr.backrooms.block.entity.FireSaltProjectileEnt;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class FireSalt extends Item {
    public FireSalt(Settings settings) {
        super(settings);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_PLACE, 100, 1); //placeholder
        if (!world.isClient) {
            FireSaltProjectileEnt FireSaltEnt = new FireSaltProjectileEnt(world, user);
            FireSaltEnt.setItem(itemStack);
            FireSaltEnt.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 1.0F);
            world.spawnEntity(FireSaltEnt);
        }
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!user.getAbilities().creativeMode) {
            itemStack.decrement(1);
        }
        return TypedActionResult.success(itemStack, world.isClient);
    }
}