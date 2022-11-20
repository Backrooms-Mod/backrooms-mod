package com.kpabr.backrooms.items;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.component.WretchedComponent;
import com.kpabr.backrooms.config.BackroomsConfig;
import com.kpabr.backrooms.init.BackroomStatusEffects;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
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
	private static final int MAX_USE_TIME = 40;

	public AlmondWaterItem(Settings settings) {
		super(settings);
	}

	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		super.finishUsing(stack, world, user);
		if (user instanceof ServerPlayerEntity serverPlayerEntity) {
			Criteria.CONSUME_ITEM.trigger(serverPlayerEntity, stack);
			serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
		}

		if (!world.isClient) {
			user.removeStatusEffect(StatusEffects.POISON);
			user.removeStatusEffect(StatusEffects.HUNGER);
			user.removeStatusEffect(StatusEffects.NAUSEA);
			user.removeStatusEffect(StatusEffects.WEAKNESS);
			user.removeStatusEffect(StatusEffects.WITHER);
			user.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 20 * 10, 1));
			user.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 20 * 30, 0));


			int almondMilkRestoring = BackroomsConfig.getInstance().almondMilkRestoring;
			WretchedComponent wretched = WRETCHED.get(user);
			wretched.remove(almondMilkRestoring+1); // +1 because we're calling applyWretchedCycle and it's decrementing wretched parameter immediately
			BackroomsMod.applyWretchedCycle((ServerPlayerEntity) user);
			if(wretched.getValue() < 24 && wretched.getValue()+almondMilkRestoring >= 24) {
				user.removeStatusEffect(BackroomStatusEffects.RAGGED);
			} else if(wretched.getValue() < 50 && wretched.getValue()+almondMilkRestoring >= 50) {
				user.removeStatusEffect(BackroomStatusEffects.ROTTEN);
			} else if(wretched.getValue() < 75 && wretched.getValue()+almondMilkRestoring >= 75) {
				user.removeStatusEffect(BackroomStatusEffects.WRETCHED);
			}

		}

		if (stack.isEmpty()) {
			return new ItemStack(Items.GLASS_BOTTLE);
		} else {
			if (user instanceof PlayerEntity && !((PlayerEntity)user).getAbilities().creativeMode) {
				ItemStack itemStack = new ItemStack(Items.GLASS_BOTTLE);
				PlayerEntity playerEntity = (PlayerEntity)user;
				if (!playerEntity.getInventory().insertStack(itemStack)) {
					playerEntity.dropItem(itemStack, false);
				}
			}

			return stack;
		}
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
