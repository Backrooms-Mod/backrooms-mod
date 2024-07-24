package com.kpabr.backrooms.world.chunk;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.block.FiresaltCrystalBlock;
import com.kpabr.backrooms.block.FluorescentLightBlock;
import com.kpabr.backrooms.block.PipeBlock;
import com.kpabr.backrooms.init.BackroomsBlocks;
import com.kpabr.backrooms.init.BackroomsLevels;
import com.kpabr.backrooms.util.ChunkType;
import com.kpabr.backrooms.util.NbtPlacerUtil;
import com.kpabr.backrooms.world.biome.sources.LevelTwoBiomeSource;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep.Carver;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class LevelTwoChunkGenerator extends ChunkGenerator {

    public static final Codec<LevelTwoChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> instance
            .group(RegistryOps.getEntryLookupCodec(RegistryKeys.BIOME),
                    RegistryOps.getEntryLookupCodec(RegistryKeys.BLOCK))
            .apply(instance, instance.stable(LevelTwoChunkGenerator::new)));
    private final HashMap<String, NbtPlacerUtil> loadedStructures = new HashMap<String, NbtPlacerUtil>(30);
    private Identifier nbtId = BackroomsMod.id("level_2");

    private final static int ROOF_Y = 15;
    private final static int FLOOR_Y = 1;
    private SimplexNoiseSampler xPlaneNoise;
    private SimplexNoiseSampler zPlaneNoise;
    private Random random;
    private final static BlockState fluorescentLightOn = BackroomsBlocks.FLUORESCENT_LIGHT.getDefaultState()
            .with(FluorescentLightBlock.LIT, true);
    private final static BlockState fluorescentLightOff = BackroomsBlocks.FLUORESCENT_LIGHT.getDefaultState()
            .with(FluorescentLightBlock.LIT, false);
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

    private RegistryEntryLookup<Block> blockLookup;

    public LevelTwoChunkGenerator(RegistryEntryLookup<Biome> biomeRegistry, RegistryEntryLookup<Block> blockLookup) {
        super(new LevelTwoBiomeSource(biomeRegistry));
        this.blockLookup = blockLookup;
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, NoiseConfig noiseConfig,
            StructureAccessor structureAccessor, Chunk chunk) {
        if (this.random == null) {
            this.random = Random.create(BackroomsLevels.LEVEL_2_WORLD.getSeed());
            final ChunkRandom planeRandom = new ChunkRandom(Random.create(BackroomsLevels.LEVEL_2_WORLD.getSeed()));
            this.xPlaneNoise = new SimplexNoiseSampler(planeRandom);
            this.zPlaneNoise = new SimplexNoiseSampler(planeRandom);
        }

        if (this.loadedStructures.isEmpty()) {
            storeStructures(BackroomsLevels.LEVEL_2_WORLD);
        }

        final ChunkPos pos = chunk.getPos();

        final var biome = getBiome(pos);
        if (isPipesBiome(biome)) {
            final boolean isCold = biome == ChunkType.COLD_PIPES;
            final boolean isHot = biome == ChunkType.HOT_PIPES;
            Block wallBlock = Blocks.LIGHT_GRAY_TERRACOTTA;
            if (isCold)
                wallBlock = Blocks.CYAN_TERRACOTTA;
            else if (isHot)
                wallBlock = Blocks.RED_TERRACOTTA;

            final boolean isClosestToSpawnEastWestChunkCorridor;
            final boolean isClosestToSpawnNorthSouthChunkCorridor;

            final ChunkType eastWestChunkBiome;
            if (pos.x > 0)
                eastWestChunkBiome = getBiome(new ChunkPos(pos.x - 1, pos.z));
            else
                eastWestChunkBiome = getBiome(new ChunkPos(pos.x + 1, pos.z));
            isClosestToSpawnEastWestChunkCorridor = isPipesBiome(eastWestChunkBiome);

            final ChunkType northSouthChunkBiome;
            if (pos.z > 0)
                northSouthChunkBiome = getBiome(new ChunkPos(pos.x, pos.z - 1));
            else
                northSouthChunkBiome = getBiome(new ChunkPos(pos.x, pos.z + 1));
            isClosestToSpawnNorthSouthChunkCorridor = isPipesBiome(northSouthChunkBiome);

            if (isClosestToSpawnEastWestChunkCorridor && isClosestToSpawnNorthSouthChunkCorridor) {
                // Generate intersection between two corridors
                if ((eastWestChunkBiome == ChunkType.HOT_PIPES || northSouthChunkBiome == ChunkType.HOT_PIPES)
                        && random.nextBetween(0, 5) == 1) {
                    generateNbt(chunk, new BlockPos(pos.getStartX(), FLOOR_Y + 1, pos.getStartZ()),
                            "hot_corridors_intersection");
                } else if ((eastWestChunkBiome == ChunkType.COLD_PIPES || northSouthChunkBiome == ChunkType.COLD_PIPES)
                        && random.nextBetween(0, 5) == 1) {
                    generateNbt(chunk, new BlockPos(pos.getStartX(), FLOOR_Y + 1, pos.getStartZ()),
                            "cold_corridors_intersection");
                } else {
                    generateNbt(chunk, new BlockPos(pos.getStartX(), FLOOR_Y + 1, pos.getStartZ()),
                            "corridors_intersection");
                }

                // Generate 4 random lights
                fillRectZX(chunk, chunk, pos, 1, 2, 4, 7, FLOOR_Y + 7,
                        random.nextBoolean() ? fluorescentLightOn : fluorescentLightOff);
                fillRectZX(chunk, chunk, pos, 1, 2, 11, 7, FLOOR_Y + 7,
                        random.nextBoolean() ? fluorescentLightOn : fluorescentLightOff);
                fillRectZX(chunk, chunk, pos, 2, 1, 7, 4, FLOOR_Y + 7,
                        random.nextBoolean() ? fluorescentLightOn : fluorescentLightOff);
                fillRectZX(chunk, chunk, pos, 2, 1, 7, 11, FLOOR_Y + 7,
                        random.nextBoolean() ? fluorescentLightOn : fluorescentLightOff);

                // Fill empty space with light gray terracotta
                for (int i = 1; i <= 7; i++) {
                    fillRectZX(chunk, chunk, pos, 3, 5, 0, 0, FLOOR_Y + i, Blocks.LIGHT_GRAY_TERRACOTTA);
                    fillRectZX(chunk, chunk, pos, 2, 3, 3, 0, FLOOR_Y + i, Blocks.LIGHT_GRAY_TERRACOTTA);
                }
                for (int i = 1; i <= 7; i++) {
                    fillRectZX(chunk, chunk, pos, 3, 5, 13, 0, FLOOR_Y + i, Blocks.LIGHT_GRAY_TERRACOTTA);
                    fillRectZX(chunk, chunk, pos, 2, 3, 11, 0, FLOOR_Y + i, Blocks.LIGHT_GRAY_TERRACOTTA);
                }
                for (int i = 1; i <= 7; i++) {
                    fillRectZX(chunk, chunk, pos, 3, 5, 0, 11, FLOOR_Y + i, Blocks.LIGHT_GRAY_TERRACOTTA);
                    fillRectZX(chunk, chunk, pos, 2, 3, 3, 13, FLOOR_Y + i, Blocks.LIGHT_GRAY_TERRACOTTA);
                }
                for (int i = 1; i <= 7; i++) {
                    fillRectZX(chunk, chunk, pos, 3, 5, 13, 11, FLOOR_Y + i, Blocks.LIGHT_GRAY_TERRACOTTA);
                    fillRectZX(chunk, chunk, pos, 2, 3, 11, 13, FLOOR_Y + i, Blocks.LIGHT_GRAY_TERRACOTTA);
                }

            } else if (isClosestToSpawnEastWestChunkCorridor) {
                generateNbt(chunk, new BlockPos(pos.getStartX(), FLOOR_Y + 1, pos.getStartZ()), "corridor",
                        BlockRotation.CLOCKWISE_90);

                // Generate walls
                for (int i = 1; i <= 2; i++) {
                    fillRectZX(chunk, chunk, pos, 16, 1, 0, 5 * i, FLOOR_Y + 4, wallBlock);
                    fillRectZX(chunk, chunk, pos, 16, 1, 0, 5 * i, FLOOR_Y + 5, wallBlock);
                    fillRectZX(chunk, chunk, pos, 16, 1, 0, 5 * i, FLOOR_Y + 6, wallBlock);
                }

                // Generate lights on roof
                fillRectZX(chunk, chunk, pos, 3, 1, 2, 7, FLOOR_Y + 7,
                        random.nextBoolean() ? fluorescentLightOn : fluorescentLightOff);
                fillRectZX(chunk, chunk, pos, 3, 1, 11, 7, FLOOR_Y + 7,
                        random.nextBoolean() ? fluorescentLightOn : fluorescentLightOff);

                // Generate hot and cold pipes biomes features(magma and ice)
                if (isCold) {
                    // Generate on floor
                    for (int z = 7; z <= 9; z++) {
                        for (int x = 0; x < 16; x++) {
                            if (random.nextBetween(0, 5) == 1) {
                                setBlock(chunk, chunk, x, z, FLOOR_Y + 1, Blocks.PACKED_ICE);
                                if (random.nextBetween(0, 8) == 1) {
                                    setBlock(chunk, chunk, x, z, FLOOR_Y + 2, Blocks.PACKED_ICE);
                                    if (random.nextBetween(0, 8) == 1) {
                                        setBlock(chunk, chunk, x, z, FLOOR_Y + 3, Blocks.PACKED_ICE);
                                    }
                                }
                            }
                        }
                    }
                    // Generate on wall
                    for (int x = 0; x < 16; x++) {
                        if (random.nextBetween(0, 5) == 1) {
                            setBlock(chunk, chunk, x, 5, FLOOR_Y + 5, Blocks.PACKED_ICE);
                        }
                    }
                    // Generate on roof
                    for (int z = 7; z <= 9; z++) {
                        for (int x = 0; x < 16; x++) {
                            if (random.nextBetween(0, 8) == 1) {
                                setBlock(chunk, chunk, x, z, FLOOR_Y + 7, Blocks.PACKED_ICE);
                            }
                        }
                    }
                } else if (isHot) {
                    // Generate on floor
                    for (int z = 7; z <= 9; z++) {
                        for (int x = 0; x < 16; x++) {
                            if (random.nextBetween(0, 8) == 1) {
                                setBlock(chunk, chunk, x, z, FLOOR_Y + 1, Blocks.MAGMA_BLOCK);
                            }
                        }
                    }
                    // Generate on wall
                    for (int x = 0; x < 16; x++) {
                        if (random.nextBetween(0, 10) == 1) {
                            setBlock(chunk, chunk, x, 5, FLOOR_Y + 5, Blocks.MAGMA_BLOCK);
                            if (random.nextBetween(0, 4) == 1) {
                                setBlock(chunk, chunk, x, 6, FLOOR_Y + 5, firesaltNorthWallState);
                            }
                        }
                    }
                    // Generate on roof
                    for (int z = 7; z <= 9; z++) {
                        for (int x = 0; x < 16; x++) {
                            if (random.nextBetween(0, 8) == 1) {
                                setBlock(chunk, chunk, x, z, FLOOR_Y + 7, Blocks.MAGMA_BLOCK);
                            }
                        }
                    }
                }

                // Generate pipes upon corridor
                for (int x = 0; x <= 1; x++) {
                    if (random.nextBetween(0, 6) == 1) {
                        setBlock(chunk, chunk, x, 6, FLOOR_Y + 6, pipeWestEastState.with(PipeBlock.SOUTH, true));
                        setBlock(chunk, chunk, x, 7, FLOOR_Y + 6, pipeNorthSouthState);
                        setBlock(chunk, chunk, x, 8, FLOOR_Y + 6, pipeNorthSouthState);
                        setBlock(chunk, chunk, x, 9, FLOOR_Y + 6, pipeWestEastState.with(PipeBlock.NORTH, true));
                    }
                }
                for (int x = 5; x <= 10; x++) {
                    if (random.nextBetween(0, 6) == 1) {
                        setBlock(chunk, chunk, x, 6, FLOOR_Y + 6, pipeWestEastState.with(PipeBlock.SOUTH, true));
                        setBlock(chunk, chunk, x, 7, FLOOR_Y + 6, pipeNorthSouthState);
                        setBlock(chunk, chunk, x, 8, FLOOR_Y + 6, pipeNorthSouthState);
                        setBlock(chunk, chunk, x, 9, FLOOR_Y + 6, pipeWestEastState.with(PipeBlock.NORTH, true));
                    }
                }
                for (int x = 14; x <= 15; x++) {
                    if (random.nextBetween(0, 6) == 1) {
                        setBlock(chunk, chunk, x, 6, FLOOR_Y + 6, pipeWestEastState.with(PipeBlock.SOUTH, true));
                        setBlock(chunk, chunk, x, 7, FLOOR_Y + 6, pipeNorthSouthState);
                        setBlock(chunk, chunk, x, 8, FLOOR_Y + 6, pipeNorthSouthState);
                        setBlock(chunk, chunk, x, 9, FLOOR_Y + 6, pipeWestEastState.with(PipeBlock.NORTH, true));
                    }
                }

                // Fill empty space with light gray terracotta
                for (int i = 1; i <= 7; i++) {
                    fillRectZX(chunk, chunk, pos, 16, 5, 0, 0, FLOOR_Y + i, Blocks.LIGHT_GRAY_TERRACOTTA);
                    fillRectZX(chunk, chunk, pos, 16, 5, 0, 11, FLOOR_Y + i, Blocks.LIGHT_GRAY_TERRACOTTA);
                }
            } else if (isClosestToSpawnNorthSouthChunkCorridor) {
                generateNbt(chunk, new BlockPos(pos.getStartX(), FLOOR_Y + 1, pos.getStartZ()), "corridor");
                // Generate walls
                for (int i = 1; i <= 2; i++) {
                    fillRectZX(chunk, chunk, pos, 1, 16, 5 * i, 0, FLOOR_Y + 4, wallBlock);
                    fillRectZX(chunk, chunk, pos, 1, 16, 5 * i, 0, FLOOR_Y + 5, wallBlock);
                    fillRectZX(chunk, chunk, pos, 1, 16, 5 * i, 0, FLOOR_Y + 6, wallBlock);
                }

                // Generate lights on roof
                fillRectZX(chunk, chunk, pos, 1, 3, 7, 2, FLOOR_Y + 7,
                        random.nextBoolean() ? fluorescentLightOn : fluorescentLightOff);
                fillRectZX(chunk, chunk, pos, 1, 3, 7, 11, FLOOR_Y + 7,
                        random.nextBoolean() ? fluorescentLightOn : fluorescentLightOff);

                // Generate hot and cold pipes biomes features(magma and ice)
                if (isCold) {
                    // Generate on floor
                    for (int x = 7; x <= 9; x++) {
                        for (int z = 0; z < 16; z++) {
                            if (random.nextBetween(0, 5) == 1) {
                                setBlock(chunk, chunk, x, z, FLOOR_Y + 1, Blocks.PACKED_ICE);
                                if (random.nextBetween(0, 8) == 1) {
                                    setBlock(chunk, chunk, x, z, FLOOR_Y + 2, Blocks.PACKED_ICE);
                                    if (random.nextBetween(0, 8) == 1) {
                                        setBlock(chunk, chunk, x, z, FLOOR_Y + 3, Blocks.PACKED_ICE);
                                    }
                                }
                            }
                        }
                    }
                    // Generate on wall
                    for (int z = 0; z < 16; z++) {
                        if (random.nextBetween(0, 5) == 1) {
                            setBlock(chunk, chunk, 5, z, FLOOR_Y + 5, Blocks.PACKED_ICE);
                        }
                    }
                    // Generate on roof
                    for (int x = 7; x <= 9; x++) {
                        for (int z = 0; z < 16; z++) {
                            if (random.nextBetween(0, 8) == 1) {
                                setBlock(chunk, chunk, x, z, FLOOR_Y + 7, Blocks.PACKED_ICE);
                            }
                        }
                    }
                } else if (isHot) {
                    // Generate on floor
                    for (int x = 7; x <= 9; x++) {
                        for (int z = 0; z < 16; z++) {
                            if (random.nextBetween(0, 8) == 1) {
                                setBlock(chunk, chunk, x, z, FLOOR_Y + 1, Blocks.MAGMA_BLOCK);
                            }
                        }
                    }
                    // Generate on wall
                    for (int z = 0; z < 16; z++) {
                        if (random.nextBetween(0, 10) == 1) {
                            setBlock(chunk, chunk, 5, z, FLOOR_Y + 5, Blocks.MAGMA_BLOCK);
                            if (random.nextBetween(0, 4) == 1) {
                                setBlock(chunk, chunk, 6, z, FLOOR_Y + 5, firesaltWestWallState);
                            }
                        }
                    }
                    // Generate on roof
                    for (int x = 7; x <= 9; x++) {
                        for (int z = 0; z < 16; z++) {
                            if (random.nextBetween(0, 8) == 1) {
                                setBlock(chunk, chunk, x, z, FLOOR_Y + 7, Blocks.MAGMA_BLOCK);
                            }
                        }
                    }
                }

                // Generate pipes upon corridor
                for (int z = 0; z <= 1; z++) {
                    if (random.nextBetween(0, 6) == 1) {
                        setBlock(chunk, chunk, 6, z, FLOOR_Y + 6, pipeNorthSouthState.with(PipeBlock.EAST, true));
                        setBlock(chunk, chunk, 7, z, FLOOR_Y + 6, pipeWestEastState);
                        setBlock(chunk, chunk, 8, z, FLOOR_Y + 6, pipeWestEastState);
                        setBlock(chunk, chunk, 9, z, FLOOR_Y + 6, pipeNorthSouthState.with(PipeBlock.WEST, true));
                    }
                }
                for (int z = 5; z <= 10; z++) {
                    if (random.nextBetween(0, 6) == 1) {
                        setBlock(chunk, chunk, 6, z, FLOOR_Y + 6, pipeNorthSouthState.with(PipeBlock.EAST, true));
                        setBlock(chunk, chunk, 7, z, FLOOR_Y + 6, pipeWestEastState);
                        setBlock(chunk, chunk, 8, z, FLOOR_Y + 6, pipeWestEastState);
                        setBlock(chunk, chunk, 9, z, FLOOR_Y + 6, pipeNorthSouthState.with(PipeBlock.WEST, true));
                    }
                }
                for (int z = 14; z <= 15; z++) {
                    if (random.nextBetween(0, 6) == 1) {
                        setBlock(chunk, chunk, 6, z, FLOOR_Y + 6, pipeNorthSouthState.with(PipeBlock.EAST, true));
                        setBlock(chunk, chunk, 7, z, FLOOR_Y + 6, pipeWestEastState);
                        setBlock(chunk, chunk, 8, z, FLOOR_Y + 6, pipeWestEastState);
                        setBlock(chunk, chunk, 9, z, FLOOR_Y + 6, pipeNorthSouthState.with(PipeBlock.WEST, true));
                    }
                }

                // Fill empty space with light gray terracotta
                for (int i = 1; i <= 7; i++) {
                    fillRectZX(chunk, chunk, pos, 5, 16, 0, 0, FLOOR_Y + i, Blocks.LIGHT_GRAY_TERRACOTTA);
                    fillRectZX(chunk, chunk, pos, 5, 16, 11, 0, FLOOR_Y + i, Blocks.LIGHT_GRAY_TERRACOTTA);
                }
            }
        } else {
            for (int i = 1; i <= 7; i++) {
                fillRectZX(chunk, chunk, pos, 16, 16, 0, 0, FLOOR_Y + i, Blocks.LIGHT_GRAY_TERRACOTTA);
            }
        }
        fillRectZX(chunk, chunk, pos, 16, 16, 0, 0, FLOOR_Y, BackroomsBlocks.BEDROCK_BRICKS);
        return CompletableFuture.completedFuture(chunk);
    }

    public void storeStructures(ServerWorld world) {
        store("corridors_intersection", world);
        store("hot_corridors_intersection", world);
        store("cold_corridors_intersection", world);
        store("corridor", world);
    }

    private void store(String id, ServerWorld world) {
        loadedStructures.put(id,
                NbtPlacerUtil.load(world.getServer().getResourceManager(),
                        new Identifier(this.nbtId.getNamespace(), "nbt/" + this.nbtId.getPath() + "/" + id + ".nbt"),
                        this.blockLookup).get());
    }

    @Override
    public int getHeight(int x, int z, Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        return ROOF_Y + 5;
    }

    private final static int chunksToCheckBeforeCurrentChunk = 3;

    public ChunkType getChunkType(ChunkPos chunk) {
        int z = chunk.z, x = chunk.x;
        if (z == 0 || x == 0) {
            return ChunkType.PIPES;
        }
        final double xNoise = xPlaneNoise.sample(x, 0), zNoise = zPlaneNoise.sample(z, 0);
        if (xNoise < -0.45) {
            boolean isSuccessful = true;

            if (Math.abs(x) < 4) {
                isSuccessful = false;
            }

            // If 4 <= x <= 6, only chunks in [4; x) bounds will be checked
            // Else if -6 <= x <= -4, only chunks in [4; x) bounds will be checked
            if (x > 0) {
                for (int i = Math.max(x - chunksToCheckBeforeCurrentChunk, 4); i < x; i++) {
                    if (xPlaneNoise.sample(i, 0) < -0.45) {
                        isSuccessful = false;
                        break;
                    }
                }
            } else {
                for (int i = Math.min(x + chunksToCheckBeforeCurrentChunk, -4); i > x; i--) {
                    if (xPlaneNoise.sample(i, 0) < -0.45) {
                        isSuccessful = false;
                        break;
                    }
                }
            }

            if (isSuccessful) {
                if (xNoise < -0.85)
                    return ChunkType.COLD_PIPES;
                return ChunkType.PIPES;
            }
        }
        if (zNoise > 0.45) {
            if (Math.abs(z) < 4) {
                return ChunkType.EMPTY;
            }
            // If 4 <= z <= 6, only chunks in [4; z) bounds will be checked
            // Else if -6 <= z <= -4, only chunks in [4; z) bounds will be checked
            if (z > 0) {
                for (int i = Math.max(z - chunksToCheckBeforeCurrentChunk, 4); i < z; i++) {
                    if (zPlaneNoise.sample(i, 0) > 0.45) {
                        return ChunkType.EMPTY;
                    }
                }
            } else {
                for (int i = Math.min(z + chunksToCheckBeforeCurrentChunk, -4); i > z; i--) {
                    if (zPlaneNoise.sample(i, 0) > 0.45) {
                        return ChunkType.EMPTY;
                    }
                }
            }

            if (zNoise > 0.85)
                return ChunkType.HOT_PIPES;
            return ChunkType.PIPES;
        }

        return ChunkType.EMPTY;
    }

    private void setBlock(Chunk region, Chunk chunk, int alignX, int alignZ, int y, final Block block) {
        setBlock(region, chunk, alignX, alignZ, y, block.getDefaultState());
    }

    private void setBlock(Chunk region, Chunk chunk, int alignX, int alignZ, int y, BlockState state) {
        setBlockState(region, chunk, state,
                new BlockPos(chunk.getPos().getStartX() + alignX, y, chunk.getPos().getStartZ() + alignZ));
    }

    private void fillRectZX(Chunk region, Chunk chunk, ChunkPos pos, int sizeX, int sizeZ, int alignX, int alignZ,
            int y, final Block block) {
        fillRectZX(region, chunk, pos, sizeX, sizeZ, alignX, alignZ, y, block.getDefaultState());
    }

    private boolean isPipesBiome(ChunkType biome) {
        return biome == ChunkType.PIPES || biome == ChunkType.COLD_PIPES || biome == ChunkType.HOT_PIPES;
    }

    private ChunkType getBiome(ChunkPos chunk) {
        return getChunkType(chunk);
    }

    private void fillRectZX(Chunk region, Chunk chunk, ChunkPos pos, int sizeX, int sizeZ, int alignX, int alignZ,
            int y, BlockState state) {
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeZ; j++) {
                setBlockState(region, chunk, state,
                        new BlockPos(pos.getStartX() + alignX + i, y, pos.getStartZ() + alignZ + j));
            }
        }
    }

    /**
     * Fabulously optimized setBlockState function, don't use it if you aren't sure,
     * that old block state is AIR and your block isn't BlockEntity
     */
    private void setBlockState(Chunk region, Chunk chunk, BlockState state, BlockPos pos) {
        final BlockState blockState = chunk.setBlockState(pos, state, false);
        if (blockState != null) {
            BackroomsLevels.LEVEL_2_WORLD.onBlockChanged(pos, blockState, state);
        }
    }

    @Override
    protected Codec<LevelTwoChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public int getMinimumY() {
        return FLOOR_Y - 1;
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {
    }

    private void generateNbt(Chunk region, BlockPos at, String id, BlockRotation rotation) {
        loadedStructures.get(id).rotate(rotation, this.blockLookup).generateNbt(region, at,
                (pos, state, nbt) -> this.modifyStructure(region, pos, state, nbt));
    }

    private void generateNbt(Chunk region, BlockPos at, String id) {
        generateNbt(region, at, id, BlockRotation.NONE);
    }

    private void modifyStructure(Chunk region, BlockPos pos, BlockState state, NbtCompound nbt) {
        if (!state.isAir()) {
            if (state.isOf(Blocks.BARRIER)) {
                region.setBlockState(pos, Blocks.AIR.getDefaultState(), true);
            } else {
                region.setBlockState(pos, state, true);
            }
        }
    }

    @Override
    public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess world,
            StructureAccessor structureAccessor, Chunk chunk, Carver carverStep) {
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        return new VerticalBlockSample(0, new BlockState[0]);
    }

    @Override
    public void getDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }

    @Override
    public int getWorldHeight() {
        return 128;
    }

    @Override
    public void populateEntities(ChunkRegion region) {
    }
}
