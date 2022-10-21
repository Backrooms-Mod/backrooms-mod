package com.kpabr.backrooms.world.biome;

import com.kpabr.backrooms.entity.living.HoundLivingEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;

public class CrimsonHallsBiome {
    public static Biome create() {
        Biome.Builder biome = new Biome.Builder();

        SpawnSettings.Builder spawnSettings = new SpawnSettings.Builder();
        //spawnSettings.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.Builder.create(HoundLivingEntity::new, SpawnGroup.MONSTER).setDimensions(1F, 1.5F).maxTrackingRange(8).build("hound"), 5, 1, 1));

        GenerationSettings.Builder generationSettings = new GenerationSettings.Builder();

        BiomeEffects.Builder biomeEffects = new BiomeEffects.Builder();
        biomeEffects.skyColor(13776695);
        biomeEffects.waterColor(13548960);  
        biomeEffects.waterFogColor(16735821);
        biomeEffects.fogColor(11548232);
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
