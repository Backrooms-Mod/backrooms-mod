package com.kpabr.backrooms.init;

import java.util.LinkedHashMap;
import java.util.Map;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.world.Level0;
import com.kpabr.backrooms.world.biome.Level0Biome;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import com.kpabr.backrooms.world.biome.RedRoomsBiome;

public class BackroomsBiomes {

	private static final Map<Identifier, Biome> BIOMES = new LinkedHashMap<>();

	public static final RegistryKey<Biome> LEVEL0 = add("level0", Level0Biome.create());
	public static final RegistryKey<Biome> REDROOMS_LEVEL0 = add("redrooms_level0", RedRoomsBiome.create());

	public static void init() {
		for (Identifier id : BIOMES.keySet()) {
			Registry.register(BuiltinRegistries.BIOME, id, BIOMES.get(id));
		}
		//Level0.NOISE_POINTS.put(LEVEL0, new Biome.MixedNoisePoint(0.0F, 0.0F, 0.0F, 0.0F, 0.5F));
		//Level0.NOISE_POINTS.put(REDROOMS_LEVEL0, new Biome.MixedNoisePoint(0.0F, 0.4F, 0.0F, 0.1F, 0.1F));
	}

	private static RegistryKey<Biome> add(String s, Biome b) {
		Identifier id = BackroomsMod.id(s);
		System.out.println("registering biome with id "+id);
		BIOMES.put(id, b);
		return RegistryKey.of(Registry.BIOME_KEY, id);
	}

}
