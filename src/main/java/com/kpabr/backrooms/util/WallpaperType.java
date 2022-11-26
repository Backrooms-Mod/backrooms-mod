package com.kpabr.backrooms.util;

import com.google.common.base.Supplier;

import com.kpabr.backrooms.init.BackroomsBlocks;
import net.minecraft.block.Block;
import net.minecraft.util.StringIdentifiable;

public enum WallpaperType implements StringIdentifiable {
	PATTERNED("patterned", () -> BackroomsBlocks.PATTERNED_WALLPAPER),
	STRIPED("striped", () -> BackroomsBlocks.STRIPED_WALLPAPER),
	DOTTED("dotted", () -> BackroomsBlocks.DOTTED_WALLPAPER),
	BLANK("blank", () -> BackroomsBlocks.BLANK_WALLPAPER),
	RED_BLANK("red_blank", () -> BackroomsBlocks.RED_BLANK_WALLPAPER),
	RED_PATTERNED("red_patterned", () -> BackroomsBlocks.RED_PATTERNED_WALLPAPER),
	RED_STRIPED("red_striped", () -> BackroomsBlocks.RED_STRIPED_WALLPAPER),
	RED_DOTTED("red_dotted", () -> BackroomsBlocks.RED_DOTTED_WALLPAPER);

	public final String name;
	public final Supplier<Block> block;

	WallpaperType(String name, Supplier<Block> block) {
		this.name = name;
		this.block = block;
	}

	@Override
	public String asString() {
		return this.name;
	}

}
