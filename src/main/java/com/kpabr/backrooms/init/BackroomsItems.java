package com.kpabr.backrooms.init;

import java.util.ArrayList;
import java.util.HashMap;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.items.FireSalt;
import com.kpabr.backrooms.items.AlmondWaterItem;
import com.kpabr.backrooms.items.MaskItem;
import com.kpabr.backrooms.util.Color;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class BackroomsItems {
	private static HashMap<RegistryKey<ItemGroup>, ArrayList<Item>> GROUPED_ITEMS = new HashMap<RegistryKey<ItemGroup>, ArrayList<Item>>();
	public static ArrayList<ItemEntry> ITEMS = new ArrayList<>();
	public static ArrayList<BlockItem> BLOCK_ITEMS = new ArrayList<>();

	public static final Item ALMOND_WATER = add("almond_water_bottle",
			new AlmondWaterItem(new Item.Settings().food(
					new FoodComponent.Builder().alwaysEdible().snack().saturationModifier(20).hunger(0).build())
					.maxCount(16)),
			ItemGroups.FOOD_AND_DRINK);
	public static final Item FIRESALT = add("firesalt",
			new FireSalt(new Item.Settings()), ItemGroups.COMBAT);
	public static final Item TILEMOLD_LUMP = add("tilemold_lump",
			new Item(new Item.Settings()), ItemGroups.NATURAL);
	public static final Item BAKED_TILEMOLD_LUMP = add("baked_tilemold_lump",
			new Item(new Item.Settings()
					.food(new FoodComponent.Builder().snack().saturationModifier(2).hunger(2).build())),
			ItemGroups.FOOD_AND_DRINK);
	public static final Item TAINTED_FLESH = add("tainted_flesh",
			new Item(new Item.Settings().food(new FoodComponent.Builder().saturationModifier(0).hunger(3)
					.statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 600, 0), 0.8F).meat().build())),
			ItemGroups.FOOD_AND_DRINK);
	public static final Item COOKED_FLESH = add("cooked_flesh",
			new Item(new Item.Settings()
					.food(new FoodComponent.Builder().saturationModifier(3).hunger(5).meat().build())),
			ItemGroups.FOOD_AND_DRINK);
	public static final Item ALMOND_WATER_BUCKET = add("almond_water_bucket",
			new BucketItem(BackroomsFluids.STILL_ALMOND_WATER,
					new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1)),
			ItemGroups.FOOD_AND_DRINK);
	public static final Item HOUND_SPAWN_EGG = add("hound_spawn_egg",
			new SpawnEggItem(BackroomsEntities.HOUND, 16777215, 8531483, new Item.Settings()), ItemGroups.SPAWN_EGGS);
	public static final Item WRETCH_SPAWN_EGG = add("wretch_spawn_egg",
			new SpawnEggItem(BackroomsEntities.WRETCH, Color.of(120, 5, 5), Color.of(89, 7, 7),
					new Item.Settings()),
			ItemGroups.SPAWN_EGGS);
	public static final Item COLOMBINA_MASK = add(
			new MaskItem(BackroomsBlocks.COLOMBINA_MASK, new Item.Settings().maxCount(1)), ItemGroups.COMBAT);
	public static final Item HARLEQUIN_MASK = add(
			new MaskItem(BackroomsBlocks.HARLEQUIN_MASK, new Item.Settings().maxCount(1)), ItemGroups.COMBAT);
	public static final Item SOCK_BUSKIN_MASK = add(
			new MaskItem(BackroomsBlocks.SOCK_BUSKIN_MASK, new Item.Settings().maxCount(1)), ItemGroups.COMBAT);

	private static <I extends Item> I add(String name, I item, RegistryKey<ItemGroup> tab) {

		if (!GROUPED_ITEMS.containsKey(tab)) {
			GROUPED_ITEMS.put(tab, new ArrayList<Item>());
		}

		GROUPED_ITEMS.get(tab).add(item);

		ITEMS.add(new ItemEntry(BackroomsMod.id(name), item));
		return item;
	}

	private static <T extends BlockItem> Item add(T item, RegistryKey<ItemGroup> tab) {
		if (!GROUPED_ITEMS.containsKey(tab)) {
			GROUPED_ITEMS.put(tab, new ArrayList<Item>());
		}

		GROUPED_ITEMS.get(tab).add(item);

		BLOCK_ITEMS.add(item);
		return item;
	}

	public static void init() {
		for (ItemEntry entry : ITEMS) {
			Registry.register(Registries.ITEM, entry.identifier, entry.item);
		}
		for (BlockItem item : BLOCK_ITEMS) {
			Registry.register(Registries.ITEM, Registries.BLOCK.getId(item.getBlock()), item);
		}

		for (RegistryKey<ItemGroup> groupKey : GROUPED_ITEMS.keySet()) {
			ItemGroupEvents.modifyEntriesEvent(groupKey)
					.register((itemGroup) -> addAllItemsToGroup(itemGroup, groupKey));
		}
	}

	private static void addAllItemsToGroup(FabricItemGroupEntries group, RegistryKey<ItemGroup> key) {
		if (GROUPED_ITEMS.containsKey(key)) {
			for (Item item : GROUPED_ITEMS.get(key)) {
				group.add(item);
			}
		}
	}

	public record ItemEntry(Identifier identifier, Item item) {
	}

}
