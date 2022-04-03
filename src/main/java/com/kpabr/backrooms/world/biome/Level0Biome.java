package com.kpabr.backrooms.world.biome;

import com.kpabr.backrooms.init.BackroomsEarlyRisers;
import com.kpabr.backrooms.init.BackroomsFeatures;
import com.kpabr.backrooms.util.Color;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep.Feature;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

public class Level0Biome {

	public static Biome create() {
		Biome.Builder biome = new Biome.Builder();

		SpawnSettings.Builder spawnSettings = (new SpawnSettings.Builder()).spawn(SpawnGroup.AMBIENT, new SpawnSettings.SpawnEntry(EntityType.MOOSHROOM, 5, 0, 2));

		GenerationSettings.Builder generationSettings = new GenerationSettings.Builder();
		generationSettings.surfaceBuilder(ConfiguredSurfaceBuilders.NOPE);
		generationSettings.feature(Feature.RAW_GENERATION, BackroomsFeatures.LEVEL0_CORK_TILES);
		generationSettings.feature(Feature.RAW_GENERATION, BackroomsFeatures.LEVEL0_FLUORESCENT_LIGHTS);
		generationSettings.feature(Feature.RAW_GENERATION, BackroomsFeatures.LEVEL0_WOOLEN_CARPET_FLOOR);
		generationSettings.feature(Feature.RAW_GENERATION, BackroomsFeatures.LEVEL0_WALLPAPER_FILLER);
		generationSettings.feature(Feature.RAW_GENERATION, BackroomsFeatures.LEVEL0_DOOR_CARVER);

		BiomeEffects.Builder biomeEffects = new BiomeEffects.Builder();
		biomeEffects.skyColor(Color.of(248, 221, 89));
		biomeEffects.waterColor(Color.of(221, 221, 89));
		biomeEffects.waterFogColor(Color.of(235, 205, 89));
		biomeEffects.fogColor(Color.of(232, 221, 101));
		biomeEffects.grassColor(Color.of(228, 209, 85));
		biomeEffects.moodSound(BiomeMoodSound.CAVE);
		BiomeEffects effects = biomeEffects.build();

		biome.spawnSettings(spawnSettings.build());
		biome.generationSettings(generationSettings.build());
		biome.effects(effects);

		biome.precipitation(Biome.Precipitation.NONE);
		biome.category(Biome.Category.valueOf(BackroomsEarlyRisers.LEVEL_0_KEY));

		biome.depth(0F);
		biome.scale(0F);

		biome.temperature(0.1F);
		biome.downfall(0.0F);

		return biome.build();
	}

}
