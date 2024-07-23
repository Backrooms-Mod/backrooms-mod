package com.kpabr.backrooms.world.chunk.level1chunkgenerators;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.init.BackroomsBlocks;
import com.kpabr.backrooms.init.BackroomsLevels;
import com.kpabr.backrooms.init.BackroomsLootTables;
import com.kpabr.backrooms.util.NbtPlacerUtil;
import com.kpabr.backrooms.world.chunk.LevelOneChunkGenerator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.block.enums.RailShape;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

import net.minecraft.block.CandleBlock;
import net.minecraft.block.RailBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.FacingBlock;
import com.kpabr.backrooms.block.FiresaltCrystalBlock;
import com.kpabr.backrooms.block.entity.CrateBlockEntity;

import net.minecraft.block.WallMountedBlock;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep.Carver;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.noise.NoiseConfig;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class WarehouseChunkGenerator extends ChunkGenerator {
    public static final Codec<WarehouseChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            BiomeSource.CODEC.fieldOf("biome_source")
                    .stable()
                    .forGetter((chunkGenerator) -> chunkGenerator.biomeSource),
            Codec.LONG.fieldOf("seed")
                    .stable()
                    .forGetter((chunkGenerator) -> chunkGenerator.worldSeed),
            RegistryOps.getEntryLookupCodec(RegistryKeys.BLOCK))
            .apply(instance, instance.stable(WarehouseChunkGenerator::new)));

    private static final Block[] validCandles = new Block[] { Blocks.CANDLE, Blocks.YELLOW_CANDLE, Blocks.RED_CANDLE,
            Blocks.BLUE_CANDLE, Blocks.BROWN_CANDLE, Blocks.GREEN_CANDLE, Blocks.WHITE_CANDLE };
    private static final Block[] common = new Block[] { Blocks.STONE_BUTTON, Blocks.LEVER, Blocks.FLOWER_POT,
            Blocks.REDSTONE_TORCH, Blocks.RAIL, Blocks.STONE_PRESSURE_PLATE, Blocks.AIR, Blocks.WHITE_CONCRETE_POWDER,
            Blocks.BOOKSHELF };
    private static final Block[] uncommon = new Block[] { Blocks.COBWEB, Blocks.CAULDRON, Blocks.TORCH,
            Blocks.REDSTONE_WIRE, Blocks.CANDLE, Blocks.CRAFTING_TABLE, Blocks.FURNACE, Blocks.REPEATER,
            Blocks.COMPARATOR, Blocks.LOOM };
    private static final Block[] rare = new Block[] { Blocks.COMPOSTER, Blocks.SKELETON_SKULL, Blocks.ZOMBIE_HEAD,
            Blocks.BREWING_STAND, Blocks.DAYLIGHT_DETECTOR, Blocks.LANTERN, Blocks.PISTON, Blocks.OBSERVER,
            Blocks.LIGHTNING_ROD, BackroomsBlocks.FIRESALT_CRYSTAL, BackroomsBlocks.CRATE };
    private static final Block[] validPots = new Block[] { Blocks.POTTED_DANDELION, Blocks.POTTED_POPPY,
            Blocks.POTTED_BLUE_ORCHID, Blocks.POTTED_SPRUCE_SAPLING, Blocks.POTTED_CACTUS, Blocks.POTTED_RED_MUSHROOM,
            Blocks.POTTED_AZALEA_BUSH, Blocks.POTTED_BAMBOO };
    private static final Block[] validConcretePowder = new Block[] { Blocks.YELLOW_CONCRETE_POWDER,
            Blocks.RED_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER,
            Blocks.GREEN_CONCRETE_POWDER, Blocks.WHITE_CONCRETE_POWDER };
    private static final BlockState[] rails = new BlockState[] {
            Blocks.RAIL.getDefaultState().with(RailBlock.SHAPE, RailShape.NORTH_SOUTH),
            Blocks.RAIL.getDefaultState().with(RailBlock.SHAPE, RailShape.EAST_WEST)
    };
    private final long worldSeed;
    private static final int ROOF_BEGIN_Y = 6 * (LevelOneChunkGenerator.getFloorCount() + 1) + 1;

    private final HashMap<String, NbtPlacerUtil> loadedStructures = new HashMap<String, NbtPlacerUtil>(30);
    private Identifier nbtId = BackroomsMod.id("level_1");
    private RegistryEntryLookup<Block> blockLookup;

    public WarehouseChunkGenerator(BiomeSource biomeSource, long worldSeed, RegistryEntryLookup<Block> blockLookup) {
        super(biomeSource);
        this.worldSeed = worldSeed;
        this.blockLookup = blockLookup;
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, NoiseConfig noiseConfig,
            StructureAccessor structureAccessor, Chunk chunk) {
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
        // Save the starting x and z position of the chunk. Note: positive x means east,
        // positive z means south.
        final int startX = chunkPos.getStartX();
        final int startZ = chunkPos.getStartZ();
        // Create 5 floors, top to bottom.
        final int floorCount = LevelOneChunkGenerator.getFloorCount();

        if (this.loadedStructures.isEmpty()) {
            storeStructures(BackroomsLevels.LEVEL_1_WORLD);
        }

        // Create 4 smaller sections of the floor, layed out in a 2x2 pattern.
        // (located on the eastern and southern side of the section) and a pillar,
        // located in the southeasternmost space.
        for (int y = floorCount; y >= 0; y--) {
            for (int x = 1; x >= 0; x--) {
                for (int z = 1; z >= 0; z--) {
                    // Make a Random object controlling the generation of the section.
                    final Random random = new Random(worldSeed + BlockPos.asLong(startX, startZ, x + 4 * z + 20 * y));
                    // Decide the arrangement of the walls of the section.
                    // The two numbers with an F directly after them denote the probability of
                    // an eastern wall and a southern wall generating, respectively.
                    final int wallType = (random.nextFloat() < 0.4F ? 1 : 0) + (random.nextFloat() < 0.4F ? 2 : 0);
                    final int shelfType = 4 * (random.nextFloat() < 0.7F ? random.nextInt(4) + 1 : 0)
                            + random.nextInt(3);
                    // Same for the eastern adjacent section.
                    final Random eastRandom = new Random(
                            worldSeed + BlockPos.asLong(startX + x * 16, startZ, (1 - x) + 4 * z + 20 * y));
                    final int eastWallType = (eastRandom.nextFloat() < 0.4F ? 1 : 0)
                            + (eastRandom.nextFloat() < 0.4F ? 2 : 0);
                    final int eastShelfType = 4 * (eastRandom.nextFloat() < 0.7F ? eastRandom.nextInt(4) + 1 : 0)
                            + eastRandom.nextInt(3);
                    // Same for the southern adjacent section.
                    final Random southRandom = new Random(
                            worldSeed + BlockPos.asLong(startX, startZ + z * 16, x + 4 * (1 - z) + 20 * y));
                    final int southWallType = (southRandom.nextFloat() < 0.4F ? 1 : 0)
                            + (southRandom.nextFloat() < 0.4F ? 2 : 0);
                    final int southShelfType = 4 * (southRandom.nextFloat() < 0.7F ? southRandom.nextInt(4) + 1 : 0)
                            + southRandom.nextInt(3);
                    // Check if the arrangement includes the eastern wall
                    // and create eastern wall if it includes.
                    if ((wallType & 1) == 1) {
                        for (int i = 0; i < 7; i++) {
                            for (int j = 0; j < 6; j++) {
                                chunk.setBlockState(new BlockPos(startX + x * 8 + 7, 2 + 8 * y + j, startZ + z * 8 + i),
                                        BackroomsBlocks.CEMENT_BRICKS.getDefaultState(), false);
                            }
                        }
                    }
                    // Check if the arrangement includes the southern wall
                    // and create southern wall if it includes
                    if ((wallType & 2) == 2) {
                        for (int i = 0; i < 7; i++) {
                            for (int j = 0; j < 6; j++) {
                                chunk.setBlockState(new BlockPos(startX + x * 8 + i, 2 + 8 * y + j, startZ + z * 8 + 7),
                                        BackroomsBlocks.CEMENT_BRICKS.getDefaultState(), false);
                            }
                        }
                    }
                    // Create the pillar.
                    if ((wallType & 1) + (wallType & 2) / 2 + (southWallType & 1) + (eastWallType & 2) / 2 > 0) {
                        for (int j = 0; j < 6; j++) {
                            chunk.setBlockState(new BlockPos(startX + x * 8 + 7, 2 + 8 * y + j, startZ + z * 8 + 7),
                                    BackroomsBlocks.CEMENT_BRICKS.getDefaultState(), false);
                        }
                    }
                    if (shelfType - (shelfType & 3) != 0) {
                        final Direction dir = Direction.fromHorizontal((shelfType - (shelfType & 3)) / 4 - 1);
                        final BlockRotation rotation = switch (dir) {
                            case NORTH -> BlockRotation.COUNTERCLOCKWISE_90;
                            case EAST -> BlockRotation.NONE;
                            case SOUTH -> BlockRotation.CLOCKWISE_90;
                            default -> BlockRotation.CLOCKWISE_180;
                        };
                        generateNbt(chunk, new BlockPos(startX + x * 8, 2 + 8 * y, startZ + z * 8),
                                "warehouse_" + ((shelfType & 3) + 4), rotation); // Actually generate the shelf.
                        // commented former code: generateNbt(chunk, new BlockPos(startX + x * 8, 2 + 8
                        // * y, startZ + z * 8), "warehouse_5", BlockRotation.NONE);
                    }
                    // Generate the edges of the shelves.
                    if ((wallType & 1) == 0) {
                        shelfEdge(chunk, new BlockPos(startX + x * 8 + 7, 2 + 8 * y, startZ + z * 8 + 6),
                                Direction.EAST, 1, shelfType, eastShelfType);
                        shelfEdge(chunk, new BlockPos(startX + x * 8 + 7, 2 + 8 * y, startZ + z * 8), Direction.EAST, 3,
                                shelfType, eastShelfType);
                    }
                    if ((wallType & 2) == 0) {
                        shelfEdge(chunk, new BlockPos(startX + x * 8 + 6, 2 + 8 * y, startZ + z * 8 + 7),
                                Direction.SOUTH, 0, shelfType, southShelfType);
                        shelfEdge(chunk, new BlockPos(startX + x * 8, 2 + 8 * y, startZ + z * 8 + 7), Direction.SOUTH,
                                2, shelfType, southShelfType);
                    }
                    // blockState.with(FACING, direction)
                    // Generate the carpeting and the ceiling.
                    for (int i = 0; i < 8; i++) {
                        for (int j = 0; j < 8; j++) {
                            chunk.setBlockState(new BlockPos(startX + x * 8 + i, 1 + 8 * y, startZ + z * 8 + j),
                                    BackroomsBlocks.WOOLEN_CARPET.getDefaultState(), false);
                            chunk.setBlockState(new BlockPos(startX + x * 8 + i, 8 + 8 * y, startZ + z * 8 + j),
                                    BackroomsBlocks.CORK_TILE.getDefaultState(), false);
                            for (int k = 0; k < 6; k++) {
                                final BlockPos pos = new BlockPos(startX + x * 8 + i, 2 + 8 * y + k,
                                        startZ + z * 8 + j);
                                final BlockState state = chunk.getBlockState(pos);
                                if (state.isOf(Blocks.GOLD_BLOCK)) {
                                    final int blockType = random.nextInt(11);
                                    Block chosenBlock;
                                    if (blockType < 7) {
                                        chosenBlock = common[random.nextInt(common.length)];
                                    } else if (blockType < 10) {
                                        chosenBlock = uncommon[random.nextInt(uncommon.length)];
                                    } else {
                                        chosenBlock = rare[random.nextInt(rare.length)];
                                    }

                                    if (chosenBlock == Blocks.STONE_BUTTON || chosenBlock == Blocks.LEVER) {
                                        chunk.setBlockState(pos,
                                                chosenBlock.getDefaultState()
                                                        .with(WallMountedBlock.FACE, WallMountLocation.FLOOR)
                                                        .with(WallMountedBlock.FACING,
                                                                Direction.fromHorizontal(random.nextInt(4))),
                                                false);
                                    } else if (chosenBlock == Blocks.FLOWER_POT) {
                                        chunk.setBlockState(pos,
                                                validPots[random.nextInt(validPots.length)].getDefaultState(), false);
                                    } else if (chosenBlock == Blocks.WHITE_CONCRETE_POWDER) {
                                        chunk.setBlockState(pos,
                                                validConcretePowder[random.nextInt(validConcretePowder.length)]
                                                        .getDefaultState(),
                                                false);
                                    } else if (chosenBlock == BackroomsBlocks.FIRESALT_CRYSTAL) {
                                        chunk.setBlockState(pos, chosenBlock.getDefaultState()
                                                .with(FiresaltCrystalBlock.FACING, Direction.UP), false);
                                    } else if (chosenBlock == Blocks.CANDLE) {
                                        chunk.setBlockState(pos,
                                                validCandles[random.nextInt(validCandles.length)].getDefaultState()
                                                        .with(CandleBlock.LIT, true)
                                                        .with(CandleBlock.CANDLES, random.nextInt(4) + 1),
                                                false);
                                    } else if (chosenBlock == Blocks.REPEATER || chosenBlock == Blocks.COMPARATOR
                                            || chosenBlock == Blocks.FURNACE) {
                                        chunk.setBlockState(pos,
                                                chosenBlock.getDefaultState().with(HorizontalFacingBlock.FACING,
                                                        Direction.fromHorizontal(random.nextInt(4))),
                                                false);
                                    } else if (chosenBlock == Blocks.PISTON || chosenBlock == Blocks.OBSERVER) {
                                        chunk.setBlockState(pos, chosenBlock.getDefaultState().with(FacingBlock.FACING,
                                                Direction.fromHorizontal(random.nextInt(4))), false);
                                    } else if (chosenBlock == Blocks.RAIL) {
                                        chunk.setBlockState(pos, rails[random.nextInt(2)], false);
                                    } else if (chosenBlock == Blocks.SKELETON_SKULL
                                            || chosenBlock == Blocks.ZOMBIE_HEAD) {
                                        chunk.setBlockState(pos, chosenBlock.getDefaultState().with(SkullBlock.ROTATION,
                                                random.nextInt(16)), false);
                                    } else if (chosenBlock == BackroomsBlocks.CRATE) {
                                        chunk.setBlockState(pos, chosenBlock.getDefaultState(), false);
                                        CrateBlockEntity crateBlockEntity = new CrateBlockEntity(pos,
                                                chosenBlock.getDefaultState());
                                        chunk.setBlockEntity(crateBlockEntity);
                                        crateBlockEntity.setLootTable(this.getBarrelLootTable(),
                                                worldSeed + pos.hashCode());
                                    } else {
                                        replace(chunk, chosenBlock, chunk, pos);
                                    }
                                }
                            }
                        }
                    }
                    // Place a ceiling light at the correct height.
                    chunk.setBlockState(new BlockPos(startX + x * 8 + 3, 8 + 8 * y, startZ + z * 8 + 3),
                            BackroomsBlocks.FLUORESCENT_LIGHT.getDefaultState(), false); // Place a ceiling light.
                }
            }
        }
        return CompletableFuture.completedFuture(chunk);
    }

    protected Identifier getBarrelLootTable() {
        return BackroomsLootTables.CRATE;
    }

    public void storeStructures(ServerWorld world) {
        store("warehouse", world, 4, 6); // Makes it so the shelves can be used while generating
        store("warehouse_sliver", world, 4, 6);
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
    public int getWorldHeight() {
        return 128;
    }

    @Override
    public int getHeight(int x, int z, Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        return world.getTopY();
    }

    private void replace(Chunk region, Block block, Chunk chunk, BlockPos pos) {
        final BlockState oldState = chunk.setBlockState(pos, block.getDefaultState(), false);
        BackroomsLevels.LEVEL_1_WORLD.onBlockChanged(pos, oldState, block.getDefaultState());
    }

    private void replace(ChunkRegion region, Block block, Chunk chunk, BlockPos pos) {
        final BlockState oldState = chunk.setBlockState(pos, block.getDefaultState(), false);
        BackroomsLevels.LEVEL_1_WORLD.onBlockChanged(pos, oldState, block.getDefaultState());
    }

    private void shelfEdge(Chunk region, BlockPos pos, Direction direction, int shelfDirection, int shelfType,
            int adjacentShelfType) {
        if ((shelfType - (shelfType & 3)) / 4 - 1 == shelfDirection) {
            for (int j = 0; j < (shelfType & 3) + 4; j++) {
                region.setBlockState(pos.add(new BlockPos(0, j, 0)), Blocks.OAK_TRAPDOOR.getDefaultState()
                        .with(TrapdoorBlock.FACING, direction).with(TrapdoorBlock.OPEN, true), false);
            }
            if ((adjacentShelfType - (adjacentShelfType & 3)) / 4 - 1 == shelfDirection) {
                if ((adjacentShelfType & 3) != (shelfType & 3)) {
                    for (int j = 0; j < 6; j++) {
                        region.setBlockState(pos.add(new BlockPos(0, j, 0)),
                                BackroomsBlocks.CEMENT_PILLAR.getDefaultState(), false);
                    }
                } else {
                    generateNbt(region, pos, "warehouse_sliver_" + ((shelfType & 3) + 4));
                }
            }
        } else if ((adjacentShelfType - (adjacentShelfType & 3)) / 4 - 1 == shelfDirection) {
            for (int j = 0; j < (adjacentShelfType & 3) + 4; j++) {
                region.setBlockState(
                        pos.add(new BlockPos(0, j, 0)), Blocks.OAK_TRAPDOOR.getDefaultState()
                                .with(TrapdoorBlock.FACING, direction.getOpposite()).with(TrapdoorBlock.OPEN, true),
                        false);
            }
        }
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {
        final ChunkPos chunkPos = chunk.getPos();
        // controls every block up to the roof
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < ROOF_BEGIN_Y; y++) {
                    final BlockPos pos = chunkPos.getBlockPos(x, y, z);
                    final BlockState block = chunk.getBlockState(pos);

                    if (block == BackroomsBlocks.PATTERNED_WALLPAPER.getDefaultState()) {
                        replace(region, BackroomsBlocks.CEMENT_BRICKS, chunk, pos);
                    } else if (block == BackroomsBlocks.WOOLEN_CARPET.getDefaultState()) {
                        replace(region, BackroomsBlocks.CEMENT, chunk, pos);
                    } else if (block == BackroomsBlocks.MOLDY_WOOLEN_CARPET.getDefaultState()) {
                        replace(region, BackroomsBlocks.CEMENT, chunk, pos);
                    } else if (block == BackroomsBlocks.CORK_TILE.getDefaultState()) {
                        replace(region, BackroomsBlocks.CEMENT_TILES, chunk, pos);
                    } else if (block == BackroomsBlocks.MOLDY_CORK_TILE.getDefaultState()) {
                        replace(region, BackroomsBlocks.CEMENT_TILES, chunk, pos);
                    }
                }
            }
        }
    }

    public interface PentaConsumer<K, V, S, T, I> {
        void accept(K k, V v, S s, T t, I i);
    }

    private void generateNbt(Chunk region, BlockPos at, String id) {
        generateNbt(region, at, id, BlockRotation.NONE);
    }

    private void generateNbt(Chunk region, BlockPos at, String id, BlockRotation rotation) {
        loadedStructures.get(id).rotate(rotation, this.blockLookup).generateNbt(region, at,
                (pos, state, nbt) -> this.modifyStructure(region, pos, state, nbt));
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
    public int getMinimumY() {
        return 0;
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }

    @Override
    public void populateEntities(ChunkRegion region) {
    }

}
