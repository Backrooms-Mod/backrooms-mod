package com.kpabr.backrooms.world.chunk;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.init.BackroomsBlocks;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.floats.Float2LongRBTreeMap;
import net.ludocrypt.limlib.api.LiminalUtil;
import net.ludocrypt.limlib.api.world.AbstractNbtChunkGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.ChunkRegion;
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

public class LevelTwoChunkGenerator extends AbstractNbtChunkGenerator {
    // TODO: Discuss about BiomeSource, especially about BiomeSource in constructor,
    // TODO: because we know type of BiomeSource for every chunk generator

    public static final Codec<LevelTwoChunkGenerator> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source")
                            .stable()
                            .forGetter((chunkGenerator) -> chunkGenerator.biomeSource),
                    Codec.LONG.fieldOf("seed")
                            .stable()
                            .forGetter((chunkGenerator) -> chunkGenerator.worldSeed)
            ).apply(instance, instance.stable(LevelTwoChunkGenerator::new)));

    private final long worldSeed;
    public LevelTwoChunkGenerator(BiomeSource biomeSource, long worldSeed) {
        super(new SimpleRegistry<>(Registry.STRUCTURE_SET_KEY, Lifecycle.stable(), null), Optional.empty(), biomeSource, biomeSource, worldSeed, BackroomsMod.id("level_two"), LiminalUtil.createMultiNoiseSampler());
        this.worldSeed = worldSeed;
    }

    // Roof
    private final static int ROOF_Y = 44;
    private final static int FLOOR_Y = 38;

    @Override
    public CompletableFuture<Chunk> populateNoise(ChunkRegion region, ChunkStatus targetStatus, Executor executor, ServerWorld world, ChunkGenerator generator, StructureManager structureManager, ServerLightingProvider lightingProvider, Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function, List<Chunk> chunks, Chunk chunk, boolean bl) {
        final ChunkPos chunkPos = chunk.getPos();
        //Save the starting x and z position of the chunk. Note: positive x means east, positive z means south.
        final int startX = chunkPos.getStartX();
        final int startZ = chunkPos.getStartZ();

        // Getting chunk direction with pseudo random int for current chunk
        final Random random = new Random(region.getSeed() + MathHelper.hashCode(startX, 0, startZ));
        final Direction dir = Direction.fromHorizontal(random.nextInt(4));
        final boolean isDirEastOrWest = dir == Direction.WEST || dir == Direction.EAST;

        for(int y = FLOOR_Y + 1; y < ROOF_Y; y++) {
            generateRectZX(region, chunkPos, 16, 16, 0, 0, y, Blocks.LIGHT_GRAY_TERRACOTTA);
        }

        if(isDirEastOrWest) {
            generateRectZX(region, chunkPos, 16, 3, 0, 6, FLOOR_Y + 1, BackroomsBlocks.CUT_CEMENT);
            for(int y = FLOOR_Y + 2; y < ROOF_Y; y++) {
                generateRectZX(region, chunkPos, 16, 3, 0, 6, y);
            }
        } else {
            generateRectZX(region, chunkPos, 3, 16, 6, 0, FLOOR_Y + 1, BackroomsBlocks.CUT_CEMENT);
            for(int y = FLOOR_Y + 2; y < ROOF_Y; y++) {
                generateRectZX(region, chunkPos, 3, 16, 6, 0, y);
            }
        }


        if(isDirEastOrWest) {
            final ChunkPos northChunk = region.getChunk(chunkPos.x, chunkPos.z - 1).getPos();
            final ChunkPos southChunk = region.getChunk(chunkPos.x, chunkPos.z + 1).getPos();
            // Generate connection between this chunk and northern chunk if northern chunk direction is south or north
            if(!isChunkDirectionEastOrWest(region, northChunk)) {
                generateRectZX(region, chunkPos, 3, 6, 6, 0, FLOOR_Y + 1, BackroomsBlocks.CUT_CEMENT);
                for(int y = FLOOR_Y + 2; y < ROOF_Y; y++) {
                    generateRectZX(region, chunkPos, 3, 6, 6, 0, y);
                }
            }
            // Generate connection between this chunk and southern chunk if southern chunk direction is south or north
            if(!isChunkDirectionEastOrWest(region, southChunk)) {
                generateRectZX(region, chunkPos, 3, 7, 6, 9, FLOOR_Y + 1, BackroomsBlocks.CUT_CEMENT);
                for(int y = FLOOR_Y + 2; y < ROOF_Y; y++) {
                    generateRectZX(region, chunkPos, 3, 7, 6, 9, y);
                }
            }
        } else {
            final ChunkPos westChunk = region.getChunk(chunkPos.x - 1, chunkPos.z).getPos();
            final ChunkPos eastChunk = region.getChunk(chunkPos.x + 1, chunkPos.z).getPos();

            // Generate connection between this chunk and western chunk if western chunk direction is east or west
            if(isChunkDirectionEastOrWest(region, westChunk)) {
                generateRectZX(region, chunkPos, 6, 3, 0, 6, FLOOR_Y + 1, BackroomsBlocks.CUT_CEMENT);
                for(int y = FLOOR_Y + 2; y < ROOF_Y; y++) {
                    generateRectZX(region, chunkPos, 6, 3, 0, 6, y);
                }
            }
            // Generate connection between this chunk and eastern chunk if eastern chunk direction is east or west
            if(isChunkDirectionEastOrWest(region, eastChunk)) {
                generateRectZX(region, chunkPos, 7, 3, 9, 6, FLOOR_Y + 1, BackroomsBlocks.CUT_CEMENT);
                for(int y = FLOOR_Y + 2; y < ROOF_Y; y++) {
                    generateRectZX(region, chunkPos, 7, 3, 9, 6, y);
                }
            }
        }

        // Generate roof and floor of level with bedrock bricks.
        generateRectZX(region, chunkPos, 16, 16, 0, 0, FLOOR_Y, BackroomsBlocks.BEDROCK_BRICKS);
        generateRectZX(region, chunkPos, 16, 16, 0, 0, ROOF_Y, BackroomsBlocks.BEDROCK_BRICKS);

        return CompletableFuture.completedFuture(chunk);
    }

    private void generateRectZX(ChunkRegion region, ChunkPos chunk, int sizeX, int sizeZ, int alignX, int alignZ, int y) {
        generateRectZX(region, chunk, sizeX, sizeZ, alignX, alignZ, y, Blocks.AIR);
    }
    private void generateRectZX(ChunkRegion region, ChunkPos chunk, int sizeX, int sizeZ, int alignX, int alignZ, int y, final Block block) {
        for(int i = 0; i < sizeX; i++) {
            for(int j = 0; j < sizeZ; j++) {
                region.setBlockState(
                        new BlockPos(chunk.getStartX() + alignX + i, y, chunk.getStartZ() + alignZ + j),
                        block.getDefaultState(),
                        Block.FORCE_STATE,
                        0);
            }
        }
    }

    private boolean isChunkDirectionEastOrWest(ChunkRegion region, ChunkPos pos) {
        final Random random = new Random(region.getSeed() + MathHelper.hashCode(pos.getStartX(), 0, pos.getStartZ()));
        final Direction dir = Direction.fromHorizontal(random.nextInt(4));
        return dir == Direction.EAST || dir == Direction.WEST;
    }

    @Override
    protected Codec<LevelTwoChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public void storeStructures(ServerWorld world) {
    }

    @Override
    public ChunkGenerator withSeed(long seed) {
        return new LevelTwoChunkGenerator(this.biomeSource, seed);
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structureAccessor, Chunk chunk) {
        /*final ChunkPos chunkPos = chunk.getPos();
        final BlockPos biomePos = chunkPos.getBlockPos(4, 4, 4);

        // TODO: make roof begin variable
        // controls every block up to the roof
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 100; y++) {
                    // does a swap from the various stones to the custom blocks
                }
            }
        }*/
    }
}
