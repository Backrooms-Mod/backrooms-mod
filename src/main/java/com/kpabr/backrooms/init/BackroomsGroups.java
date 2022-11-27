package com.kpabr.backrooms.init;

import com.kpabr.backrooms.BackroomsMod;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BackroomsGroups {

	public static void init() {
		FabricItemGroupBuilder.create(new Identifier(BackroomsMod.ModId, "items"))
				.icon(() -> BackroomsBlocks.CORK_TILE.asItem().getDefaultStack())
				.appendItems((stacks) -> Registry.ITEM.stream().filter((item) ->
						Registry.ITEM.getId(item).getNamespace().equals("backrooms")).forEach((item) -> stacks.add(new ItemStack(item)))).build();
	}
}
