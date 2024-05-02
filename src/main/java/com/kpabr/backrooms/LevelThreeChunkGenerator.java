package com.kpabr.backrooms;

import com.kpabr.backrooms.util.ElectricalStationRoom;
import com.kpabr.backrooms.world.biome.sources.LevelThreeBiomeSource;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import com.kpabr.backrooms.config.BackroomsConfig;
import com.kpabr.backrooms.init.BackroomsLevels;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import com.kpabr.backrooms.BackroomsMod;
import net.ludocrypt.limlib.api.LiminalUtil;
import net.ludocrypt.limlib.api.world.AbstractNbtChunkGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import com.kpabr.backrooms.init.BackroomsBlocks;
import net.minecraft.loot.LootTables;
import net.minecraft.server.world.ChunkHolder.Unloaded;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil.MultiNoiseSampler;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.GenerationStep.Carver;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;

public class LevelThreeChunkGenerator extends ChunkGenerator {
    public static final Codec<LevelThreeChunkGenerator> CODEC = RecordCodecBuilder.create((instance) ->
			method_41042(instance).and(
				RegistryOps.createRegistryCodec(Registry.BIOME_KEY).forGetter((generator) -> generator.biomeRegistry)
			)
			.apply(instance, instance.stable(LevelThreeChunkGenerator::new))
	);

    private final Registry<Biome> biomeRegistry;
    private Random moldPlacementRandom;
    private long worldSeed;
    private static final int ROOF_BEGIN_Y = 6 * (getFloorCount() + 1) + 1;
    private static final BlockState ROOF_BLOCK = BackroomsBlocks.BEDROCK_BRICKS.getDefaultState();

    public LevelThreeChunkGenerator(Registry<StructureSet> registry, Registry<Biome> biomeRegistry) {
        super(registry, Optional.empty(), new LevelThreeBiomeSource(biomeRegistry));
        this.biomeRegistry = biomeRegistry;
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }
    @Override
    public ChunkGenerator withSeed(long seed) {
        return this;
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, StructureAccessor structureAccessor, Chunk chunk) {

        // IMPORTANT NOTE:
        // For biomes generation we're using various "placeholder" blocks to replace them later with blocks we actually need in biomes.
        // If you're adding new type of structure then don't use blocks other than described below from our mod!
        // Instead, use those blocks:
        // BackroomsBlocks.PATTERNED_WALLPAPER -> any wallpaper
        // BackroomsBlocks.WOOLEN_CARPET -> any carpet
        // BackroomsBlocks.CORK_TILE -> any cork tile
        // BackroomsBlocks.FLUORESCENT_LIGHT -> any light source
        // BackroomsBlocks.MOLDY_WOOLEN_CARPET -> random blocks(you can just replace them with carpet)

        final ChunkPos chunkPos = chunk.getPos();
        //Save the starting x and z position of the chunk. Note: positive x means east, positive z means south.
        final int startX = chunkPos.getStartX();
        final int startZ = chunkPos.getStartZ();
        final long seed = BackroomsLevels.LEVEL_3_WORLD.getSeed();
        final int roomHeight = getRoomHeight();

        //Create 5 floors, top to bottom.
        for (int y = getFloorCount(); y >= 0; y--) {
            final Random random = new Random(BackroomsLevels.LEVEL_3_WORLD.getSeed() + MathHelper.hashCode(startX, startZ, y));
            ElectricalStationRoom thisRoom = new ElectricalStationRoom(y, startX, startZ, seed);
            ElectricalStationRoom eastRoom = new ElectricalStationRoom(y, startX + 16, startZ, seed);
            ElectricalStationRoom westRoom = new ElectricalStationRoom(y, startX - 16, startZ, seed);
            ElectricalStationRoom southRoom = new ElectricalStationRoom(y, startX, startZ + 16, seed);
            ElectricalStationRoom northRoom = new ElectricalStationRoom(y, startX, startZ - 16, seed);
            for (int i = 0; i < roomHeight; i++) {
                for (int j = 0; j < 16; j++) {
                    for (int k = 0; k < 16; k++) {
                        if(i==0) {
                            if(((j & 1) + (k & 1)) == 1) {
                                chunk.setBlockState(new BlockPos(startX + j, 1 + roomHeight * y + i, startZ + k), Blocks.SMOOTH_STONE.getDefaultState(), false);
                            }
                            else{
                                chunk.setBlockState(new BlockPos(startX + j, 1 + roomHeight * y + i, startZ + k), Blocks.POLISHED_ANDESITE.getDefaultState(), false);
                            }
                        }
                        else if(i==1){
                            chunk.setBlockState(new BlockPos(startX + j, 1 + roomHeight * y + i, startZ + k), Blocks.BROWN_TERRACOTTA.getDefaultState(), false);
                        }
                        else if(i==roomHeight-1){
                            chunk.setBlockState(new BlockPos(startX + j, 1 + roomHeight * y + i, startZ + k), Blocks.DIORITE.getDefaultState(), false);
                        }
                        else{
                            if(random.nextInt(5) < 3){
                                chunk.setBlockState(new BlockPos(startX + j, 1 + roomHeight * y + i, startZ + k), Blocks.BRICKS.getDefaultState(), false);
                            }
                            else{
                                chunk.setBlockState(new BlockPos(startX + j, 1 + roomHeight * y + i, startZ + k), Blocks.TERRACOTTA.getDefaultState(), false);
                            }
                        }
                    }
                }
            }
            fillRoom(chunk, 4, y, startX, startZ, thisRoom);
            fillRoom(chunk, 3, y, startX, startZ, ElectricalStationRoom.hallwayBetween(thisRoom, eastRoom, Direction.EAST));
            fillRoom(chunk, 3, y, startX, startZ, ElectricalStationRoom.hallwayBetween(thisRoom, westRoom, Direction.WEST));
            fillRoom(chunk, 3, y, startX, startZ, ElectricalStationRoom.hallwayBetween(thisRoom, southRoom, Direction.SOUTH));
            fillRoom(chunk, 3, y, startX, startZ, ElectricalStationRoom.hallwayBetween(thisRoom, northRoom, Direction.NORTH));
        }
        // Place bedrock bricks at the bottom.
        for (int x = startX; x < startX + 16; x++) {
            for (int z = startZ; z < startZ + 16; z++) {
                chunk.setBlockState(new BlockPos(x, 0, z), ROOF_BLOCK, false);
            }
        }
        // Place bedrock bricks at the roof of chunk
        for (int x = startX; x < startX + 16; x++) {
            for (int z = startZ; z < startZ + 16; z++) {
                chunk.setBlockState(new BlockPos(x, ROOF_BEGIN_Y, z), ROOF_BLOCK, false);
            }
        }
        return CompletableFuture.completedFuture(chunk);
    }

    public static int getFloorCount() {
        return 5;
    }
    public static int getRoomHeight() {
        return 6;
    }

    public void storeStructures(ServerWorld world) {
        //store("backrooms_large", world, 0, 14); //Makes it so the large regular rooms can be used while generating.
        //store("backrooms_large_nofill", world, 1, 4); //Makes it so the large nofill rooms can be used while generating.
    }

    @Override
    public int getWorldHeight() {
        return 128;
    }

    @Override
    public int getHeight(int x, int y, Heightmap.Type type, HeightLimitView world) {
        return world.getTopY();
    }

    private void replace(Block block, Chunk chunk, BlockPos pos) {
        chunk.setBlockState(pos, block.getDefaultState(), false);
    }

    private void fillRoom(Chunk region, int height, int floor, int startX, int startZ, ElectricalStationRoom room){
        int roomHeight = getRoomHeight();
        for (int i = 0; i < height; i++) {
            for (int j = room.westWallX; j <= room.eastWallX; j++) {
                for (int k = room.northWallZ; k <= room.southWallZ; k++) {
                    region.setBlockState(new BlockPos(startX + j, 2 + roomHeight * floor + i, startZ + k), Blocks.AIR.getDefaultState(), false);
                }
            }
        }
    }
    private boolean isBiomeEquals(RegistryKey<Biome> biome, Chunk chunk, BlockPos biomePos) {
        return chunk.getBiomeForNoiseGen(biomePos.getX(), biomePos.getY(), biomePos.getZ()).matchesId(biome.getValue());
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, Chunk chunk) {}

    @Override
    public void carve(ChunkRegion chunkRegion, long seed, BiomeAccess biomeAccess, StructureAccessor structureAccessor,
            Chunk chunk, Carver generationStep) {}

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world) {
        return new VerticalBlockSample(0, new BlockState[0]);
    }

    @Override
    public void getDebugHudText(List<String> text, BlockPos pos) {}

    @Override
    public int getMinimumY() {
        return 0;
    }

    @Override
    public MultiNoiseSampler getMultiNoiseSampler() {
        return null;
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }

    @Override
    public void populateEntities(ChunkRegion region) {}

    
}
