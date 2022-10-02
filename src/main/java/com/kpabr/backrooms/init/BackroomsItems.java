package com.kpabr.backrooms.init;

import java.util.LinkedHashMap;
import java.util.Map;

import com.kpabr.backrooms.items.FireSalt;
import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.items.SpecialWaterItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.item.FoodComponent;

public class BackroomsItems {

	private static final Map<Identifier, Item> ITEMS = new LinkedHashMap<>();


	public static final Item ALMOND_WATER = add("almond_water", new SpecialWaterItem(new Item.Settings().group(ItemGroup.FOOD).food(new FoodComponent.Builder().alwaysEdible().snack().saturationModifier(20).hunger(4).build()).maxCount(16)));
	public static final Item FIRESALT = add("firesalt", new FireSalt(new Item.Settings().group(ItemGroup.MISC)));
	public static final Item TILEMOLD_LUMP = add("tilemold_lump", new Item(new Item.Settings().group(ItemGroup.MISC)));
	public static final Item BAKED_TILEMOLD_LUMP = add("baked_tilemold_lump", new Item(new Item.Settings().group(ItemGroup.FOOD).food(new FoodComponent.Builder().snack().saturationModifier(2).hunger(2).build()).maxCount(64)));


	private static <I extends Item> I add(String name, I item) {
		ITEMS.put(BackroomsMod.id(name), item);
		return item;
	}

	public static void init() {
		for (Identifier id : ITEMS.keySet()) {
			Registry.register(Registry.ITEM, id, ITEMS.get(id));
		}
	}
}
