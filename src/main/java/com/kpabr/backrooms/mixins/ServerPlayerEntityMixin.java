package com.kpabr.backrooms.mixins;

import com.kpabr.backrooms.BackroomsMod;
import net.minecraft.network.encryption.PlayerPublicKey;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;


import net.ludocrypt.limlib.api.LiminalUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

	public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile, @Nullable PlayerPublicKey publicKey) {
		super(world, pos, yaw, profile, publicKey);
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void corners$tick(CallbackInfo ci) {
		if (this.world.getRegistryKey().getValue().getNamespace().equals("corners")) {
			LiminalUtil.grantAdvancement(this, BackroomsMod.id("root"));
		}
	}

}
