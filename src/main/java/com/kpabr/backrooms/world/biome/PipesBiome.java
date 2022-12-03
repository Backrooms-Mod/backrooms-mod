package com.kpabr.backrooms.world.biome;

import com.kpabr.backrooms.util.Color;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;

public class PipesBiome {
    public static Biome create() {
        SpawnSettings spawnSettings = new SpawnSettings.Builder()
                .build();
        GenerationSettings generationSettings = new GenerationSettings.Builder()
                .build();
        BiomeEffects biomeEffects = new BiomeEffects.Builder()
                .skyColor(Color.of(36, 36, 36))
                .waterColor(13548960)
                .waterFogColor(13548960)
                .fogColor(Color.of(36, 36, 36))
                .grassColor(13818488)
                .build();
                //TODO: make loop sound for level two;

        // Configure level 2 default biome
        Biome biome = new Biome.Builder()
                .spawnSettings(spawnSettings)
                .generationSettings(generationSettings)
                .effects(biomeEffects)

                .precipitation(Biome.Precipitation.NONE)
                .category(Biome.Category.NONE)

                .temperature(30.0F)
                .downfall(0.0F)
                .build();

        return biome;
    }

}
