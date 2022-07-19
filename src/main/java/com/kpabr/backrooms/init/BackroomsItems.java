package com.kpabr.backrooms.init;

import java.util.LinkedHashMap;
import java.util.Map;

import com.kpabr.backrooms.items.FireSalt;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
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
    public static final Item PYROIL = add("pyroil", new Item(new Item.Settings().group(ItemGroup.MISC)));

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