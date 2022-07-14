package com.kpabr.backrooms.init;

import java.util.LinkedHashMap;
import java.util.Map;

import com.kpabr.backrooms.items.FireSalt;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.items.WallpaperPatternItem;
import com.kpabr.backrooms.items.SpecialWaterItem;
import com.kpabr.backrooms.util.WallpaperType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.item.FoodComponent;

public class BackroomsItems {

	private static final Map<Identifier, Item> ITEMS = new LinkedHashMap<>();

	public static final Item WALLPAPER_PATTERN = add("wallpaper_pattern", new WallpaperPatternItem(BackroomsLoomPatterns.PATTERNED, WallpaperType.PATTERNED, new FabricItemSettings().group(ItemGroup.MISC)));
	public static final Item STRIPED_PATTERN = add("striped_pattern", new WallpaperPatternItem(BackroomsLoomPatterns.STRIPED, WallpaperType.STRIPED, new FabricItemSettings().group(ItemGroup.MISC)));
	public static final Item DOTTED_PATTERN = add("dotted_pattern", new WallpaperPatternItem(BackroomsLoomPatterns.DOTTED, WallpaperType.DOTTED, new FabricItemSettings().group(ItemGroup.MISC)));
	public static final Item BLANK_PATTERN = add("blank_pattern", new WallpaperPatternItem(BackroomsLoomPatterns.BLANK, WallpaperType.BLANK, new FabricItemSettings().group(ItemGroup.MISC)));

	public static final Item RED_WALLPAPER_PATTERN = add("red_wallpaper_pattern", new WallpaperPatternItem(BackroomsLoomPatterns.RED_PATTERNED, WallpaperType.RED_PATTERNED, new FabricItemSettings().group(ItemGroup.MISC)));
	public static final Item RED_STRIPED_PATTERN = add("red_striped_pattern", new WallpaperPatternItem(BackroomsLoomPatterns.RED_STRIPED, WallpaperType.RED_STRIPED, new FabricItemSettings().group(ItemGroup.MISC)));
	public static final Item RED_DOTTED_PATTERN = add("red_dotted_pattern", new WallpaperPatternItem(BackroomsLoomPatterns.DOTTED, WallpaperType.RED_DOTTED, new FabricItemSettings().group(ItemGroup.MISC)));
	public static final Item RED_BLANK_PATTERN = add("red_blank_pattern", new WallpaperPatternItem(BackroomsLoomPatterns.BLANK, WallpaperType.RED_BLANK, new FabricItemSettings().group(ItemGroup.MISC)));

	public static final Item ALMOND_WATER = add("almond_water", new SpecialWaterItem(new Item.Settings().group(ItemGroup.FOOD).food(new FoodComponent.Builder().alwaysEdible().snack().saturationModifier(20).hunger(4).build()).maxCount(16)));
	public static final Item FIRESALT = add("firesalt", new FireSalt(new Item.Settings().group(ItemGroup.MISC)));


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
