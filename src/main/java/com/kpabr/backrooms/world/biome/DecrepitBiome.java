package com.kpabr.backrooms.world.biome;

import com.kpabr.backrooms.init.BackroomsEntities;
import com.kpabr.backrooms.init.BackroomsSounds;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;

public class DecrepitBiome {
    public static Biome create() {
        SpawnSettings.Builder spawnSettings = new SpawnSettings.Builder();
        spawnSettings.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(BackroomsEntities.HOUND, 100, 1, 1));

        GenerationSettings.Builder generationSettings = new GenerationSettings.Builder();

        BiomeEffects.Builder biomeEffects = new BiomeEffects.Builder()
            .skyColor(13548960)
            .waterColor(13548960)
            .waterFogColor(13548960)
            .fogColor(13548960)
            .grassColor(13818488)
            .loopSound(BackroomsSounds.HUMBUZZ_LEVEL_0);

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
