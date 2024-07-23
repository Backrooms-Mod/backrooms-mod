package com.kpabr.backrooms.util;

public class LevelParameters {

    /**
     * All parameters from 0 to 1, both inclusive
     * 
     * @param temperature 0: very cold; 0.5: normal; 1: very hot. biomes with
     *                    similar temperatures will generate near each other
     * @param moistness   0: very dry, like dessert; 0.5: normal, like living room;
     *                    1: very wet, like water. biomes with similar moistness
     *                    will generate near each other
     * @param integrity   0: completely destructed; 0.5: used, but still ok; 1:
     *                    completely new. biomes with similar integrity will
     *                    generate near each other
     * @param purity      0: extremely dirty; 0.5: normal; 1: as pure as a
     *                    disinfectant. biomes with similar purity will generate
     *                    near each other
     * @param toxicity    0: absolutely not toxic or contaminated; 0.5: toxic; 1:
     *                    extremely toxic or contaminated, cannot survive here for
     *                    long. biomes with similar toxicity will generate near each
     *                    other
     * 
     *                    Rareness can be from 0 to technically infinity
     * @param rareness    0: too common, every part of the level will be this biome;
     *                    0.5: common, use for standard biome; 1: normal, like
     *                    decrepit biome in level 0; 10: very rare, will most likely
     *                    not generate
     */

    public final double temperature;
    public final double moistness;
    public final double integrity;
    public final double purity;
    public final double toxicity;

    public final double rareness;

    public LevelParameters(double temperature, double moistness, double integrity, double purity, double toxicity,
            double rareness) {
        this.temperature = temperature;
        this.moistness = moistness;
        this.rareness = rareness;
        this.integrity = integrity;
        this.purity = purity;
        this.toxicity = toxicity;
    }
}
