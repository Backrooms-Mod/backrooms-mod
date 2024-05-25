package com.kpabr.backrooms.world.biome.biomes.level1;

import com.kpabr.backrooms.init.BackroomsEntities;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;

public class WarehouseBiome {
    public static Biome create() {
        SpawnSettings.Builder spawnSettings = new SpawnSettings.Builder()
            .spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(BackroomsEntities.HOUND, 100, 1, 1));
        GenerationSettings.Builder generationSettings = new GenerationSettings.Builder();

        BiomeEffects.Builder biomeEffects = new BiomeEffects.Builder()
                .skyColor(10921378)
                .waterColor(10921378)
                .waterFogColor(10921378)
                .fogColor(10921378)
                .grassColor(13818488);

        // Configure warehouse biome
        Biome.Builder biome = new Biome.Builder()
                .spawnSettings(spawnSettings.build())
                .generationSettings(generationSettings.build())
                .effects(biomeEffects.build())

                .precipitation(Biome.Precipitation.NONE)
                .category(Biome.Category.NONE)

                .temperature(0.8F)
                .downfall(1.0F);

        return biome.build();
    }
}
