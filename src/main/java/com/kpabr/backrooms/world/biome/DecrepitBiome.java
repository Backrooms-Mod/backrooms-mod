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
        Biome.Builder biome = new Biome.Builder();

        SpawnSettings.Builder spawnSettings = new SpawnSettings.Builder();
        spawnSettings.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(BackroomsEntities.HOUND, 100, 1, 1));

        GenerationSettings.Builder generationSettings = new GenerationSettings.Builder();

        BiomeEffects.Builder biomeEffects = new BiomeEffects.Builder();
        biomeEffects.skyColor(13548960);
        biomeEffects.waterColor(13548960);
        biomeEffects.waterFogColor(13548960);
        biomeEffects.fogColor(13548960);
        biomeEffects.grassColor(13818488);
        biomeEffects.loopSound(BackroomsSounds.HUMBUZZ_LEVEL_0);
        BiomeEffects effects = biomeEffects.build();

        biome.spawnSettings(spawnSettings.build());
        biome.generationSettings(generationSettings.build());
        biome.effects(effects);

        biome.precipitation(Biome.Precipitation.NONE);
        biome.category(Biome.Category.NONE);

        biome.temperature(0.8F);
        biome.downfall(1.0F);

        return biome.build();
    }
}
