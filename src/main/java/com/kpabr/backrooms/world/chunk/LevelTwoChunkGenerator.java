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
import net.minecraft.util.math.noise.PerlinNoiseSampler;
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
import net.minecraft.world.gen.random.SimpleRandom;

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
    private final PerlinNoiseSampler perlinNoise;
    private final static BlockState pipeNorthState = BackroomsBlocks.PIPE.getDefaultState().with(PipeBlock.EAST, false).with(PipeBlock.WEST, false).with(PipeBlock.UP, false).with(PipeBlock.DOWN, false);
    private final static BlockState pipeWestState = BackroomsBlocks.PIPE.getDefaultState().with(PipeBlock.SOUTH, false).with(PipeBlock.NORTH, false).with(PipeBlock.UP, false).with(PipeBlock.DOWN, false);
    public LevelTwoChunkGenerator(BiomeSource biomeSource, long worldSeed) {
        super(new SimpleRegistry<>(Registry.STRUCTURE_SET_KEY, Lifecycle.stable(), null), Optional.empty(), biomeSource, biomeSource, worldSeed, BackroomsMod.id("level_two"), LiminalUtil.createMultiNoiseSampler());
        this.worldSeed = worldSeed;
        perlinNoise = new PerlinNoiseSampler(new SimpleRandom(worldSeed));
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(ChunkRegion region, ChunkStatus targetStatus, Executor executor, ServerWorld world, ChunkGenerator generator, StructureManager structureManager, ServerLightingProvider lightingProvider, Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function, List<Chunk> chunks, Chunk chunk, boolean bl) {
        final ChunkPos pos = chunk.getPos();
        //Save the starting x and z position of the chunk. Note: positive x means east, positive z means south.
        final int startX = pos.getStartX();
        final int startZ = pos.getStartZ();

        // Getting chunk direction with pseudo random int for current chunk

        // Generate paths up to yMax * 16
        final double noise = perlinNoise.sample(startX, 0, startZ, 0, 40);
        BackroomsMod.LOGGER.info(Double.toString(noise));

        if (noise < 0) {
            fillRectZX(region, chunk, pos, 16, 4, 0, 6, 0, Blocks.STONE);
        } else {
            fillRectZX(region, chunk, pos, 4, 16, 6, 0, 0, Blocks.STONE);
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

    /**
     * Fabulously optimized setBlockState function, don't use it if you aren't sure,
     * that old block state is AIR and your block isn't BlockEntity
     */
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
