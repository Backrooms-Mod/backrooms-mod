package com.kpabr.backrooms.world;

import java.util.Map;
import java.util.OptionalLong;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.init.BackroomsBlocks;
import com.kpabr.backrooms.world.chunk.MazeChunkGenerator;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.VoronoiBiomeAccessType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;

public class Level0{

	//public static final Identifier LEVEL_0_ID = BackroomsMod.id("the_lobby");
	//public static final Map<RegistryKey<Biome>, Biome.MixedNoisePoint> NOISE_POINTS = Maps.newHashMap();
	//public static final NoiseSettings DEFAULT = new NoiseSettings(7, ImmutableList.of(1.0D));

	public Level0() {
		//super(LEVEL_0_ID, DimensionTypeAccessor.createDimensionType(OptionalLong.of(1200), true, false, false, false, 1, false, false, false, false, false, 256, VoronoiBiomeAccessType.INSTANCE, BlockTags.INFINIBURN_OVERWORLD.getId(), LEVEL_0_ID, 0.2F), LEVEL_0_ID, (dim, client, ci) -> {
		//}, DEFAULT, DEFAULT, DEFAULT, DEFAULT, null, NOISE_POINTS);
	}

	//@Override
	//public ChunkGenerator createGenerator(Registry<Biome> biomeRegistry, Registry<ChunkGeneratorSettings> chunkGeneratorSettingsRegistry, long seed) {
		//return new MazeChunkGenerator(4, 8, 100, BackroomsBlocks.PLASTERWALL.getDefaultState(), BackroomsBlocks.PLASTERWALL.getDefaultState(), ImmutableList.of(false, true, false, true, false), ImmutableList.of(false, false, true, true, true), ImmutableList.of(false, true, true, false, true), ImmutableList.of(false, true, false, true, true), BIOME_SOURCE_PRESET.getBiomeSource(biomeRegistry, seed), seed);
	//}

	//@Override
	public void init() {

	}



}
