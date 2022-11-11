package com.kpabr.backrooms.world.biome;

import com.kpabr.backrooms.init.BackroomsEntities;
import com.kpabr.backrooms.init.BackroomsSounds;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;

public class ParkingGarageBiome {
    public static Biome create() {
        Biome.Builder biome = new Biome.Builder();

        SpawnSettings.Builder spawnSettings = new SpawnSettings.Builder();
        spawnSettings.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(BackroomsEntities.HOUND, 100, 1, 1));

        GenerationSettings.Builder generationSettings = new GenerationSettings.Builder();

        BiomeEffects.Builder biomeEffects = new BiomeEffects.Builder();
        biomeEffects.skyColor(10921378);
        biomeEffects.waterColor(10921378);
        biomeEffects.waterFogColor(10921378);
        biomeEffects.fogColor(10921378);
        biomeEffects.grassColor(13818488);
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
