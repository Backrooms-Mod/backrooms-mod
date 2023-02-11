package com.kpabr.backrooms.world.biome;

import com.kpabr.backrooms.util.Color;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;

public class ElectricalStationBiome {
    public static Biome create() {
        SpawnSettings.Builder spawnSettings = new SpawnSettings.Builder();
        GenerationSettings.Builder generationSettings = new GenerationSettings.Builder();
        BiomeEffects.Builder biomeEffects = new BiomeEffects.Builder()
                .skyColor(Color.of(36, 36, 36))
                .waterColor(13548960)
                .waterFogColor(13548960)
                .fogColor(Color.of(36, 36, 36))
                .grassColor(13818488);
                //TODO: make loop sound for level two;

        // Configure level 2 default biome
        Biome.Builder biome = new Biome.Builder()
                .spawnSettings(spawnSettings.build())
                .generationSettings(generationSettings.build())
                .effects(biomeEffects.build())

                .precipitation(Biome.Precipitation.NONE)
                .category(Biome.Category.NONE)

                .temperature(15.0F)
                .downfall(0.0F);

        return biome.build();
    }

}
