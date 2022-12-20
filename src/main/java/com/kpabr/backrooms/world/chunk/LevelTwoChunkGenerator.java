package com.kpabr.backrooms.world.chunk;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.block.PipeBlock;
import com.kpabr.backrooms.init.BackroomsBlocks;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ludocrypt.limlib.api.LiminalUtil;
import net.ludocrypt.limlib.api.world.AbstractNbtChunkGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.nio.channels.Pipe;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

public class LevelTwoChunkGenerator extends AbstractNbtChunkGenerator {
    // TODO: Discuss BiomeSource, especially about BiomeSource in constructor,
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
    private final static int ROOF_Y = 45;
    private final static int FLOOR_Y = 38;
    private final static BlockState pipeNorthState = BackroomsBlocks.PIPE.getDefaultState().with(PipeBlock.EAST, false).with(PipeBlock.WEST, false).with(PipeBlock.UP, false).with(PipeBlock.DOWN, false);
    private final static BlockState pipeWestState = BackroomsBlocks.PIPE.getDefaultState().with(PipeBlock.SOUTH, false).with(PipeBlock.NORTH, false).with(PipeBlock.UP, false).with(PipeBlock.DOWN, false);
    public LevelTwoChunkGenerator(BiomeSource biomeSource, long worldSeed) {
        super(new SimpleRegistry<>(Registry.STRUCTURE_SET_KEY, Lifecycle.stable(), null), Optional.empty(), biomeSource, biomeSource, worldSeed, BackroomsMod.id("level_two"), LiminalUtil.createMultiNoiseSampler());
        this.worldSeed = worldSeed;
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(ChunkRegion region, ChunkStatus targetStatus, Executor executor, ServerWorld world, ChunkGenerator generator, StructureManager structureManager, ServerLightingProvider lightingProvider, Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function, List<Chunk> chunks, Chunk chunk, boolean bl) {
        final ChunkPos pos = chunk.getPos();
        //Save the starting x and z position of the chunk. Note: positive x means east, positive z means south.
        final int startX = pos.getStartX();
        final int startZ = pos.getStartZ();

        // Getting chunk direction with pseudo random int for current chunk
        final Random random = new Random(region.getSeed() + MathHelper.hashCode(startX, 0, startZ));
        final Direction dir = Direction.fromHorizontal(random.nextInt(4));
        final boolean isDirEastOrWest = dir == Direction.WEST || dir == Direction.EAST;

        // Fill roof and floor of level with bedrock bricks.
        fillRectZX(region, chunk, pos, 16, 16, 0, 0, FLOOR_Y, BackroomsBlocks.BEDROCK_BRICKS);
        fillRectZX(region, chunk, pos, 16, 16, 0, 0, ROOF_Y, BackroomsBlocks.BEDROCK_BRICKS);

        for(int y = FLOOR_Y + 1; y <= ROOF_Y - 1; y++) {
            fillRectZX(region, chunk, pos, 16, 16, 0, 0, y, Blocks.LIGHT_GRAY_TERRACOTTA);
        }

        // Generate main corridors
        if(isDirEastOrWest) {
            // generate real floor and roof
            fillRectZX(region, chunk, pos, 16, 3, 0, 6, FLOOR_Y + 1, BackroomsBlocks.CEMENT_TILES);
            fillRectZX(region, chunk, pos, 16, 3, 0, 6, ROOF_Y - 1, BackroomsBlocks.CEMENT_TILES);

            // Fill with air a corridor where player can move
            for(int y = FLOOR_Y + 2; y < ROOF_Y - 1; y++) {
                fillRectZX(region, chunk, pos, 16, 3, 0, 6, y);
            }
            fillRectZX(region, chunk, pos, 16, 1, 0, 6, ROOF_Y - 2, BackroomsBlocks.PIPE, pipeWestState);
        } else {
            fillRectZX(region, chunk, pos, 3, 16, 6, 0, FLOOR_Y + 1, BackroomsBlocks.CEMENT_TILES);
            fillRectZX(region, chunk, pos, 3, 16, 6, 0, ROOF_Y - 1, BackroomsBlocks.CEMENT_TILES);
            // Fill with air a corridor where  can move
            for(int y = FLOOR_Y + 2; y < ROOF_Y - 1; y++) {
                fillRectZX(region, chunk, pos, 3, 16, 6, 0, y);
            }
            fillRectZX(region, chunk, pos, 1, 16, 6, 0, ROOF_Y - 2, BackroomsBlocks.PIPE, pipeNorthState);
        }

        // Generate "bridges" between perpendicular corridors
        if(isDirEastOrWest) {
            final ChunkPos northChunk = region.getChunk(pos.x, pos.z - 1).getPos();
            final ChunkPos southChunk = region.getChunk(pos.x, pos.z + 1).getPos();
            // Generate connection between this chunk and northern chunk if northern chunk direction is south or north
            if(isChunkDirectionNorthOrSouth(region, northChunk)) {
                fillRectZX(region, chunk, pos, 3, 6, 6, 0, FLOOR_Y + 1, BackroomsBlocks.CEMENT_TILES);
                fillRectZX(region, chunk, pos, 3, 6, 6, 0, ROOF_Y - 1, BackroomsBlocks.CEMENT_TILES);
                // Fill with air a corridor where player can move
                for(int y = FLOOR_Y + 2; y < ROOF_Y - 1; y++) {
                    fillRectZX(region, chunk, pos, 3, 6, 7, 0, y);
                }
                fillRectZX(region, chunk, pos, 1, 6, 7, 0, ROOF_Y - 2, BackroomsBlocks.PIPE, pipeNorthState);
            }
            
            // Generate connection between this chunk and southern chunk if southern chunk direction is south or north
            if(isChunkDirectionNorthOrSouth(region, southChunk)) {
                fillRectZX(region, chunk, pos, 3, 7, 6, 9, FLOOR_Y + 1, BackroomsBlocks.CEMENT_TILES);
                fillRectZX(region, chunk, pos, 3, 7, 6, 9, ROOF_Y - 1, BackroomsBlocks.CEMENT_TILES);
                // Fill with air a corridor where player can move
                for(int y = FLOOR_Y + 2; y < ROOF_Y - 1; y++) {
                    fillRectZX(region, chunk, pos, 3, 7, 6, 9, y);
                }
                fillRectZX(region, chunk, pos, 1, 7, 6, 9, ROOF_Y - 2, BackroomsBlocks.PIPE, pipeNorthState);
            }
        } else {
            final ChunkPos westChunk = region.getChunk(pos.x - 1, pos.z).getPos();
            final ChunkPos eastChunk = region.getChunk(pos.x + 1, pos.z).getPos();

            // Generate connection between this chunk and western chunk if western chunk direction is east or west
            if(isChunkDirectionEastOrWest(region, westChunk)) {
                fillRectZX(region, chunk, pos, 6, 3, 0, 6, ROOF_Y - 1, BackroomsBlocks.CEMENT_TILES);
                fillRectZX(region, chunk, pos, 6, 3, 0, 6, FLOOR_Y + 1, BackroomsBlocks.CEMENT_TILES);
                // Fill with air a corridor where player can move
                for(int y = FLOOR_Y + 2; y < ROOF_Y - 1; y++) {
                    fillRectZX(region, chunk, pos, 7, 3, 0, 6, y);
                }
                fillRectZX(region, chunk, pos, 6, 1, 0, 6, ROOF_Y - 2, BackroomsBlocks.PIPE, pipeWestState);
            }
            // Generate connection between this chunk and eastern chunk if eastern chunk direction is east or west
            if(isChunkDirectionEastOrWest(region, eastChunk)) {
                fillRectZX(region, chunk, pos, 7, 3, 9, 6, FLOOR_Y + 1, BackroomsBlocks.CEMENT_TILES);
                fillRectZX(region, chunk, pos, 7, 3, 9, 6, ROOF_Y - 1, BackroomsBlocks.CEMENT_TILES);
                // Fill with air a corridor where player can move
                for(int y = FLOOR_Y + 2; y < ROOF_Y - 1; y++) {
                    fillRectZX(region, chunk, pos, 7, 3, 9, 6, y);
                }
                // Fill bridge with pipes
                fillRectZX(region, chunk, pos, 6, 1, 9, 6, ROOF_Y - 2, BackroomsBlocks.PIPE, pipeWestState);
            }
        }

        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public int getHeight(int var1, int var2, Heightmap.Type var3, HeightLimitView var4) {
        return ROOF_Y + 5;
    }

    private void fillRectZX(ChunkRegion region, Chunk chunk, ChunkPos pos, int sizeX, int sizeZ, int alignX, int alignZ, int y) {
        fillRectZX(region, chunk, pos, sizeX, sizeZ, alignX, alignZ, y, Blocks.AIR);
    }
    private void fillRectZX(ChunkRegion region, Chunk chunk, ChunkPos pos, int sizeX, int sizeZ, int alignX, int alignZ, int y, final Block block) {
        fillRectZX(region, chunk, pos, sizeX, sizeZ, alignX, alignZ, y, block, block.getDefaultState());
    }
    private void fillRectZX(ChunkRegion region, Chunk chunk, ChunkPos pos, int sizeX, int sizeZ, int alignX, int alignZ, int y, final Block block, BlockState state) {
        for(int i = 0; i < sizeX; i++) {
            for(int j = 0; j < sizeZ; j++) {
                setBlockState(region, chunk, state,
                        new BlockPos(pos.getStartX() + alignX + i, y, pos.getStartZ() + alignZ + j));
            }
        }
    }

    // Fabulously optimized setBlockState function
    private void setBlockState(ChunkRegion region, Chunk chunk, BlockState state, BlockPos pos) {
        final BlockState blockState = chunk.setBlockState(pos, state, false);
        region.toServerWorld().onBlockChanged(pos, blockState, state);
    }


    private boolean isChunkDirectionEastOrWest(ChunkRegion region, ChunkPos pos) {
        final Random random = new Random(region.getSeed() + MathHelper.hashCode(pos.getStartX(), 0, pos.getStartZ()));
        final Direction dir = Direction.fromHorizontal(random.nextInt(4));
        return dir == Direction.EAST || dir == Direction.WEST;
    }
    private boolean isChunkDirectionNorthOrSouth(ChunkRegion region, ChunkPos pos) {
        final Random random = new Random(region.getSeed() + MathHelper.hashCode(pos.getStartX(), 0, pos.getStartZ()));
        final Direction dir = Direction.fromHorizontal(random.nextInt(4));
        return dir == Direction.NORTH || dir == Direction.SOUTH;
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
    public int getMinimumY() {
        return 20;
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structureAccessor, Chunk chunk) {
        /*final pos pos = chunk.getPos();
        final BlockPos biomePos = pos.getBlockPos(4, 4, 4);

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
