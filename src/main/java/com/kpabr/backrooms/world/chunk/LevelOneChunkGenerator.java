package com.kpabr.backrooms.world.chunk;


import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.config.BackroomsConfig;
import com.kpabr.backrooms.init.BackroomsBlocks;
import com.kpabr.backrooms.init.BackroomsLevels;
import com.kpabr.backrooms.world.chunk.level1chunkgenerators.*;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ludocrypt.limlib.api.LiminalUtil;
import net.ludocrypt.limlib.api.world.AbstractNbtChunkGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.loot.LootTables;
import net.minecraft.server.world.ChunkHolder.Unloaded;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
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
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

public class LevelOneChunkGenerator extends AbstractNbtChunkGenerator {
    public static final Codec<LevelOneChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(BiomeSource.CODEC.fieldOf("biome_source").stable().forGetter((chunkGenerator) -> {
            return chunkGenerator.biomeSource;
        }), Codec.LONG.fieldOf("seed").stable().forGetter((chunkGenerator) -> {
            return chunkGenerator.worldSeed;
        })).apply(instance, instance.stable(LevelOneChunkGenerator::new));
    });


    private final long worldSeed;
    private final CementHallsChunkGenerator cementHallsChunkGenerator;
    private final ParkingGarageChunkGenerator parkingGarageChunkGenerator;
    private final WarehouseChunkGenerator warehouseChunkGenerator;
    public LevelOneChunkGenerator(BiomeSource biomeSource, long worldSeed, CementHallsChunkGenerator cementHallsChunkGenerator, ParkingGarageChunkGenerator parkingGarageChunkGenerator, WarehouseChunkGenerator warehouseChunkGenerator) {
        super(new SimpleRegistry<StructureSet>(Registry.STRUCTURE_SET_KEY, Lifecycle.stable(), null), Optional.empty(), biomeSource, biomeSource, worldSeed, BackroomsMod.id("level_1"), LiminalUtil.createMultiNoiseSampler());
        this.worldSeed = worldSeed;
        this.cementHallsChunkGenerator = cementHallsChunkGenerator;
        this.parkingGarageChunkGenerator = parkingGarageChunkGenerator;
        this.warehouseChunkGenerator = warehouseChunkGenerator;
    }
    public LevelOneChunkGenerator(BiomeSource biomeSource, long worldSeed) {
        this(biomeSource, worldSeed, new CementHallsChunkGenerator(biomeSource, worldSeed),  new ParkingGarageChunkGenerator(biomeSource, worldSeed),  new WarehouseChunkGenerator(biomeSource, worldSeed));
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }
    @Override
    public ChunkGenerator withSeed(long seed) {
        return new LevelOneChunkGenerator(this.biomeSource, seed);
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(ChunkRegion region, ChunkStatus targetStatus, Executor executor, ServerWorld world, ChunkGenerator generator, StructureManager structureManager, ServerLightingProvider lightingProvider, Function<Chunk, CompletableFuture<Either<Chunk, Unloaded>>> function, List<Chunk> chunks, Chunk chunk, boolean bl) {

        // IMPORTANT NOTE:
        // For biomes generation we're using various "placeholder" blocks to replace them later with blocks we actually need in biomes.
        // If you're adding new type of structure then don't use blocks other than described below from our mod!
        // Instead, use those blocks:
        // BackroomsBlocks.PATTERNED_WALLPAPER -> any wallpaper
        // BackroomsBlocks.WOOLEN_CARPET -> any carpet
        // BackroomsBlocks.CORK_TILE -> any cork tile
        // BackroomsBlocks.FLUORESCENT_LIGHT -> any light source
        // BackroomsBlocks.MOLDY_WOOLEN_CARPET -> random blocks(you can just replace them with carpet)


        ChunkPos chunkPos = chunk.getPos();
        //Define a position for checking biomes
        BlockPos biomePos = chunkPos.getBlockPos(4, 4, 4);
        //Save the starting x and z position of the chunk. Note: positive x means east, positive z means south.
        int startX = chunkPos.getStartX();
        int startZ = chunkPos.getStartZ();
        //Define how many floors the level will have.
        int floorCount=getFloorCount();
        if(checkBiome(BackroomsLevels.CEMENT_WALLS_BIOME, chunk, biomePos)){
            this.cementHallsChunkGenerator.populateNoise(region, targetStatus, executor, world, generator, structureManager, lightingProvider, function, chunks, chunk, bl);
        }
        if(checkBiome(BackroomsLevels.PARKING_GARAGE_BIOME, chunk, biomePos)){
            this.parkingGarageChunkGenerator.populateNoise(region, targetStatus, executor, world, generator, structureManager, lightingProvider, function, chunks, chunk, bl);
        }
        if(checkBiome(BackroomsLevels.WAREHOUSE_BIOME, chunk, biomePos)){
            this.warehouseChunkGenerator.populateNoise(region, targetStatus, executor, world, generator, structureManager, lightingProvider, function, chunks, chunk, bl);
        }

        // Place bedrock bricks at the bottom.
        for (int x = startX; x < startX + 16; x++) {
            for (int z = startZ; z < startZ + 16; z++) {
                region.setBlockState(new BlockPos(x, 0, z), BackroomsBlocks.BEDROCK_BRICKS.getDefaultState(), Block.FORCE_STATE, 0);
            }
        }
        // Place bedrock bricks at the roof of chunk
        for (int x = startX; x < startX + 16; x++) {
            for (int z = startZ; z < startZ + 16; z++) { // 3 layers to be placed
                region.setBlockState(new BlockPos(x, 1 + 8 * (floorCount + 1), z), BackroomsBlocks.BEDROCK_BRICKS.getDefaultState(), Block.FORCE_STATE, 0);
                region.setBlockState(new BlockPos(x, 2 + 8 * (floorCount + 1), z), BackroomsBlocks.BEDROCK_BRICKS.getDefaultState(), Block.FORCE_STATE, 0);
                region.setBlockState(new BlockPos(x, 3 + 8 * (floorCount + 1), z), BackroomsBlocks.BEDROCK_BRICKS.getDefaultState(), Block.FORCE_STATE, 0);
            }
        }

        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public void storeStructures(ServerWorld world) {
<<<<<<< Updated upstream
        /*store("warehouse", world, 0, 5); //Makes it so the large regular rooms can be used while generating.
        store("cement_halls", world, 1, 3); //Makes it so the large nofill rooms can be used while generating.*/
=======
        this.cementHallsChunkGenerator.storeStructures(world);
>>>>>>> Stashed changes
    }

    @Override
    public int chunkRadius() {
        return 1;
    }

    @Override
    protected Identifier getBarrelLootTable() {
        return LootTables.SPAWN_BONUS_CHEST;
    }

    @Override
    public int getWorldHeight() {
        return 128;
    }

    @Override
    public int getHeight(int x, int y, Heightmap.Type type, HeightLimitView world) {
        return world.getTopY();
    }

    public static int getFloorCount() {
        return 5;
    }

    private void replace(Block block, Chunk chunk, BlockPos pos) {
        chunk.setBlockState(pos, block.getDefaultState(), false);
    }

    private boolean checkBiome(RegistryKey<Biome> biome, Chunk chunk, BlockPos biomePos) {
        return chunk.getBiomeForNoiseGen(biomePos.getX(), biomePos.getY(), biomePos.getZ()).matchesId(biome.getValue());
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structureAccessor, Chunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        BlockPos biomePos = chunkPos.getBlockPos(4, 4, 4);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < chunk.getHeight(); y++) {    // controls every block in the chunk
                    BlockPos pos = chunkPos.getBlockPos(x, y, z);
                    BlockState block = chunk.getBlockState(pos);

                    if (block == BackroomsBlocks.PATTERNED_WALLPAPER.getDefaultState()) {
                        replace(BackroomsBlocks.CEMENT_BRICKS, chunk, pos);
                    } else if (block == BackroomsBlocks.WOOLEN_CARPET.getDefaultState()) {
                        replace(BackroomsBlocks.CEMENT, chunk, pos);
                    } else if (block == BackroomsBlocks.MOLDY_WOOLEN_CARPET.getDefaultState()) {
                        replace(BackroomsBlocks.CEMENT, chunk, pos);
                    } else if (block == BackroomsBlocks.CORK_TILE.getDefaultState()) {
                        replace(BackroomsBlocks.CEMENT_TILES, chunk, pos);
                    } else if (block == BackroomsBlocks.MOLDY_CORK_TILE.getDefaultState()) {
                        replace(BackroomsBlocks.CEMENT_TILES, chunk, pos);
                    }
                }
            }
        }
    }
}
