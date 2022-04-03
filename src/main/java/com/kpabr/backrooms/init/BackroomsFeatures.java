package com.kpabr.backrooms.init;

import java.util.HashMap;
import java.util.Map;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.world.feature.ForeachExtraPosDecorator;
import com.kpabr.backrooms.world.feature.ForeachPosDecorator;
import com.kpabr.backrooms.world.feature.level.zero.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

// TODO: Check
public class BackroomsFeatures {

	private static final Map<Identifier, ConfiguredFeature<? extends FeatureConfig, ? extends Feature<? extends FeatureConfig>>> CONFIGURED_FEATURES = new HashMap<>();
	private static final Map<Identifier, Decorator<? extends DecoratorConfig>> DECORATORS = new HashMap<>();
	private static final Map<Identifier, Feature<? extends FeatureConfig>> FEATURES = new HashMap<>();

	// Level 0
	public static final Decorator<ChanceDecoratorConfig> FOREACH_POS = add("foreach_pos_decorator", new ForeachPosDecorator(ChanceDecoratorConfig.CODEC));
	public static final Decorator<ChanceDecoratorConfig> FOREACH_EXTRA_POS = add("foreach_extra_pos_decorator", new ForeachExtraPosDecorator(ChanceDecoratorConfig.CODEC));

	public static final Feature<DefaultFeatureConfig> CORK_TILE_FEATURE = add("cork_tile_feature", new TileFeature(DefaultFeatureConfig.CODEC));
	public static final ConfiguredFeature<?, ?> LEVEL0_CORK_TILES = add("level0_cork_tiles", CORK_TILE_FEATURE.configure(DefaultFeatureConfig.INSTANCE).decorate(FOREACH_POS.configure(new ChanceDecoratorConfig(104))));

	public static final Feature<DefaultFeatureConfig> FLUORESCENT_LIGHT_FEATURE = add("fluorescent_light_feature", new FluorescentLightFeature(DefaultFeatureConfig.CODEC));
	public static final ConfiguredFeature<?, ?> LEVEL0_FLUORESCENT_LIGHTS = add("level0_fluorescent_lights", FLUORESCENT_LIGHT_FEATURE.configure(DefaultFeatureConfig.INSTANCE).decorate(FOREACH_POS.configure(new ChanceDecoratorConfig(104))));

	public static final Feature<DefaultFeatureConfig> WOOLEN_CARPET_FLOOR_FEATURE = add("woolen_carpet_floor_feature", new WoolenCarpetFeature(DefaultFeatureConfig.CODEC));
	public static final ConfiguredFeature<?, ?> LEVEL0_WOOLEN_CARPET_FLOOR = add("woolen_carpet_floor", WOOLEN_CARPET_FLOOR_FEATURE.configure(DefaultFeatureConfig.INSTANCE).decorate(FOREACH_POS.configure(new ChanceDecoratorConfig(99))));

	public static final Feature<DefaultFeatureConfig> DOOR_CARVER_FEATURE = add("door_carver_feature", new DoorCarverFeature(DefaultFeatureConfig.CODEC));
	public static final ConfiguredFeature<?, ?> LEVEL0_DOOR_CARVER = add("door_carver", DOOR_CARVER_FEATURE.configure(DefaultFeatureConfig.INSTANCE).decorate(FOREACH_EXTRA_POS.configure(new ChanceDecoratorConfig(100))));

	public static final Feature<DefaultFeatureConfig> WALLPAPER_FILLER_FEATURE = add("wallpaper_filler_feature", new WallpaperFillerFeature(DefaultFeatureConfig.CODEC));
	public static final ConfiguredFeature<?, ?> LEVEL0_WALLPAPER_FILLER = add("wallpaper_filler", WALLPAPER_FILLER_FEATURE.configure(DefaultFeatureConfig.INSTANCE).decorate(FOREACH_EXTRA_POS.configure(new ChanceDecoratorConfig(100))));

	public static final Feature<DefaultFeatureConfig> RED_WALLPAPER_FILLER_FEATURE = add("red_wallpaper_filler_feature", new RedWallpaperFillerFeature(DefaultFeatureConfig.CODEC));
	public static final ConfiguredFeature<?, ?> LEVEL0_RED_WALLPAPER_FILLER = add("red_wallpaper_filler", RED_WALLPAPER_FILLER_FEATURE.configure(DefaultFeatureConfig.INSTANCE).decorate(FOREACH_EXTRA_POS.configure(new ChanceDecoratorConfig(100))));


	private static <FC extends FeatureConfig, F extends Feature<FC>> ConfiguredFeature<FC, F> add(String name, ConfiguredFeature<FC, F> cf) {
		CONFIGURED_FEATURES.put(BackroomsMod.id(name), cf);
		return cf;
	}

	private static <DC extends DecoratorConfig> Decorator<DC> add(String name, Decorator<DC> d) {
		DECORATORS.put(BackroomsMod.id(name), d);
		return d;
	}

	private static <FC extends FeatureConfig> Feature<FC> add(String name, Feature<FC> f) {
		FEATURES.put(BackroomsMod.id(name), f);
		return f;
	}

	public static void init() {
		for (Identifier id : CONFIGURED_FEATURES.keySet()) {
			Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, id, CONFIGURED_FEATURES.get(id));
		}
		for (Identifier id : DECORATORS.keySet()) {
			Registry.register(Registry.DECORATOR, id, DECORATORS.get(id));
		}
		for (Identifier id : FEATURES.keySet()) {
			Registry.register(Registry.FEATURE, id, FEATURES.get(id));
		}
	}

}
