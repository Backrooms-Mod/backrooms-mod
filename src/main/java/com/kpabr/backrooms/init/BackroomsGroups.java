package com.kpabr.backrooms.init;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import com.kpabr.backrooms.BackroomsMod;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

public class BackroomsGroups {

	public static void init() {
		FabricItemGroupBuilder.create(BackroomsMod.id("items")).icon(() -> BackroomsBlocks.CORK_TILE.asItem().getDefaultStack()).appendItems((stacks) -> {
			Registry.ITEM.stream().filter((item) -> {
				return Registry.ITEM.getId(item).getNamespace().equals("backrooms");
			}).forEach((item) -> stacks.add(new ItemStack(item)));
		}).build();
	}
}
