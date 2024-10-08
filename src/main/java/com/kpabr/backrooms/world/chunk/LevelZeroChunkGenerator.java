package com.kpabr.backrooms.world.chunk;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.HashMap;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.block.entity.CrateBlockEntity;
import com.kpabr.backrooms.config.BackroomsConfig;
import com.kpabr.backrooms.init.BackroomsBlocks;
import com.kpabr.backrooms.init.BackroomsLevels;
import com.kpabr.backrooms.init.BackroomsLootTables;
import com.kpabr.backrooms.util.NbtPlacerUtil;
import com.kpabr.backrooms.world.biome.sources.LevelZeroBiomeSource;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
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

public class LevelZeroChunkGenerator extends ChunkGenerator {

    public static final Codec<LevelZeroChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> instance
            .group(RegistryOps.getEntryLookupCodec(RegistryKeys.BIOME),
                    RegistryOps.getEntryLookupCodec(RegistryKeys.BLOCK))
            .apply(instance, instance.stable(LevelZeroChunkGenerator::new)));
    private static final int ROOF_BEGIN_Y = 6 * (getFloorCount() + 1) + 1;
    private static final BlockState ROOF_BLOCK = BackroomsBlocks.BEDROCK_BRICKS.getDefaultState();

    private final HashMap<String, NbtPlacerUtil> loadedStructures = new HashMap<String, NbtPlacerUtil>(100);
    private final Identifier nbtId = BackroomsMod.id("level_zero");

    private Random moldPlacementRandom;
    // Random object controlling generation of walls.
    private Random random;
    private RegistryEntryLookup<Block> blockLookup;

    public LevelZeroChunkGenerator(RegistryEntryLookup<Biome> biomeRegistry, RegistryEntryLookup<Block> blockLookup) {
        super(new LevelZeroBiomeSource(biomeRegistry));
        this.blockLookup = blockLookup;
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess world,
            StructureAccessor structureAccessor, Chunk chunk, Carver carverStep) {
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {
        if (this.moldPlacementRandom == null) {
            this.moldPlacementRandom = new Random(BackroomsLevels.LEVEL_0_WORLD.getSeed());
        }

        final ChunkPos chunkPos = chunk.getPos();
        final BlockPos biomePos = chunkPos.getBlockPos(4, 4, 4);

        // controls every block up to the roof
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < ROOF_BEGIN_Y; y++) {
                    // does a swap from the various stones to the custom blocks
                    if (isBiomeEquals(BackroomsLevels.CRIMSON_WALLS_BIOME, chunk, biomePos)) {
                        final BlockPos pos = chunkPos.getBlockPos(x, y, z);
                        final BlockState block = chunk.getBlockState(pos);
                        if (block == BackroomsBlocks.PATTERNED_WALLPAPER.getDefaultState()) {
                            replace(BackroomsBlocks.RED_PATTERNED_WALLPAPER, chunk, pos);
                        } else if (block.getBlock() == BackroomsBlocks.WOOLEN_CARPET
                                || block == BackroomsBlocks.MOLDY_WOOLEN_CARPET.getDefaultState()) {

                            replace(BackroomsBlocks.RED_CARPETING, chunk, pos);
                        } else if (block == BackroomsBlocks.CORK_TILE.getDefaultState()
                                && moldPlacementRandom
                                        .nextDouble() < BackroomsConfig.getInstance().moldyCorkTileChance) {

                            replace(BackroomsBlocks.MOLDY_CORK_TILE, chunk, pos);
                        } else if(block == BackroomsBlocks.NOCLIP_CARPETING.getDefaultState()) {
                            replace(BackroomsBlocks.RED_CARPETING, chunk, pos);
                        } else if(block == BackroomsBlocks.NOCLIP_WALL.getDefaultState()) {
                            replace(BackroomsBlocks.RED_PATTERNED_WALLPAPER, chunk, pos);
                        } else if(block == BackroomsBlocks.STRIPED_WALLPAPER.getDefaultState()) {
                            replace(BackroomsBlocks.RED_STRIPED_WALLPAPER, chunk, pos);
                        } else if(block == BackroomsBlocks.DOTTED_WALLPAPER.getDefaultState()) {
                            replace(BackroomsBlocks.RED_DOTTED_WALLPAPER, chunk, pos);
                        }
                    } else if (isBiomeEquals(BackroomsLevels.DECREPIT_BIOME, chunk, biomePos)) {
                        final BlockPos pos = chunkPos.getBlockPos(x, y, z);
                        final BlockState block = chunk.getBlockState(pos);

                        if (block == BackroomsBlocks.WOOLEN_CARPET.getDefaultState()) {
                            replace(BackroomsBlocks.MOLDY_WOOLEN_CARPET, chunk, pos);
                        } else if (block == BackroomsBlocks.FLUORESCENT_LIGHT.getDefaultState()) {
                            replace(BackroomsBlocks.REPAIRED_FLUORESCENT_LIGHT, chunk, pos);
                        } else if(block == BackroomsBlocks.NOCLIP_CARPETING.getDefaultState()) {
                            replace(BackroomsBlocks.MOLDY_WOOLEN_CARPET, chunk, pos);
                        } else if(block == Blocks.BIRCH_DOOR.getDefaultState()) {
                            replace(Blocks.DARK_OAK_DOOR, chunk, pos);
                        }
                    }
                }
            }
        }

    }

    @Override
    public void populateEntities(ChunkRegion region) {
    }

    @Override
    public int getWorldHeight() {
        return 0;
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, NoiseConfig noiseConfig,
            StructureAccessor structureAccessor, Chunk chunk) {
        if (this.random == null) {
            this.random = new Random(BackroomsLevels.LEVEL_0_WORLD.getSeed());
        }

        // IMPORTANT NOTE:
        // For biomes generation we're using various "placeholder" blocks to replace
        // them later with blocks we actually need in biomes.
        // If you're adding new type of structure then don't use blocks other than
        // described below from our mod!
        // Instead, use those blocks:
        // BackroomsBlocks.PATTERNED_WALLPAPER -> any wallpaper
        // BackroomsBlocks.WOOLEN_CARPET -> any carpet
        // BackroomsBlocks.CORK_TILE -> any cork tile
        // BackroomsBlocks.FLUORESCENT_LIGHT -> any light source
        // BackroomsBlocks.MOLDY_WOOLEN_CARPET -> random blocks(you can just replace
        // them with carpet)

        final ChunkPos chunkPos = chunk.getPos();
        final BlockPos biomePos = chunkPos.getBlockPos(4, 4, 4);
        // Save the starting x and z position of the chunk. Note: positive x means east,
        // positive z means south.
        final int startX = chunkPos.getStartX();
        final int startZ = chunkPos.getStartZ();
        if (this.loadedStructures.isEmpty()) {
            storeStructures(BackroomsLevels.LEVEL_0_WORLD);
        }

        // Place bedrock bricks at the roof of chunk
        for (int x = startX; x < startX + 16; x++) {
            for (int z = startZ; z < startZ + 16; z++) {
                chunk.setBlockState(new BlockPos(x, ROOF_BEGIN_Y, z), ROOF_BLOCK, false);
            }
        }

        if (isBiomeEquals(BackroomsLevels.MEGALOPHOBIA_BIOME, chunk, biomePos)) {
            // Create 3 floors, top to bottom, because one Megalophobia floor equals 2
            // normal floors.
            for (int y = 2; y >= 0; y--) {
                // Create 16 smaller sections of the floor, layed out in a 4x4 pattern. Each
                // section will consist of the carpeting, the ceiling, two walls (located on the
                // eastern and southern side of the section) and a pillar, located in the
                // southeasternmost space.
                for (int x = 1; x >= 0; x--) {
                    for (int z = 1; z >= 0; z--) {
                        // wall generating, respectively.
                        final int wallType = (random.nextFloat() < 0.4F ? 1 : 0) + (random.nextFloat() < 0.4F ? 2 : 0);

                        // Check if the arrangement includes the eastern wall.
                        // and create eastern wall if true
                        if ((wallType & 1) == 1) {
                            for (int i = 0; i < 7; i++) {
                                for (int j = 0; j < 10; j++) {
                                    for (int k = 0; k < 2; k++) {
                                        chunk.setBlockState(
                                                new BlockPos(startX + x * 8 + 6 + k, 2 + 12 * y + j,
                                                        startZ + z * 8 + i),
                                                BackroomsBlocks.PATTERNED_WALLPAPER.getDefaultState(),
                                                false);
                                    }
                                }
                            }
                        }
                        // Check if the arrangement includes the southern wall
                        // and create southern wall if true
                        if ((wallType & 2) == 2) {
                            for (int i = 0; i < 7; i++) {
                                for (int j = 0; j < 10; j++) {

                                    for (int k = 0; k < 2; k++) {
                                        chunk.setBlockState(
                                                new BlockPos(startX + x * 8 + i, 2 + 12 * y + j,
                                                        startZ + z * 8 + 6 + k),
                                                BackroomsBlocks.PATTERNED_WALLPAPER.getDefaultState(),
                                                false);
                                    }
                                }
                            }
                        }
                        // New variable controlling whether a pillar in generated.
                        // If there's a wall in the current section, always create a pillar.
                        boolean pillar = wallType != 0;

                        // If you're on the southeasternmost spot on the chunk, always make a pillar.
                        pillar = pillar || (x == 1 && z == 1);

                        // Check if you're not on the eastern edge of the chunk. If you aren't, proceed.
                        // Check one block east whether there's a wall there. If so, a pillar will
                        // always be generated.
                        if (x != 1) {
                            pillar = pillar ||
                                    !chunk.getBlockState(
                                            new BlockPos(startX + x * 8 + 8, 2 + 12 * y, startZ + z * 8 + 7)).isAir();
                        }
                        // Check if you're not on the southern edge of the chunk. If you aren't,
                        // proceed.
                        if (z != 1) {
                            // Check one block south whether there's a wall there. If so, a pillar will
                            // always be generated.
                            pillar = pillar || !chunk
                                    .getBlockState(new BlockPos(startX + x * 8 + 7, 2 + 12 * y, startZ + z * 8 + 8))
                                    .isAir();
                        }
                        pillar = pillar || (random.nextFloat() < 0.2F); // Sometimes generate a pillar anyways, even if
                                                                        // none of the previous conditions were met.
                        if (pillar) {
                            // Create the pillar.
                            for (int i = 0; i < 10; i++) {
                                for (int j = 0; j < 2; j++) {
                                    for (int k = 0; k < 2; k++) {
                                        chunk.setBlockState(
                                                new BlockPos(startX + x * 8 + 6 + j, 2 + 12 * y + i,
                                                        startZ + z * 8 + 6 + k),
                                                BackroomsBlocks.PATTERNED_WALLPAPER.getDefaultState(), false);
                                    }
                                }
                            }
                        }
                        // Generate the carpeting and the ceiling.
                        for (int i = 0; i < 8; i++) {
                            for (int j = 0; j < 8; j++) {
                                chunk.setBlockState(new BlockPos(startX + x * 8 + i, 1 + 12 * y, startZ + z * 8 + j),
                                        BackroomsBlocks.WOOLEN_CARPET.getDefaultState(), false);
                                chunk.setBlockState(new BlockPos(startX + x * 8 + i, 12 + 12 * y, startZ + z * 8 + j),
                                        BackroomsBlocks.CORK_TILE.getDefaultState(), false);
                            }
                        }
                        // Place a ceiling light.
                        chunk.setBlockState(new BlockPos(startX + x * 8 + 2, 12 + 12 * y, startZ + z * 8 + 2),
                                BackroomsBlocks.FLUORESCENT_LIGHT.getDefaultState(), false);
                        chunk.setBlockState(new BlockPos(startX + x * 8 + 2, 12 + 12 * y, startZ + z * 8 + 3),
                                BackroomsBlocks.FLUORESCENT_LIGHT.getDefaultState(), false);
                        chunk.setBlockState(new BlockPos(startX + x * 8 + 3, 12 + 12 * y, startZ + z * 8 + 2),
                                BackroomsBlocks.FLUORESCENT_LIGHT.getDefaultState(), false);
                        chunk.setBlockState(new BlockPos(startX + x * 8 + 3, 12 + 12 * y, startZ + z * 8 + 3),
                                BackroomsBlocks.FLUORESCENT_LIGHT.getDefaultState(), false);
                    }
                }
            }

            // Mold placement code; will be subject to heavy revisions, so ignore for now.
            for (int y = getFloorCount(); y >= 0; y--) {

                for (int i = 0; i < 300; i++) {
                    final int x = random.nextInt(16);
                    final int z = random.nextInt(16);
                    int x2 = x + random.nextInt(3) - 1;
                    int z2 = z + random.nextInt(3) - 1;
                    if (chunk.getBlockState(new BlockPos(startX + x, 1 + 12 * y,
                            startZ + z)) == BackroomsBlocks.WOOLEN_CARPET.getDefaultState()) {
                        if (x2 < 0)
                            x2 = 0;
                        else if (x2 > 15)
                            x2 = 15;
                        if (z2 < 0)
                            z2 = 0;
                        else if (z2 > 15)
                            z2 = 15;
                        if (random.nextFloat() < 0.1F || chunk.getBlockState(new BlockPos(startX + x2,
                                1 + 12 * y, startZ + z2)) == BackroomsBlocks.CORK_TILE.getDefaultState()) {
                            chunk.setBlockState(
                                    new BlockPos(startX + x, 1 + 12 * y, startZ + z),
                                    BackroomsBlocks.MOLDY_WOOLEN_CARPET.getDefaultState(),
                                    false);
                        }
                    }
                }
            }
            // Place bedrock bricks at the bottom.
            for (int x = startX; x < startX + 16; x++) {
                for (int z = startZ; z < startZ + 16; z++) {
                    chunk.setBlockState(new BlockPos(x, 0, z), ROOF_BLOCK, false);
                }
            }
            return CompletableFuture.completedFuture(chunk);
        } else {

            // Create 5 floors, top to bottom.
            for (int y = getFloorCount(); y >= 0; y--) {
                // Create 16 smaller sections of the floor, layed out in a 4x4 pattern. Each
                // section will consist of the carpeting, the ceiling, two walls (located on the
                // eastern and southern side of the section) and a pillar, located in the
                // southeasternmost space.
                for (int x = 3; x >= 0; x--) {
                    for (int z = 3; z >= 0; z--) {
                        // Decide the arrangement of the walls of the section. The two numbers with an F
                        // directly after them denote the probability of an eastern wall and a southern
                        // wall generating, respectively.
                        final int wallType = (random.nextFloat() < 0.4F ? 1 : 0) + (random.nextFloat() < 0.4F ? 2 : 0);

                        // Check if the arrangement includes the eastern wall.
                        // and create eastern wall if true
                        if ((wallType & 1) == 1) {
                            double noclipWallChance = random.nextDouble(0, 1);
                            for (int i = 0; i < 3; i++) {
                                for (int j = 0; j < 4; j++) {
                                    chunk.setBlockState(
                                            new BlockPos(startX + x * 4 + 3, 2 + 6 * y + j, startZ + z * 4 + i),
                                            BackroomsBlocks.PATTERNED_WALLPAPER.getDefaultState(),
                                            false);

                                    if(noclipWallChance <= BackroomsConfig.getInstance().noclipWallSpawnChance) {
                                        if(j <= 2) {
                                            chunk.setBlockState(
                                                    new BlockPos(startX + x * 4 + 3, 2 + 6 * y + j, startZ + z * 4 + i),
                                                    BackroomsBlocks.NOCLIP_WALL.getDefaultState(),
                                                    false);
                                        }
                                    }
                                }
                            }
                        }
                        // Check if the arrangement includes the southern wall
                        // and create southern wall if true
                        if ((wallType & 2) == 2) {
                            for (int i = 0; i < 3; i++) {
                                for (int j = 0; j < 4; j++) {
                                    chunk.setBlockState(
                                            new BlockPos(startX + x * 4 + i, 2 + 6 * y + j, startZ + z * 4 + 3),
                                            BackroomsBlocks.PATTERNED_WALLPAPER.getDefaultState(),
                                            false);
                                }
                            }
                        }
                        // New variable controlling whether a pillar in generated.
                        // If there's a wall in the current section, always create a pillar.
                        boolean pillar = wallType != 0;

                        // If you're on the southeasternmost spot on the chunk, always make a pillar.
                        pillar = pillar || (x == 3 && z == 3);

                        // Check if you're not on the eastern edge of the chunk. If you aren't, proceed.
                        // Check one block east whether there's a wall there. If so, a pillar will
                        // always be generated.
                        if (x != 3) {
                            pillar = pillar ||
                                    !chunk.getBlockState(
                                            new BlockPos(startX + x * 4 + 4, 2 + 6 * y, startZ + z * 4 + 3)).isAir();
                        }
                        // Check if you're not on the southern edge of the chunk. If you aren't,
                        // proceed.
                        if (z != 3) {
                            // Check one block south whether there's a wall there. If so, a pillar will
                            // always be generated.
                            pillar = pillar || !chunk
                                    .getBlockState(new BlockPos(startX + x * 4 + 3, 2 + 6 * y, startZ + z * 4 + 4))
                                    .isAir();
                        }
                        pillar = pillar || (random.nextFloat() < 0.2F); // Sometimes generate a pillar anyways, even if
                                                                        // none of the previous conditions were met.
                        if (pillar) {
                            // Create the pillar.
                            for (int i = 0; i < 4; i++) {
                                chunk.setBlockState(new BlockPos(startX + x * 4 + 3, 2 + 6 * y + i, startZ + z * 4 + 3),
                                        BackroomsBlocks.PATTERNED_WALLPAPER.getDefaultState(), false);
                            }
                        }
                        // Generate the carpeting and the ceiling.
                        for (int i = 0; i < 4; i++) {
                            for (int j = 0; j < 4; j++) {
                                double noclipCarpetingChance = random.nextDouble(0, 1);

                                if(noclipCarpetingChance < BackroomsConfig.getInstance().noclipCarpetingSpawnChance) {
                                    chunk.setBlockState(new BlockPos(startX + x * 4 + i, 1 + 6 * y, startZ + z * 4 + j),
                                            BackroomsBlocks.NOCLIP_CARPETING.getDefaultState(), false);
                                } else {
                                    chunk.setBlockState(new BlockPos(startX + x * 4 + i, 1 + 6 * y, startZ + z * 4 + j),
                                            BackroomsBlocks.WOOLEN_CARPET.getDefaultState(), false);
                                }
                                chunk.setBlockState(new BlockPos(startX + x * 4 + i, 6 + 6 * y, startZ + z * 4 + j),
                                        BackroomsBlocks.CORK_TILE.getDefaultState(), false);
                            }
                        }
                        // Place a ceiling light.
                        chunk.setBlockState(new BlockPos(startX + x * 4 + 1, 6 + 6 * y, startZ + z * 4 + 1),
                                BackroomsBlocks.FLUORESCENT_LIGHT.getDefaultState(), false);
                    }
                }


                // Check whether a random number between zero and one is less than the number
                // with an F directly after it. Currently, for debugging reasons, a "|| true"
                // has been placed, which means that the following code will be excecuted
                // anyways.
                // Place a large (7x7 or bigger) room in the current chunk at the current floor.
                // Both dimensions of the base of the room must be of the form 4x-1.
                if (random.nextFloat() < 0.1f) {
                    // Define the amounts of r.egular and nofill rooms.
                    final int rooms = 58;
                    // Choose the room that will be placed.
                    int roomNumber = (random.nextInt(1, rooms + 1));
                    // The number with an F directly after it denotes the probability of an empty
                    // room being generated regardless;
                    String roomName = "room_" + roomNumber;

                    // Choose the rotation for the room.
                    Direction dir = Direction.fromHorizontal(random.nextInt(4));
                    BlockRotation rotation = switch (dir) {
                        case NORTH -> BlockRotation.COUNTERCLOCKWISE_90;
                        case EAST -> BlockRotation.NONE;
                        case SOUTH -> BlockRotation.CLOCKWISE_90;
                        default -> BlockRotation.CLOCKWISE_180;
                    };

                    // Calculate size of current room


                    final var currentRoom = this.loadedStructures.get(roomName);


                    int sizeY = currentRoom.sizeY, sizeX, sizeZ;
                    final boolean isEastOrWestDirection = dir.equals(Direction.EAST) || dir.equals(Direction.WEST);
                    if (isEastOrWestDirection) {
                        sizeX = currentRoom.sizeX;
                        sizeZ = currentRoom.sizeZ;
                    } else {
                        sizeX = currentRoom.sizeZ;
                        sizeZ = currentRoom.sizeX;
                    }
                    // Place a structure only if it fits before the bedrock
                    if (6 * y + sizeY < ROOF_BEGIN_Y) {
                        // Choose a spot in the chunk.
                        final int x = random.nextInt(5);
                        final int z = random.nextInt(5);
                        // Fill the area the room will be placed in with air.
                        for (int i = 0; i < sizeX; i++) {
                            for (int j = 0; j < sizeY; j++) {
                                for (int k = 0; k < sizeZ; k++) {
                                    chunk.setBlockState(
                                            new BlockPos(startX + x * 4 + i, 2 + 6 * y + j, startZ + z * 4 + k),
                                            Blocks.AIR.getDefaultState(),
                                            false);
                                }
                            }
                        }
                        generateNbt(chunk, new BlockPos(startX + x * 4, 2 + 6 * y, startZ + z * 4), roomName, rotation);
                    }
                }
            }

            // Mold placement code; will be subject to heavy revisions, so ignore for now.
            for (int y = getFloorCount(); y >= 0; y--) {
                for (int i = 0; i < 300; i++) {
                    final int x = random.nextInt(16);
                    final int z = random.nextInt(16);
                    int x2 = x + random.nextInt(3) - 1;
                    int z2 = z + random.nextInt(3) - 1;
                    if (chunk.getBlockState(new BlockPos(startX + x, 1 + 6 * y,
                            startZ + z)) == BackroomsBlocks.WOOLEN_CARPET.getDefaultState()) {
                        if (x2 < 0)
                            x2 = 0;
                        else if (x2 > 15)
                            x2 = 15;
                        if (z2 < 0)
                            z2 = 0;
                        else if (z2 > 15)
                            z2 = 15;
                        if (random.nextFloat() < 0.1F || chunk.getBlockState(new BlockPos(startX + x2,
                                1 + 6 * y, startZ + z2)) == BackroomsBlocks.CORK_TILE.getDefaultState()) {
                            chunk.setBlockState(
                                    new BlockPos(startX + x, 1 + 6 * y, startZ + z),
                                    BackroomsBlocks.MOLDY_WOOLEN_CARPET.getDefaultState(),
                                    false);
                        }
                    }
                }
            }
            // Place bedrock bricks at the bottom.
            for (int x = startX; x < startX + 16; x++) {
                for (int z = startZ; z < startZ + 16; z++) {
                    chunk.setBlockState(new BlockPos(x, 0, z), ROOF_BLOCK, false);
                }
            }

            return CompletableFuture.completedFuture(chunk);

        }
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }

    @Override
    public int getMinimumY() {
        return 0;
    }

    @Override
    public int getHeight(int x, int z, Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        return 128;
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        return new VerticalBlockSample(0, new BlockState[0]);
    }

    @Override
    public void getDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {
    }

    public static int getFloorCount() {
        return 5;
    }

    public void storeStructures(ServerWorld world) {
        store("room", world, 1, 58);
    }

    private void store(String id, ServerWorld world) {
        loadedStructures.put(id,
                NbtPlacerUtil.load(world.getServer().getResourceManager(),
                        new Identifier(this.nbtId.getNamespace(), "nbt/" + this.nbtId.getPath() + "/" + id + ".nbt"),
                        this.blockLookup).get());
    }

    private void store(String id, ServerWorld world, int from, int to) {
        for (int i = from; i <= to; i++) {
            store(id + "_" + i, world);
        }
    }

    private void generateNbt(Chunk region, BlockPos at, String id, BlockRotation rotation) {
        loadedStructures.get(id).rotate(rotation, blockLookup).generateNbt(region, at,
                (pos, state, nbt) -> this.modifyStructure(region, pos, state, nbt));
    }

    // TODO this code is in every chunk generator almost the same, so it should be reused
    private void modifyStructure(Chunk region, BlockPos pos, BlockState state, NbtCompound nbt) {
        if (!state.isAir()) {
            if (state.isOf(Blocks.BARRIER)) {
                region.setBlockState(pos, Blocks.AIR.getDefaultState(), true);
            } else {
                region.setBlockState(pos, state, true);
                if (state.isOf(Blocks.BARREL)) {
                    BarrelBlockEntity barrelBlockEntity = new BarrelBlockEntity(pos, state);
                    region.setBlockEntity(barrelBlockEntity);
                    barrelBlockEntity.setLootTable(this.getBarrelLootTable(),
                            BackroomsLevels.LEVEL_0_WORLD.getSeed() + pos.hashCode());
                } else if (state.isOf(BackroomsBlocks.CRATE)) {
                    CrateBlockEntity crateBlockEntity = new CrateBlockEntity(pos, state);
                    region.setBlockEntity(crateBlockEntity);
                    crateBlockEntity.setLootTable(this.getBarrelLootTable(),
                            BackroomsLevels.LEVEL_0_WORLD.getSeed() + pos.hashCode());
                }
            }
        }
    }

    protected Identifier getBarrelLootTable() {
        return BackroomsLootTables.CRATE;
    }

    private boolean isBiomeEquals(RegistryKey<Biome> biome, Chunk chunk, BlockPos biomePos) {
        return chunk.getBiomeForNoiseGen(biomePos.getX(), biomePos.getY(), biomePos.getZ()).matchesId(biome.getValue());
    }

    private void replace(Block block, Chunk chunk, BlockPos pos) {
        chunk.setBlockState(pos, block.getDefaultState(), false);
    }
}