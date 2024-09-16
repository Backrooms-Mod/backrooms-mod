package com.kpabr.backrooms.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

@Config.Gui.Background("backrooms:textures/block/patterned_wallpaper.png")
@Config(name = "backrooms")
public class BackroomsConfig implements ConfigData {
    @ConfigEntry.Category("Gameplay")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int suffocationChance = 10;

    @ConfigEntry.Category("Gameplay")
    public int almondMilkRestoring = 2;

    @ConfigEntry.Category("Gameplay")
    public int wretchedCycleStepTime = 65;
    
    @ConfigEntry.Category("Generation")
    public double moldyCorkTileChance = 0.05;

    @ConfigEntry.Category("Entities/AI")
    public boolean aiDebug = false;

    @ConfigEntry.Category("Generation")
    public double noclipCarpetingSpawnChance = 0.001;

    @ConfigEntry.Category("Generation")
    public double noclipWallSpawnChance = 0.005;

    @ConfigEntry.Category("Gameplay")
    public double noclipIntoLevelOneChance = 0.05;

    @ConfigEntry.Category("Gameplay")
    public double noclipIntoLevelTwoChance = 0.01;


    public static void init() {
        AutoConfig.register(BackroomsConfig.class, GsonConfigSerializer::new);
    }

    public static BackroomsConfig getInstance() {
        return AutoConfig.getConfigHolder(BackroomsConfig.class).getConfig();
    }
}