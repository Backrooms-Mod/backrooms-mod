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
    public int wretchedCycleStepTime = 20; // by default every 20 seconds we increment player's wretched parameter
    
    @ConfigEntry.Category("Gameplay")
    public double moldyCorkTileChance = 0.05;

    @ConfigEntry.Category("Entities/AI")
    public boolean aiDebug = false;

    public static void init() {
        AutoConfig.register(BackroomsConfig.class, GsonConfigSerializer::new);
    }

    public static BackroomsConfig getInstance() {
        return AutoConfig.getConfigHolder(BackroomsConfig.class).getConfig();
    }
}