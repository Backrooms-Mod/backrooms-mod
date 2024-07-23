package com.kpabr.backrooms.init;

import java.util.ArrayList;

import com.kpabr.backrooms.BackroomsMod;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BackroomsGroups {

	public static final RegistryKey<ItemGroup> ITEMS_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(),
			BackroomsMod.id("items"));
	public static final ItemGroup ITEMS_GROUP = FabricItemGroup.builder()
			.icon(() -> BackroomsBlocks.PATTERNED_WALLPAPER.asItem().getDefaultStack())
			.displayName(Text.translatable("itemGroup.backrooms.items"))
			.build();

	public static void init() {
		Registry.register(Registries.ITEM_GROUP, ITEMS_GROUP_KEY, ITEMS_GROUP);

		ArrayList<Identifier> itemIds = new ArrayList<Identifier>();
		// Collections.reverse(itemIds);
		for (com.kpabr.backrooms.init.BackroomsBlocks.ItemEntry entry : BackroomsBlocks.ITEMS) {
			itemIds.add(entry.identifier());
		}
		for (BlockItem item : BackroomsItems.BLOCK_ITEMS) {
			itemIds.add(Registries.BLOCK.getId(item.getBlock()));
		}
		for (com.kpabr.backrooms.init.BackroomsItems.ItemEntry entry : BackroomsItems.ITEMS) {
			itemIds.add(entry.identifier());
		}

		ItemGroupEvents.modifyEntriesEvent(ITEMS_GROUP_KEY).register(itemGroup -> {
			for (Identifier entry : itemIds) {
				itemGroup.add(Registries.ITEM.get(entry));
			}
		});
	}
}
