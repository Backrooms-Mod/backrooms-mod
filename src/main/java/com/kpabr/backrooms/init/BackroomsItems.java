package com.kpabr.backrooms.init;

import java.util.ArrayList;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.items.FireSalt;
import com.kpabr.backrooms.items.AlmondWaterItem;
import com.kpabr.backrooms.util.Color;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.SpawnEggItem;

public class BackroomsItems {
	public static ArrayList<ItemEntry> ITEMS = new ArrayList<>();

	public static final Item ALMOND_WATER = add("almond_water_bottle",
			new AlmondWaterItem(new Item.Settings().group(ItemGroup.FOOD).food(
					new FoodComponent.Builder().alwaysEdible().snack().saturationModifier(20).hunger(4).build()).maxCount(16)));
	public static final Item FIRESALT = add("firesalt",
			new FireSalt(new Item.Settings().group(ItemGroup.MISC)));
	public static final Item TILEMOLD_LUMP = add("tilemold_lump",
			new Item(new Item.Settings().group(ItemGroup.MISC)));
	public static final Item BAKED_TILEMOLD_LUMP = add("baked_tilemold_lump",
			new Item(new Item.Settings().group(ItemGroup.FOOD).food(new FoodComponent.Builder().snack().saturationModifier(2).hunger(2).build())));
	public static final Item TAINTED_FLESH = add("tainted_flesh",
			new Item(new Item.Settings().group(ItemGroup.FOOD).food(new FoodComponent.Builder().saturationModifier(0).hunger(3).statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 600, 0), 0.8F).meat().build())));
	public static final Item COOKED_FLESH = add("cooked_flesh",
			new Item(new Item.Settings().group(ItemGroup.FOOD).food(new FoodComponent.Builder().saturationModifier(3).hunger(5).meat().build())));
	public static final Item ALMOND_WATER_BUCKET = add("almond_water_bucket",
			new BucketItem(BackroomsFluids.STILL_ALMOND_WATER, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1)));
	public static final Item HOUND_SPAWN_EGG = add("hound_spawn_egg",
			new SpawnEggItem(BackroomsEntities.HOUND,16777215,8531483,new Item.Settings().group(ItemGroup.MISC)));
	public static final Item WRETCH_SPAWN_EGG = add("wretch_spawn_egg",
			new SpawnEggItem(BackroomsEntities.HOUND,Color.of(120, 5, 5), Color.of(89, 7, 7) ,
					new Item.Settings().group(ItemGroup.MISC)));


	private static <I extends Item> I add(String name, I item) {
		ITEMS.add(new ItemEntry(BackroomsMod.id(name), item));
		return item;
	}

	public static void init() {
		for (ItemEntry entry : ITEMS) {
			Registry.register(Registry.ITEM, entry.identifier, entry.item);
		}
	}

	private record ItemEntry(Identifier identifier, Item item) {}
}
