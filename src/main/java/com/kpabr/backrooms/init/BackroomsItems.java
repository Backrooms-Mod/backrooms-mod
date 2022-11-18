package com.kpabr.backrooms.init;

import java.util.LinkedHashMap;
import java.util.Map;

import com.kpabr.backrooms.items.FireSalt;
import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.items.AlmondWaterItem;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BackroomsItems {

	private static final Map<Identifier, Item> ITEMS = new LinkedHashMap<>();


	public static final Item ALMOND_WATER = add("almond_water", new AlmondWaterItem(new Item.Settings().group(ItemGroup.FOOD).food(new FoodComponent.Builder().alwaysEdible().snack().saturationModifier(20).hunger(4).build()).maxCount(16)));
	public static final Item FIRESALT = add("firesalt", new FireSalt(new Item.Settings().group(ItemGroup.MISC)));
	public static final Item TILEMOLD_LUMP = add("tilemold_lump", new Item(new Item.Settings().group(ItemGroup.MISC)));
	public static final Item BAKED_TILEMOLD_LUMP = add("baked_tilemold_lump", new Item(new Item.Settings().group(ItemGroup.FOOD).food(new FoodComponent.Builder().snack().saturationModifier(2).hunger(2).build())));
	public static final Item TAINTED_FLESH = add("tainted_flesh", new Item(new Item.Settings().group(ItemGroup.FOOD).food(new FoodComponent.Builder().saturationModifier(0).hunger(3).statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 600, 0), 0.8F).meat().build())));
	public static final Item COOKED_FLESH = add("cooked_flesh", new Item(new Item.Settings().group(ItemGroup.FOOD).food(new FoodComponent.Builder().saturationModifier(3).hunger(5).meat().build())));
	public static final Item ALMOND_WATER_BUCKET = add("almond_water_bucket", new BucketItem(BackroomsFluids.ALMOND_WATER_STILL, new Item.Settings().group(ItemGroup.MISC).maxCount(1)));
	public static final Item HOUND_SPAWN_EGG = add("hound_spawn_egg", new SpawnEggItem(BackroomsEntities.HOUND,16777215,8531483,new Item.Settings().group(ItemGroup.MISC)));


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
