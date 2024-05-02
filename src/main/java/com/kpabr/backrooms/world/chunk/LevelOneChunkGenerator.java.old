package com.kpabr.backrooms.world.chunk;


import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.init.BackroomsBlocks;
import com.kpabr.backrooms.init.BackroomsLevels;
import com.kpabr.backrooms.world.chunk.level1chunkgenerators.CementHallsChunkGenerator;
import com.kpabr.backrooms.world.chunk.level1chunkgenerators.ParkingGarageChunkGenerator;
import com.kpabr.backrooms.world.chunk.level1chunkgenerators.WarehouseChunkGenerator;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ludocrypt.limlib.api.LiminalUtil;
import net.ludocrypt.limlib.api.world.AbstractNbtChunkGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.loot.LootTables;
import net.minecraft.server.world.ChunkHolder.Unloaded;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

public class LevelOneChunkGenerator extends AbstractNbtChunkGenerator {

    public static final Codec<LevelOneChunkGenerator> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").stable().forGetter(
                            (chunkGenerator) -> chunkGenerator.biomeSource),
                    Codec.LONG.fieldOf("seed").stable().forGetter(
                            (chunkGenerator) -> chunkGenerator.worldSeed)
            ).apply(instance, instance.stable(LevelOneChunkGenerator::new))
    );

    private final long worldSeed;
    private final CementHallsChunkGenerator cementHallsChunkGenerator;
    private final ParkingGarageChunkGenerator parkingGarageChunkGenerator;
    private final WarehouseChunkGenerator warehouseChunkGenerator;
    private static final BlockState PATTERNED_WALLPAPER = BackroomsBlocks.PATTERNED_WALLPAPER.getDefaultState();
    private static final BlockState WOOLEN_CARPET = BackroomsBlocks.WOOLEN_CARPET.getDefaultState();
    private static final BlockState MOLDY_WOOLEN_CARPET = BackroomsBlocks.MOLDY_WOOLEN_CARPET.getDefaultState();
    private static final BlockState CORK_TILE = BackroomsBlocks.CORK_TILE.getDefaultState();
    private static final BlockState MOLDY_CORK_TILE = BackroomsBlocks.MOLDY_CORK_TILE.getDefaultState();
    private static final BlockState ROOF_BLOCK = BackroomsBlocks.BEDROCK_BRICKS.getDefaultState();
    //Define roof position on y coordinate.
    private static final int ROOF_BEGIN_Y = 8 * (getFloorCount() + 1) + 1;
    public LevelOneChunkGenerator(BiomeSource biomeSource, long worldSeed, CementHallsChunkGenerator cementHallsChunkGenerator, ParkingGarageChunkGenerator parkingGarageChunkGenerator, WarehouseChunkGenerator warehouseChunkGenerator) {
        super(new SimpleRegistry<>(Registry.STRUCTURE_SET_KEY, Lifecycle.stable(), null), Optional.empty(), biomeSource, biomeSource, worldSeed, BackroomsMod.id("level_1"), LiminalUtil.createMultiNoiseSampler());
        this.worldSeed = worldSeed;
        this.cementHallsChunkGenerator = cementHallsChunkGenerator;
        this.parkingGarageChunkGenerator = parkingGarageChunkGenerator;
        this.warehouseChunkGenerator = warehouseChunkGenerator;
    }
    public LevelOneChunkGenerator(BiomeSource biomeSource, long worldSeed) {
        this(biomeSource, worldSeed,
                new CementHallsChunkGenerator(biomeSource, worldSeed),
                new ParkingGarageChunkGenerator(biomeSource, worldSeed),
                new WarehouseChunkGenerator(biomeSource, worldSeed));
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

        final ChunkPos chunkPos = chunk.getPos();
        //Define a position for checking biomes
        final BlockPos biomePos = chunkPos.getBlockPos(0, 4, 4);

        //Save the first and last x and z position of the chunk. Note: positive x means east, positive z means south.
        final int startX = chunkPos.getStartX();
        final int endX = startX + 16;
        final int startZ = chunkPos.getStartZ();
        final int endZ = startZ  + 16;

        // if(isBiomeEquals(BackroomsLevels.CEMENT_WALLS_BIOME, chunk, biomePos)) {
        //     this.cementHallsChunkGenerator.populateNoise(region, targetStatus, executor, world, generator, structureManager, lightingProvider, function, chunks, chunk, bl);
        // }
        // else if(isBiomeEquals(BackroomsLevels.PARKING_GARAGE_BIOME, chunk, biomePos)) {
        //     this.parkingGarageChunkGenerator.populateNoise(region, targetStatus, executor, world, generator, structureManager, lightingProvider, function, chunks, chunk, bl);
        // }
        // else if(isBiomeEquals(BackroomsLevels.WAREHOUSE_BIOME, chunk, biomePos)) {
        //     this.warehouseChunkGenerator.populateNoise(region, targetStatus, executor, world, generator, structureManager, lightingProvider, function, chunks, chunk, bl);
        // }

        // Place bedrock bricks at the bottom.
        for (int x = startX; x < endX; x++) {
            for (int z = startZ; z < endZ; z++) {
                region.setBlockState(new BlockPos(x, 0, z), ROOF_BLOCK, Block.FORCE_STATE, 0);
            }
        }

        // Place bedrock bricks at the roof of chunk
        for (int x = startX; x < endX; x++) {
            // 3 layers to be placed
            for (int z = startZ; z < endZ; z++) {
                region.setBlockState(new BlockPos(x, ROOF_BEGIN_Y, z), ROOF_BLOCK, Block.FORCE_STATE, 0);
                region.setBlockState(new BlockPos(x, 1 + ROOF_BEGIN_Y, z), ROOF_BLOCK, Block.FORCE_STATE, 0);
                region.setBlockState(new BlockPos(x, 2 + ROOF_BEGIN_Y, z), ROOF_BLOCK, Block.FORCE_STATE, 0);
            }
        }

        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public int chunkRadius() {
        return 1;
    }

    @Override
    public void storeStructures(ServerWorld world) {
        this.parkingGarageChunkGenerator.storeStructures(world);
        this.cementHallsChunkGenerator.storeStructures(world);
        this.warehouseChunkGenerator.storeStructures(world);
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

    private static void replace(Block block, Chunk chunk, BlockPos pos) {
        chunk.setBlockState(pos, block.getDefaultState(), false);
    }

    private boolean isBiomeEquals(RegistryKey<Biome> biome, Chunk chunk, BlockPos biomePos) {
        return chunk.getBiomeForNoiseGen(biomePos.getX(), biomePos.getY(), biomePos.getZ()).matchesId(biome.getValue());
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structureAccessor, Chunk chunk) {
        final ChunkPos chunkPos = chunk.getPos();

        // controls every block up to the roof
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < ROOF_BEGIN_Y; y++) {
                    final BlockPos pos = chunkPos.getBlockPos(x, y, z);
                    final BlockState block = chunk.getBlockState(pos);

                    if (block == PATTERNED_WALLPAPER) {
                        replace(BackroomsBlocks.CEMENT_BRICKS, chunk, pos);
                    } else if (block == WOOLEN_CARPET || block == MOLDY_WOOLEN_CARPET) {
                        replace(BackroomsBlocks.CEMENT, chunk, pos);
                    } else if (block == CORK_TILE || block == MOLDY_CORK_TILE) {
                        replace(BackroomsBlocks.CEMENT_TILES, chunk, pos);
                    }
                }
            }
        }
    }
}
