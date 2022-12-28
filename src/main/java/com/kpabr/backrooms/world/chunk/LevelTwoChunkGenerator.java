package com.kpabr.backrooms.world.chunk;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.block.FiresaltCrystalBlock;
import com.kpabr.backrooms.block.FluorescentLightBlock;
import com.kpabr.backrooms.block.PipeBlock;
import com.kpabr.backrooms.init.BackroomsBlocks;
import com.kpabr.backrooms.init.BackroomsLevels;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ludocrypt.limlib.api.LiminalUtil;
import net.ludocrypt.limlib.api.world.AbstractNbtChunkGenerator;
import net.minecraft.block.*;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
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
import net.minecraft.world.gen.random.AtomicSimpleRandom;

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
    private final static int ROOF_Y = 15;
    private final static int FLOOR_Y = 1;
    private final AtomicSimpleRandom random;
    private final static BlockState fluorescentLightOn = BackroomsBlocks.FLUORESCENT_LIGHT.getDefaultState().with(FluorescentLightBlock.LIT, true);
    private final static BlockState fluorescentLightOff = BackroomsBlocks.FLUORESCENT_LIGHT.getDefaultState().with(FluorescentLightBlock.LIT, false);
    private final static BlockState ironBarsNorthSouthState = Blocks.IRON_BARS.getDefaultState()
            .with(PaneBlock.EAST, true)
            .with(PaneBlock.NORTH, true)
            .with(PaneBlock.SOUTH, true);
    private final static BlockState ironBarsWestEastState = Blocks.IRON_BARS.getDefaultState()
            .with(PaneBlock.WEST, true)
            .with(PaneBlock.EAST, true)
            .with(PaneBlock.SOUTH, true);
    private final static BlockState ironTrapdoorState = Blocks.IRON_TRAPDOOR.getDefaultState().with(TrapdoorBlock.HALF, BlockHalf.TOP);
    private final static BlockState firesaltWestWallState = BackroomsBlocks.FIRESALT_CRYSTAL.getDefaultState()
            .with(FiresaltCrystalBlock.FACING, Direction.EAST);
    private final static BlockState firesaltNorthWallState = BackroomsBlocks.FIRESALT_CRYSTAL.getDefaultState()
            .with(FiresaltCrystalBlock.FACING, Direction.SOUTH);
    private final static BlockState pipeNorthSouthState = BackroomsBlocks.PIPE.getDefaultState()
            .with(PipeBlock.EAST, false)
            .with(PipeBlock.WEST, false)
            .with(PipeBlock.UP, false)
            .with(PipeBlock.DOWN, false)
            .with(PipeBlock.NORTH, true)
            .with(PipeBlock.SOUTH, true);
    private final static BlockState pipeWestEastState = BackroomsBlocks.PIPE.getDefaultState()
            .with(PipeBlock.EAST, true)
            .with(PipeBlock.WEST, true)
            .with(PipeBlock.UP, false)
            .with(PipeBlock.DOWN, false)
            .with(PipeBlock.NORTH, false)
            .with(PipeBlock.SOUTH, false);
    public LevelTwoChunkGenerator(BiomeSource biomeSource, long worldSeed) {
        super(new SimpleRegistry<>(Registry.STRUCTURE_SET_KEY, Lifecycle.stable(), null), Optional.empty(), biomeSource, biomeSource, worldSeed, BackroomsMod.id("level_2"), LiminalUtil.createMultiNoiseSampler());
        this.worldSeed = worldSeed;
        random = new AtomicSimpleRandom(worldSeed);
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(ChunkRegion region, ChunkStatus targetStatus, Executor executor, ServerWorld world, ChunkGenerator generator, StructureManager structureManager, ServerLightingProvider lightingProvider, Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function, List<Chunk> chunks, Chunk chunk, boolean bl) {
        final ChunkPos pos = chunk.getPos();

        final var biome = getBiome(chunk);
        if(isPipesBiome(biome)) {
            final boolean isCold = biome.matchesKey(BackroomsLevels.COLD_PIPES_BIOME);
            final boolean isHot = biome.matchesKey(BackroomsLevels.HOT_PIPES_BIOME);
            Block wallBlock = Blocks.LIGHT_GRAY_TERRACOTTA;
            if(isCold) wallBlock = Blocks.CYAN_TERRACOTTA;
            else if(isHot) wallBlock = Blocks.RED_TERRACOTTA;

            final boolean isClosestToSpawnEastWestChunkCorridor;
            final boolean isClosestToSpawnNorthSouthChunkCorridor;
            final var eastWestChunkBiome = getBiome(region.getChunk(pos.x - 1, pos.z));
            isClosestToSpawnEastWestChunkCorridor = isPipesBiome(eastWestChunkBiome);
            final var northSouthChunkBiome = getBiome(region.getChunk(pos.x, pos.z - 1));
            isClosestToSpawnNorthSouthChunkCorridor = isPipesBiome(northSouthChunkBiome);

            if (isClosestToSpawnEastWestChunkCorridor && isClosestToSpawnNorthSouthChunkCorridor) {
                // Generate intersection between two corridors
                if((eastWestChunkBiome.matchesKey(BackroomsLevels.COLD_PIPES_BIOME) || eastWestChunkBiome.matchesKey(BackroomsLevels.COLD_PIPES_BIOME)) && random.nextBetween(0, 8) == 1) {
                    generateNbt(region, new BlockPos(pos.getStartX(), FLOOR_Y + 1, pos.getStartZ()), "cold_corridors_intersection");
                } else if((eastWestChunkBiome.matchesKey(BackroomsLevels.HOT_PIPES_BIOME) || eastWestChunkBiome.matchesKey(BackroomsLevels.HOT_PIPES_BIOME)) && random.nextBetween(0, 15) == 1) {
                    generateNbt(region, new BlockPos(pos.getStartX(), FLOOR_Y + 1, pos.getStartZ()), "hot_corridors_intersection");
                } else {
                    generateNbt(region, new BlockPos(pos.getStartX(), FLOOR_Y + 1, pos.getStartZ()), "corridors_intersection");
                }

                // Generate 4 random lights
                fillRectZX(region, chunk, pos, 1, 2, 4, 7, FLOOR_Y + 7,
                        random.nextBoolean() ? fluorescentLightOn : fluorescentLightOff);
                fillRectZX(region, chunk, pos, 1, 2, 11, 7, FLOOR_Y + 7,
                        random.nextBoolean() ? fluorescentLightOn : fluorescentLightOff);
                fillRectZX(region, chunk, pos, 2, 1, 7, 4, FLOOR_Y + 7,
                        random.nextBoolean() ? fluorescentLightOn : fluorescentLightOff);
                fillRectZX(region, chunk, pos, 2, 1, 7, 11, FLOOR_Y + 7,
                        random.nextBoolean() ? fluorescentLightOn : fluorescentLightOff);

                // Fill empty space with light gray terracotta
                for (int i = 1; i <= 7; i++) {
                    fillRectZX(region, chunk, pos, 3, 5, 0, 0, FLOOR_Y + i, Blocks.LIGHT_GRAY_TERRACOTTA);
                    fillRectZX(region, chunk, pos, 2, 3, 3, 0, FLOOR_Y + i, Blocks.LIGHT_GRAY_TERRACOTTA);
                }
                for (int i = 1; i <= 7; i++) {
                    fillRectZX(region, chunk, pos, 3, 5, 13, 0, FLOOR_Y + i, Blocks.LIGHT_GRAY_TERRACOTTA);
                    fillRectZX(region, chunk, pos, 2, 3, 11, 0, FLOOR_Y + i, Blocks.LIGHT_GRAY_TERRACOTTA);
                }
                for (int i = 1; i <= 7; i++) {
                    fillRectZX(region, chunk, pos, 3, 5, 0, 11, FLOOR_Y + i, Blocks.LIGHT_GRAY_TERRACOTTA);
                    fillRectZX(region, chunk, pos, 2, 3, 3, 13, FLOOR_Y + i, Blocks.LIGHT_GRAY_TERRACOTTA);
                }
                for (int i = 1; i <= 7; i++) {
                    fillRectZX(region, chunk, pos, 3, 5, 13, 11, FLOOR_Y + i, Blocks.LIGHT_GRAY_TERRACOTTA);
                    fillRectZX(region, chunk, pos, 2, 3, 11, 13, FLOOR_Y + i, Blocks.LIGHT_GRAY_TERRACOTTA);
                }

            }
            else if (isClosestToSpawnEastWestChunkCorridor) {
                for (int i = 1; i <= 2; i++) {
                    fillRectZX(region, chunk, pos, 16, 1, 0, 5 * i, FLOOR_Y + 1, BackroomsBlocks.CEMENT);
                    fillRectZX(region, chunk, pos, 16, 1, 0, 5 * i, FLOOR_Y + 2, BackroomsBlocks.CEMENT);
                    fillRectZX(region, chunk, pos, 16, 1, 0, 5 * i, FLOOR_Y + 3, BackroomsBlocks.CEMENT);
                    fillRectZX(region, chunk, pos, 16, 1, 0, 5 * i, FLOOR_Y + 4, wallBlock);
                    fillRectZX(region, chunk, pos, 16, 1, 0, 5 * i, FLOOR_Y + 5, wallBlock);
                    fillRectZX(region, chunk, pos, 16, 1, 0, 5 * i, FLOOR_Y + 6, wallBlock);
                }

                // Generate left side of corridor(from south direction)
                fillRectZX(region, chunk, pos, 16, 1, 0, 6, FLOOR_Y + 2, BackroomsBlocks.CEMENT);
                fillRectZX(region, chunk, pos, 16, 1, 0, 6, FLOOR_Y + 3, BackroomsBlocks.CEMENT_SLAB);

                fillRectZX(region, chunk, pos, 16, 1, 0, 6, FLOOR_Y + 4, pipeWestEastState);
                fillRectZX(region, chunk, pos, 16, 1, 0, 6, FLOOR_Y + 6, pipeWestEastState);

                // Generate right side of corridor(from south direction)
                fillRectZX(region, chunk, pos, 12, 1, 0, 9, FLOOR_Y + 2, ironBarsWestEastState);
                fillRectZX(region, chunk, pos, 12, 1, 0, 9, FLOOR_Y + 3, pipeWestEastState);

                fillRectZX(region, chunk, pos, 2, 1, 14, 9, FLOOR_Y + 2, ironBarsWestEastState);
                fillRectZX(region, chunk, pos, 2, 1, 14, 9, FLOOR_Y + 3, pipeWestEastState);

                fillRectZX(region, chunk, pos, 12, 1, 0, 9, FLOOR_Y + 6, pipeWestEastState);
                fillRectZX(region, chunk, pos, 2, 1, 14, 9, FLOOR_Y + 6, pipeWestEastState);

                fillRectZX(region, chunk, pos, 2, 1, 12, 9, FLOOR_Y + 2, BackroomsBlocks.CEMENT);
                fillRectZX(region, chunk, pos, 2, 1, 12, 9, FLOOR_Y + 3, BackroomsBlocks.CEMENT);
                fillRectZX(region, chunk, pos, 2, 1, 12, 9, FLOOR_Y + 4, BackroomsBlocks.CEMENT_PILLAR);
                fillRectZX(region, chunk, pos, 2, 1, 12, 9, FLOOR_Y + 5, BackroomsBlocks.CEMENT_PILLAR);
                fillRectZX(region, chunk, pos, 2, 1, 12, 9, FLOOR_Y + 6, BackroomsBlocks.CEMENT_PILLAR);

                fillRectZX(region, chunk, pos, 2, 1, 14, 9, FLOOR_Y + 2, ironBarsWestEastState);
                fillRectZX(region, chunk, pos, 2, 1, 14, 9, FLOOR_Y + 3, pipeWestEastState);

                // Generate first part(3x16) of roof
                fillRectZX(region, chunk, pos, 16, 2, 0, 5, FLOOR_Y + 7, BackroomsBlocks.CEMENT);

                // Generate lights on roof
                fillRectZX(region, chunk, pos, 3, 1, 0, 7, FLOOR_Y + 7, BackroomsBlocks.CEMENT);

                fillRectZX(region, chunk, pos, 3, 1, 2, 7, FLOOR_Y + 7,
                        random.nextBoolean() ? fluorescentLightOn : fluorescentLightOff);
                fillRectZX(region, chunk, pos, 3, 1, 2, 7, FLOOR_Y + 6, ironTrapdoorState);
                fillRectZX(region, chunk, pos, 6, 1, 5, 7, FLOOR_Y + 7, BackroomsBlocks.CEMENT);

                fillRectZX(region, chunk, pos, 3, 1, 11, 7, FLOOR_Y + 7,
                        random.nextBoolean() ? fluorescentLightOn : fluorescentLightOff);
                fillRectZX(region, chunk, pos, 3, 1, 11, 7, FLOOR_Y + 6, ironTrapdoorState);
                fillRectZX(region, chunk, pos, 2, 1, 14, 7, FLOOR_Y + 7, BackroomsBlocks.CEMENT);

                // Generate last part(3x16) of roof
                fillRectZX(region, chunk, pos, 16, 3, 0, 8, FLOOR_Y + 7, BackroomsBlocks.CEMENT);

                // Generate floor
                for (int x = 0; x < 16; x += 4) {
                    fillRectZX(region, chunk, pos, 2, 2, x, 6, FLOOR_Y + 1, BackroomsBlocks.CEMENT_TILES);
                }
                for (int x = 2; x < 16; x += 4) {
                    fillRectZX(region, chunk, pos, 2, 2, x, 6, FLOOR_Y + 1, Blocks.CYAN_TERRACOTTA);
                }

                for (int x = 0; x < 16; x += 4) {
                    fillRectZX(region, chunk, pos, 2, 2, x, 8, FLOOR_Y + 1, Blocks.CYAN_TERRACOTTA);
                }
                for (int x = 2; x < 16; x += 4) {
                    fillRectZX(region, chunk, pos, 2, 2, x, 8, FLOOR_Y + 1, BackroomsBlocks.CEMENT_TILES);
                }

                // Generate hot and cold pipes biomes features(magma and ice)
                if(isCold) {
                    // Generate on floor
                    for (int z = 7; z <= 9; z++) {
                        for (int x = 0; x < 16; x++) {
                            if(random.nextBetween(0, 5) == 1) {
                                setBlock(region, chunk, x, z, FLOOR_Y + 1, Blocks.PACKED_ICE);
                            }
                        }
                    }
                    // Generate on wall
                    for (int x = 0; x < 16; x++) {
                        if(random.nextBetween(0, 5) == 1) {
                            setBlock(region, chunk, x, 5, FLOOR_Y + 5, Blocks.PACKED_ICE);
                        }
                    }
                    // Generate on roof
                    for (int z = 7; z <= 9; z++) {
                        for (int x = 0; x < 16; x++) {
                            if(random.nextBetween(0, 8) == 1) {
                                setBlock(region, chunk, x, z, FLOOR_Y + 7, Blocks.PACKED_ICE);
                            }
                        }
                    }
                } else if(isHot) {
                    // Generate on floor
                    for (int z = 7; z <= 9; z++) {
                        for (int x = 0; x < 16; x++) {
                            if(random.nextBetween(0, 8) == 1) {
                                setBlock(region, chunk, x, z, FLOOR_Y + 1, Blocks.MAGMA_BLOCK);
                            }
                        }
                    }
                    // Generate on wall
                    for (int x = 0; x < 16; x++) {
                        if(random.nextBetween(0, 10) == 1) {
                            setBlock(region, chunk, x, 5, FLOOR_Y + 5, Blocks.MAGMA_BLOCK);
                            if(random.nextBetween(0, 4) == 1) {
                                setBlock(region, chunk, x, 6, FLOOR_Y + 5, firesaltNorthWallState);
                            }
                        }
                    }
                    // Generate on roof
                    for (int z = 7; z <= 9; z++) {
                        for (int x = 0; x < 16; x++) {
                            if(random.nextBetween(0, 8) == 1) {
                                setBlock(region, chunk, x, z, FLOOR_Y + 7, Blocks.MAGMA_BLOCK);
                            }
                        }
                    }
                }

                // Generate pipes upon corridor
                for (int x = 0; x <= 1; x++) {
                    if(random.nextBetween(0, 6) == 1) {
                        setBlock(region, chunk, x, 6, FLOOR_Y + 6, pipeWestEastState.with(PipeBlock.SOUTH, true));
                        setBlock(region, chunk, x, 7, FLOOR_Y + 6, pipeNorthSouthState);
                        setBlock(region, chunk, x, 8, FLOOR_Y + 6, pipeNorthSouthState);
                        setBlock(region, chunk, x, 9, FLOOR_Y + 6, pipeWestEastState.with(PipeBlock.NORTH, true));
                    }
                }
                for (int x = 5; x <= 10; x++) {
                    if(random.nextBetween(0, 6) == 1) {
                        setBlock(region, chunk, x, 6, FLOOR_Y + 6, pipeWestEastState.with(PipeBlock.SOUTH, true));
                        setBlock(region, chunk, x, 7, FLOOR_Y + 6, pipeNorthSouthState);
                        setBlock(region, chunk, x, 8, FLOOR_Y + 6, pipeNorthSouthState);
                        setBlock(region, chunk, x, 9, FLOOR_Y + 6, pipeWestEastState.with(PipeBlock.NORTH, true));
                    }
                }
                for (int x = 14; x <= 15; x++) {
                    if(random.nextBetween(0, 6) == 1) {
                        setBlock(region, chunk, x, 6, FLOOR_Y + 6, pipeWestEastState.with(PipeBlock.SOUTH, true));
                        setBlock(region, chunk, x, 7, FLOOR_Y + 6, pipeNorthSouthState);
                        setBlock(region, chunk, x, 8, FLOOR_Y + 6, pipeNorthSouthState);
                        setBlock(region, chunk, x, 9, FLOOR_Y + 6, pipeWestEastState.with(PipeBlock.NORTH, true));
                    }
                }

                // Fill empty space with light gray terracotta
                for (int i = 1; i <= 7; i++) {
                    fillRectZX(region, chunk, pos, 16, 5, 0, 0, FLOOR_Y + i, Blocks.LIGHT_GRAY_TERRACOTTA);
                    fillRectZX(region, chunk, pos, 16, 5, 0, 11, FLOOR_Y + i, Blocks.LIGHT_GRAY_TERRACOTTA);
                }
            }
            else if (isClosestToSpawnNorthSouthChunkCorridor) {
                // Generate walls
                for (int i = 1; i <= 2; i++) {
                    fillRectZX(region, chunk, pos, 1, 16, 5 * i, 0, FLOOR_Y + 1, BackroomsBlocks.CEMENT);
                    fillRectZX(region, chunk, pos, 1, 16, 5 * i, 0, FLOOR_Y + 2, BackroomsBlocks.CEMENT);
                    fillRectZX(region, chunk, pos, 1, 16, 5 * i, 0, FLOOR_Y + 3, BackroomsBlocks.CEMENT);
                    fillRectZX(region, chunk, pos, 1, 16, 5 * i, 0, FLOOR_Y + 4, wallBlock);
                    fillRectZX(region, chunk, pos, 1, 16, 5 * i, 0, FLOOR_Y + 5, wallBlock);
                    fillRectZX(region, chunk, pos, 1, 16, 5 * i, 0, FLOOR_Y + 6, wallBlock);
                }

                // Generate left side of corridor(from south direction)
                fillRectZX(region, chunk, pos, 1, 16, 6, 0, FLOOR_Y + 2, BackroomsBlocks.CEMENT);
                fillRectZX(region, chunk, pos, 1, 16, 6, 0, FLOOR_Y + 3, BackroomsBlocks.CEMENT_SLAB);

                fillRectZX(region, chunk, pos, 1, 16, 6, 0, FLOOR_Y + 4, pipeNorthSouthState);
                fillRectZX(region, chunk, pos, 1, 16, 6, 0, FLOOR_Y + 6, pipeNorthSouthState);

                // Generate right side of corridor(from south direction)
                fillRectZX(region, chunk, pos, 1, 12, 9, 0, FLOOR_Y + 2, ironBarsNorthSouthState);
                fillRectZX(region, chunk, pos, 1, 12, 9, 0, FLOOR_Y + 3, pipeNorthSouthState);

                fillRectZX(region, chunk, pos, 1, 2, 9, 14, FLOOR_Y + 2, ironBarsNorthSouthState);
                fillRectZX(region, chunk, pos, 1, 2, 9, 14, FLOOR_Y + 3, pipeNorthSouthState);

                fillRectZX(region, chunk, pos, 1, 12, 9, 0, FLOOR_Y + 6, pipeNorthSouthState);
                fillRectZX(region, chunk, pos, 1, 2, 9, 14, FLOOR_Y + 6, pipeNorthSouthState);

                fillRectZX(region, chunk, pos, 1, 2, 9, 12, FLOOR_Y + 2, BackroomsBlocks.CEMENT);
                fillRectZX(region, chunk, pos, 1, 2, 9, 12, FLOOR_Y + 3, BackroomsBlocks.CEMENT);
                fillRectZX(region, chunk, pos, 1, 2, 9, 12, FLOOR_Y + 4, BackroomsBlocks.CEMENT_PILLAR);
                fillRectZX(region, chunk, pos, 1, 2, 9, 12, FLOOR_Y + 5, BackroomsBlocks.CEMENT_PILLAR);
                fillRectZX(region, chunk, pos, 1, 2, 9, 12, FLOOR_Y + 6, BackroomsBlocks.CEMENT_PILLAR);

                fillRectZX(region, chunk, pos, 1, 2, 9, 14, FLOOR_Y + 2, ironBarsNorthSouthState);
                fillRectZX(region, chunk, pos, 1, 2, 9, 14, FLOOR_Y + 3, pipeNorthSouthState);

                // Generate first part(3x16) of roof
                fillRectZX(region, chunk, pos, 2, 16, 5, 0, FLOOR_Y + 7, BackroomsBlocks.CEMENT);

                // Generate lights on roof
                fillRectZX(region, chunk, pos, 1, 3, 7, 0, FLOOR_Y + 7, BackroomsBlocks.CEMENT);

                fillRectZX(region, chunk, pos, 1, 3, 7, 2, FLOOR_Y + 7,
                        random.nextBoolean() ? fluorescentLightOn : fluorescentLightOff);
                fillRectZX(region, chunk, pos, 1, 3, 7, 2, FLOOR_Y + 6, ironTrapdoorState);
                fillRectZX(region, chunk, pos, 1, 6, 7, 5, FLOOR_Y + 7, BackroomsBlocks.CEMENT);

                fillRectZX(region, chunk, pos, 1, 3, 7, 11, FLOOR_Y + 7,
                        random.nextBoolean() ? fluorescentLightOn : fluorescentLightOff);
                fillRectZX(region, chunk, pos, 1, 3, 7, 11, FLOOR_Y + 6, ironTrapdoorState);
                fillRectZX(region, chunk, pos, 1, 2, 7, 14, FLOOR_Y + 7, BackroomsBlocks.CEMENT);

                // Generate last part(3x16) of roof
                fillRectZX(region, chunk, pos, 3, 16, 8, 0, FLOOR_Y + 7, BackroomsBlocks.CEMENT);

                // Generate floor
                for (int z = 0; z < 16; z += 4) {
                    fillRectZX(region, chunk, pos, 2, 2, 6, z, FLOOR_Y + 1, BackroomsBlocks.CEMENT_TILES);
                }
                for (int z = 2; z < 16; z += 4) {
                    fillRectZX(region, chunk, pos, 2, 2, 6, z, FLOOR_Y + 1, Blocks.CYAN_TERRACOTTA);
                }

                for (int z = 0; z < 16; z += 4) {
                    fillRectZX(region, chunk, pos, 2, 2, 8, z, FLOOR_Y + 1, Blocks.CYAN_TERRACOTTA);
                }
                for (int z = 2; z < 16; z += 4) {
                    fillRectZX(region, chunk, pos, 2, 2, 8, z, FLOOR_Y + 1, BackroomsBlocks.CEMENT_TILES);
                }
                // Generate hot and cold pipes biomes features(magma and ice)
                if(isCold) {
                    // Generate on floor
                    for (int x = 7; x <= 9; x++) {
                        for (int z = 0; z < 16; z++) {
                            if(random.nextBetween(0, 5) == 1) {
                                setBlock(region, chunk, x, z, FLOOR_Y + 1, Blocks.PACKED_ICE);
                            }
                        }
                    }
                    // Generate on wall
                    for (int z = 0; z < 16; z++) {
                        if(random.nextBetween(0, 5) == 1) {
                            setBlock(region, chunk, 5, z, FLOOR_Y + 5, Blocks.PACKED_ICE);
                        }
                    }
                    // Generate on roof
                    for (int x = 7; x <= 9; x++) {
                        for (int z = 0; z < 16; z++) {
                            if(random.nextBetween(0, 8) == 1) {
                                setBlock(region, chunk, x, z, FLOOR_Y + 7, Blocks.PACKED_ICE);
                            }
                        }
                    }
                } else if(isHot) {
                    // Generate on floor
                    for (int x = 7; x <= 9; x++) {
                        for (int z = 0; z < 16; z++) {
                            if(random.nextBetween(0, 8) == 1) {
                                setBlock(region, chunk, x, z, FLOOR_Y + 1, Blocks.MAGMA_BLOCK);
                            }
                        }
                    }
                    // Generate on wall
                    for (int z = 0; z < 16; z++) {
                        if(random.nextBetween(0, 10) == 1) {
                            setBlock(region, chunk, 5, z, FLOOR_Y + 5, Blocks.MAGMA_BLOCK);
                            if(random.nextBetween(0, 4) == 1) {
                                setBlock(region, chunk, 6, z, FLOOR_Y + 5, firesaltWestWallState);
                            }
                        }
                    }
                    // Generate on roof
                    for (int x = 7; x <= 9; x++) {
                        for (int z = 0; z < 16; z++) {
                            if(random.nextBetween(0, 8) == 1) {
                                setBlock(region, chunk, x, z, FLOOR_Y + 7, Blocks.MAGMA_BLOCK);
                            }
                        }
                    }
                }

                // Generate pipes upon corridor
                for (int z = 0; z <= 1; z++) {
                    if(random.nextBetween(0, 6) == 1) {
                        setBlock(region, chunk, 6, z, FLOOR_Y + 6, pipeNorthSouthState.with(PipeBlock.EAST, true));
                        setBlock(region, chunk, 7, z, FLOOR_Y + 6, pipeWestEastState);
                        setBlock(region, chunk, 8, z, FLOOR_Y + 6, pipeWestEastState);
                        setBlock(region, chunk, 9, z, FLOOR_Y + 6, pipeNorthSouthState.with(PipeBlock.WEST, true));
                    }
                }
                for (int z = 5; z <= 10; z++) {
                    if(random.nextBetween(0, 6) == 1) {
                        setBlock(region, chunk, 6, z, FLOOR_Y + 6, pipeNorthSouthState.with(PipeBlock.EAST, true));
                        setBlock(region, chunk, 7, z, FLOOR_Y + 6, pipeWestEastState);
                        setBlock(region, chunk, 8, z, FLOOR_Y + 6, pipeWestEastState);
                        setBlock(region, chunk, 9, z, FLOOR_Y + 6, pipeNorthSouthState.with(PipeBlock.WEST, true));
                    }
                }
                for (int z = 14; z <= 15; z++) {
                    if(random.nextBetween(0, 6) == 1) {
                        setBlock(region, chunk, 6, z, FLOOR_Y + 6, pipeNorthSouthState.with(PipeBlock.EAST, true));
                        setBlock(region, chunk, 7, z, FLOOR_Y + 6, pipeWestEastState);
                        setBlock(region, chunk, 8, z, FLOOR_Y + 6, pipeWestEastState);
                        setBlock(region, chunk, 9, z, FLOOR_Y + 6, pipeNorthSouthState.with(PipeBlock.WEST, true));
                    }
                }

                // Fill empty space with light gray terracotta
                for (int i = 1; i <= 7; i++) {
                    fillRectZX(region, chunk, pos, 5, 16, 0, 0, FLOOR_Y + i, Blocks.LIGHT_GRAY_TERRACOTTA);
                    fillRectZX(region, chunk, pos, 5, 16, 11, 0, FLOOR_Y + i, Blocks.LIGHT_GRAY_TERRACOTTA);
                }
            }
        }
        else {
            for(int i = 1; i <= 7; i++) {
                fillRectZX(region, chunk, pos, 16, 16, 0, 0, FLOOR_Y + i, Blocks.LIGHT_GRAY_TERRACOTTA);
            }
        }
        fillRectZX(region, chunk, pos, 16, 16, 0, 0, FLOOR_Y, BackroomsBlocks.BEDROCK_BRICKS);
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public void storeStructures(ServerWorld world) {
        store("corridors_intersection", world);
        store("hot_corridors_intersection", world);
        store("cold_corridors_intersection", world);
    }

    @Override
    public int getHeight(int var1, int var2, Heightmap.Type var3, HeightLimitView var4) {
        return ROOF_Y + 5;
    }

    private void setBlock(ChunkRegion region, Chunk chunk, int alignX, int alignZ, int y, final Block block) {
        setBlock(region, chunk, alignX, alignZ, y, block.getDefaultState());
    }
    private void setBlock(ChunkRegion region, Chunk chunk, int alignX, int alignZ, int y, BlockState state) {
        setBlockState(region, chunk, state,
                new BlockPos(chunk.getPos().getStartX() + alignX, y, chunk.getPos().getStartZ() + alignZ));
    }
    private void fillRectZX(ChunkRegion region, Chunk chunk, ChunkPos pos, int sizeX, int sizeZ, int alignX, int alignZ, int y) {
        fillRectZX(region, chunk, pos, sizeX, sizeZ, alignX, alignZ, y, Blocks.AIR);
    }
    private void fillRectZX(ChunkRegion region, Chunk chunk, ChunkPos pos, int sizeX, int sizeZ, int alignX, int alignZ, int y, final Block block) {
        fillRectZX(region, chunk, pos, sizeX, sizeZ, alignX, alignZ, y, block.getDefaultState());
    }
    private boolean isPipesBiome(RegistryEntry<Biome> biome) {
        return  biome.matchesId(BackroomsLevels.PIPES_BIOME.getValue()) ||
                biome.matchesId(BackroomsLevels.COLD_PIPES_BIOME.getValue()) ||
                biome.matchesId(BackroomsLevels.HOT_PIPES_BIOME.getValue());
    }
    private RegistryEntry<Biome> getBiome(Chunk chunk) {
        final BlockPos pos = chunk.getPos().getStartPos();
        return chunk.getBiomeForNoiseGen(pos.getX(), pos.getY(), pos.getZ());
    }

    private void fillRectZX(ChunkRegion region, Chunk chunk, ChunkPos pos, int sizeX, int sizeZ, int alignX, int alignZ, int y, BlockState state) {
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
    public ChunkGenerator withSeed(long seed) {
        return new LevelTwoChunkGenerator(this.biomeSource, seed);
    }

    @Override
    public int getMinimumY() {
        return FLOOR_Y - 1;
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structureAccessor, Chunk chunk) {
    }
}
