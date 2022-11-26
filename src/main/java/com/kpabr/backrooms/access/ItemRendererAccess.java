package com.kpabr.backrooms.access;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface ItemRendererAccess {

	BakedModel getItemModelPure(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity, int seed);
}
