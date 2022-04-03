package com.kpabr.backrooms.world.feature.level.zero;

import java.util.Locale;

import com.mojang.serialization.Codec;

import com.kpabr.backrooms.util.WallpaperType;
import net.minecraft.world.gen.feature.FeatureConfig;

public class WallpaperConfig implements FeatureConfig {
	public static final Codec<WallpaperConfig> CODEC = Codec.STRING.fieldOf("wallpaper").xmap((string) -> new WallpaperConfig(WallpaperType.valueOf(string.toUpperCase(Locale.ENGLISH))), (config) -> {
		return config.type.name;
	}).codec();

	public final WallpaperType type;

	public WallpaperConfig(WallpaperType type) {
		this.type = type;
	}

}
