package com.kpabr.backrooms.config;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

@Config(name = "backrooms")
public class BackroomsConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip()
    public boolean disableStrongShaders = true;

    @ConfigEntry.Gui.Tooltip()
    public int suffocationChance = 10;

    @ConfigEntry.Gui.Tooltip()
    public int almondMilkRestoring = 2;

    @ConfigEntry.Gui.Tooltip()
    public int wretchedCycleStepTime = 20; // by default every 20 seconds we increment player's wretched parameter

    public static void init() {
        AutoConfig.register(BackroomsConfig.class, GsonConfigSerializer::new);
    }

    public static BackroomsConfig getInstance() {
        return AutoConfig.getConfigHolder(BackroomsConfig.class).getConfig();
    }

}