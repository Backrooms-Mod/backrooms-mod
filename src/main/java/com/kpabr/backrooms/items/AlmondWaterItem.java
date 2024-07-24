package com.kpabr.backrooms.items;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.component.WretchedComponent;
import com.kpabr.backrooms.config.BackroomsConfig;
import com.kpabr.backrooms.init.BackroomStatusEffects;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

import static com.kpabr.backrooms.BackroomsComponents.WRETCHED;

public class AlmondWaterItem extends Item {
	public AlmondWaterItem(Settings settings) {
		super(settings);
	}

	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		super.finishUsing(stack, world, user);
		if (user instanceof ServerPlayerEntity player) {
			final int almondMilkRestoring = BackroomsConfig.getInstance().almondMilkRestoring;
			final WretchedComponent wretched = WRETCHED.get(player);

			Criteria.CONSUME_ITEM.trigger(player, stack);
			player.incrementStat(Stats.USED.getOrCreateStat(this));

			user.removeStatusEffect(StatusEffects.POISON);
			user.removeStatusEffect(StatusEffects.HUNGER);
			user.removeStatusEffect(StatusEffects.NAUSEA);
			user.removeStatusEffect(StatusEffects.WEAKNESS);
			user.removeStatusEffect(StatusEffects.WITHER);

			player.getHungerManager().add(8, 8.0f);

			if (wretched.getValue() < 24 && wretched.getValue() + almondMilkRestoring >= 24) {
				user.removeStatusEffect(BackroomStatusEffects.RAGGED);
			} else if (wretched.getValue() < 50 && wretched.getValue() + almondMilkRestoring >= 50) {
				user.removeStatusEffect(BackroomStatusEffects.ROTTEN);
			} else if (wretched.getValue() < 75 && wretched.getValue() + almondMilkRestoring >= 75) {
				user.removeStatusEffect(BackroomStatusEffects.WRETCHED);
			}

			// add 1 to almondMilkRestoring because we're calling applyWretchedCycle and
			// it's decrementing wretched parameter immediately
			wretched.remove(almondMilkRestoring + 1);
			BackroomsMod.applyWretchedCycle(player);
		} else if (stack.isEmpty()) {
			return new ItemStack(Items.GLASS_BOTTLE);
		} else if (user instanceof PlayerEntity player && !player.getAbilities().creativeMode) {
			final ItemStack itemStack = new ItemStack(Items.GLASS_BOTTLE);
			if (!player.getInventory().insertStack(itemStack)) {
				player.dropItem(itemStack, false);
			}
		}
		return stack;
	}

	public int getMaxUseTime(ItemStack stack) {
		return 40;
	}

	public UseAction getUseAction(ItemStack stack) {
		return UseAction.DRINK;
	}

	public SoundEvent getDrinkSound() {
		return SoundEvents.ITEM_HONEY_BOTTLE_DRINK;
	}

	public SoundEvent getEatSound() {
		return SoundEvents.ITEM_HONEY_BOTTLE_DRINK;
	}

	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		return ItemUsage.consumeHeldItem(world, user, hand);
	}
}
