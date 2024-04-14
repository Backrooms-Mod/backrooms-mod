package com.kpabr.backrooms.world.biome;
import com.kpabr.backrooms.init.BackroomsSounds;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;

public class YellowHallsBiome {
    public static Biome create() {
        SpawnSettings.Builder spawnSettings = new SpawnSettings.Builder();
        GenerationSettings.Builder generationSettings = new GenerationSettings.Builder();
        BiomeEffects.Builder biomeEffects = new BiomeEffects.Builder()
                .skyColor(13548960)
                .waterColor(13548960)
                .waterFogColor(13548960)
                .fogColor(13548960)
                .grassColor(13818488)
                .loopSound(BackroomsSounds.HUMBUZZ_LEVEL_0);

        // Configure level 0 default biome
        Biome.Builder biome = new Biome.Builder()
            .spawnSettings(spawnSettings.build())
            .generationSettings(generationSettings.build())
            .effects(biomeEffects.build())

            .precipitation(Biome.Precipitation.NONE)
            .category(Biome.Category.NONE)

            .temperature(0.8F)
            .downfall(0.0F);

        return biome.build();
    }

}
