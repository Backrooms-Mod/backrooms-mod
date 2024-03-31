package com.kpabr.backrooms.world.chunk;


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
import net.minecraft.util.Identifier;
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
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class LevelZeroChunkGenerator extends AbstractNbtChunkGenerator {
    public static final Codec<LevelZeroChunkGenerator> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source")
                            .stable()
                            .forGetter((chunkGenerator) -> chunkGenerator.biomeSource),
                    Codec.LONG.fieldOf("seed")
                            .stable()
                            .forGetter((chunkGenerator) -> chunkGenerator.worldSeed)
            ).apply(instance, instance.stable(LevelZeroChunkGenerator::new)));


    private final Random moldPlacementRandom;
    private final long worldSeed;
    private static final int ROOF_BEGIN_Y = 6 * (getFloorCount() + 1) + 1;
    private static final BlockState ROOF_BLOCK = BackroomsBlocks.BEDROCK_BRICKS.getDefaultState();

    public LevelZeroChunkGenerator(BiomeSource biomeSource, long worldSeed) {
        super(new SimpleRegistry<>(Registry.STRUCTURE_SET_KEY, Lifecycle.stable(), null), Optional.empty(), biomeSource, biomeSource, worldSeed, BackroomsMod.id("level_zero"), LiminalUtil.createMultiNoiseSampler());
        this.worldSeed = worldSeed;
        this.moldPlacementRandom = new Random(worldSeed);
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }
    @Override
    public ChunkGenerator withSeed(long seed) {
        return new LevelZeroChunkGenerator(this.biomeSource, seed);
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
        final BlockPos biomePos = chunkPos.getBlockPos(4, 4, 4);
        //Save the starting x and z position of the chunk. Note: positive x means east, positive z means south.
        final int startX = chunkPos.getStartX();
        final int startZ = chunkPos.getStartZ();

        if (isBiomeEquals(BackroomsLevels.MEGALOPHOBIA_BIOME, chunk, biomePos)) {
            //Create 3 floors, top to bottom, because one Megalophobia floor equals 6 normal floors.
        for (int y = 2; y >= 0; y--) {
            //Create 16 smaller sections of the floor, layed out in a 4x4 pattern. Each section will consist of the carpeting, the ceiling, two walls (located on the eastern and southern side of the section) and a pillar, located in the southeasternmost space.
            for (int x = 1; x >= 0; x--) {
                for (int z = 1; z >= 0; z--) {
                    //Make a Random object controlling the generation of the section.
                    final Random random = new Random(region.getSeed() + MathHelper.hashCode(startX, startZ, x + 4 * z + 20 * y));
                    //Decide the arrangement of the walls of the section. The two numbers with an F directly after them denote the probability of an eastern wall and a southern wall generating, respectively.
                    final int wallType = (random.nextFloat() < 0.4F ? 1 : 0) + (random.nextFloat() < 0.4F ? 2 : 0);

                    //Check if the arrangement includes the eastern wall.
                    // and create eastern wall if true
                    if ((wallType & 1) == 1) {
                        for(int i = 0; i < 7; i++){
                            for(int j = 0; j < 10; j++) {
                                for(int k = 0; k < 2; k++){
                                    region.setBlockState(
                                        new BlockPos(startX + x * 8 + 6 + k, 2 + 12 * y + j, startZ + z * 8 + i),
                                        BackroomsBlocks.PATTERNED_WALLPAPER.getDefaultState(),
                                        Block.FORCE_STATE,
                                        0);
                                }
                            }
                        }
                    }
                    // Check if the arrangement includes the southern wall
                    // and create southern wall if true
                    if ((wallType & 2) == 2) {
                        for(int i = 0; i < 7; i++){
                            for(int j = 0; j < 10; j++){
                                for(int k = 0; k < 2; k++){
                                    region.setBlockState(
                                        new BlockPos(startX + x * 8 + i, 2 + 12 * y + j, startZ + z * 8 + 6 + k),
                                        BackroomsBlocks.PATTERNED_WALLPAPER.getDefaultState(),
                                        Block.FORCE_STATE,
                                        0);
                                }
                            }
                        }
                    }
                    // New variable controlling whether a pillar in generated.
                    // If there's a wall in the current section, always create a pillar.
                    boolean pillar = wallType != 0;

                    //If you're on the southeasternmost spot on the chunk, always make a pillar.
                    pillar = pillar || (x == 1 && z == 1);

                    // Check if you're not on the eastern edge of the chunk. If you aren't, proceed.
                    // Check one block east whether there's a wall there. If so, a pillar will always be generated.
                    if(x != 1) {
                        pillar = pillar ||
                                !region.getBlockState(new BlockPos(startX + x * 8 + 8, 2 + 12 * y, startZ + z * 8 + 7)).isAir();
                    }
                    // Check if you're not on the southern edge of the chunk. If you aren't, proceed.
                    if(z != 1) {
                        // Check one block south whether there's a wall there. If so, a pillar will always be generated.
                        pillar = pillar || !region.getBlockState(new BlockPos(startX + x * 8 + 7, 2 + 12 * y, startZ + z * 8 + 8)).isAir();
                    }
                    pillar = pillar || (random.nextFloat() < 0.2F); //Sometimes generate a pillar anyways, even if none of the previous conditions were met.
                    if (pillar) {
                        //Create the pillar.
                        for (int i = 0; i < 10; i++) {
                            for (int j = 0; j < 2; j++) {
                                for (int k = 0; k < 2; k++) {
                                    region.setBlockState(new BlockPos(startX + x * 8 + 6 + j, 2 + 12 * y + i, startZ + z * 8 + 6 + k), BackroomsBlocks.PATTERNED_WALLPAPER.getDefaultState(), Block.FORCE_STATE, 0);
                                }
                            }
                        }
                    }
                    // Generate the carpeting and the ceiling.
                    for(int i = 0; i < 8; i++) {
                        for(int j = 0; j < 8; j++){
                            region.setBlockState(new BlockPos(startX + x * 8 + i, 1 + 12 * y, startZ + z * 8 + j), BackroomsBlocks.WOOLEN_CARPET.getDefaultState(), Block.FORCE_STATE, 0);
                            region.setBlockState(new BlockPos(startX + x * 8 + i, 12 + 12 * y, startZ + z * 8 + j), BackroomsBlocks.CORK_TILE.getDefaultState(), Block.FORCE_STATE, 0);
                        }
                    }
                    //Place a ceiling light.
                    region.setBlockState(new BlockPos(startX + x * 8 + 2, 12 + 12 * y, startZ + z * 8 + 2), BackroomsBlocks.FLUORESCENT_LIGHT.getDefaultState(), Block.FORCE_STATE, 0);
                    region.setBlockState(new BlockPos(startX + x * 8 + 2, 12 + 12 * y, startZ + z * 8 + 3), BackroomsBlocks.FLUORESCENT_LIGHT.getDefaultState(), Block.FORCE_STATE, 0);
                    region.setBlockState(new BlockPos(startX + x * 8 + 3, 12 + 12 * y, startZ + z * 8 + 2), BackroomsBlocks.FLUORESCENT_LIGHT.getDefaultState(), Block.FORCE_STATE, 0);
                    region.setBlockState(new BlockPos(startX + x * 8 + 3, 12 + 12 * y, startZ + z * 8 + 3), BackroomsBlocks.FLUORESCENT_LIGHT.getDefaultState(), Block.FORCE_STATE, 0);
                }
            }
        }

        //Mold placement code; will be subject to heavy revisions, so ignore for now.
        for (int y = getFloorCount(); y >= 0; y--) {
            final Random fullFloorRandom = new Random(region.getSeed()
                    + MathHelper.hashCode(chunk.getPos().getStartX(), chunk.getPos().getStartZ(), y));

            for(int i = 0 ; i < 300; i++){
                final int x = fullFloorRandom.nextInt(16);
                final int z = fullFloorRandom.nextInt(16);
                int x2 = x + fullFloorRandom.nextInt(3) - 1;
                int z2 = fullFloorRandom.nextInt(3) - 1;
                if(region.getBlockState(new BlockPos(startX + x, 1 + 12 * y, startZ + z))
                        == BackroomsBlocks.WOOLEN_CARPET.getDefaultState()){
                    if(x2 < 0) x2=0;
                    else if(x2 > 15) x2=15;
                    if(z2 < 0) z2=0;
                    else if(z2 > 15) z2=15;
                    if(fullFloorRandom.nextFloat() < 0.1F || region.getBlockState(new BlockPos(startX + x2, 1 + 12 * y, startZ + z2)) == BackroomsBlocks.CORK_TILE.getDefaultState()) {
                        region.setBlockState(
                                new BlockPos(startX + x, 1 + 12 * y, startZ + z),
                                BackroomsBlocks.MOLDY_WOOLEN_CARPET.getDefaultState(),
                                Block.FORCE_STATE,
                                0);
                    }
                }
            }
        }
        // Place bedrock bricks at the bottom.
        for (int x = startX; x < startX + 16; x++) {
            for (int z = startZ; z < startZ + 16; z++) {
                region.setBlockState(new BlockPos(x, 0, z), ROOF_BLOCK, Block.FORCE_STATE, 0);
            }
        }
        // Place bedrock bricks at the roof of chunk
        for (int x = startX; x < startX + 16; x++) {
            for (int z = startZ; z < startZ + 16; z++) {
                region.setBlockState(new BlockPos(x, ROOF_BEGIN_Y, z), ROOF_BLOCK, Block.FORCE_STATE, 0);
            }
        }
        return CompletableFuture.completedFuture(chunk);
        } else {
        //Create 5 floors, top to bottom.
        for (int y = getFloorCount(); y >= 0; y--) {
            //Create 16 smaller sections of the floor, layed out in a 4x4 pattern. Each section will consist of the carpeting, the ceiling, two walls (located on the eastern and southern side of the section) and a pillar, located in the southeasternmost space.
            for (int x = 3; x >= 0; x--) {
                for (int z = 3; z >= 0; z--) {
                    //Make a Random object controlling the generation of the section.
                    final Random random = new Random(region.getSeed() + MathHelper.hashCode(startX, startZ, x + 4 * z + 20 * y));
                    //Decide the arrangement of the walls of the section. The two numbers with an F directly after them denote the probability of an eastern wall and a southern wall generating, respectively.
                    final int wallType = (random.nextFloat() < 0.4F ? 1 : 0) + (random.nextFloat() < 0.4F ? 2 : 0);

                    //Check if the arrangement includes the eastern wall.
                    // and create eastern wall if true
                    if ((wallType & 1) == 1) {
                        for(int i = 0; i < 3; i++){
                            for(int j = 0; j < 4; j++){
                                region.setBlockState(
                                        new BlockPos(startX + x * 4 + 3, 2 + 6 * y + j, startZ + z * 4 + i),
                                        BackroomsBlocks.PATTERNED_WALLPAPER.getDefaultState(),
                                        Block.FORCE_STATE,
                                        0);
                            }
                        }
                    }
                    // Check if the arrangement includes the southern wall
                    // and create southern wall if true
                    if ((wallType & 2) == 2) {
                        for(int i = 0; i < 3; i++){
                            for(int j = 0; j < 4; j++){
                                region.setBlockState(
                                        new BlockPos(startX + x * 4 + i, 2 + 6 * y + j, startZ + z * 4 + 3),
                                        BackroomsBlocks.PATTERNED_WALLPAPER.getDefaultState(),
                                        Block.FORCE_STATE,
                                        0);
                            }
                        }
                    }
                    // New variable controlling whether a pillar in generated.
                    // If there's a wall in the current section, always create a pillar.
                    boolean pillar = wallType != 0;

                    //If you're on the southeasternmost spot on the chunk, always make a pillar.
                    pillar = pillar || (x == 3 && z == 3);

                    // Check if you're not on the eastern edge of the chunk. If you aren't, proceed.
                    // Check one block east whether there's a wall there. If so, a pillar will always be generated.
                    if(x != 3) {
                        pillar = pillar ||
                                !region.getBlockState(new BlockPos(startX + x * 4 + 4, 2 + 6 * y, startZ + z * 4 + 3)).isAir();
                    }
                    // Check if you're not on the southern edge of the chunk. If you aren't, proceed.
                    if(z != 3) {
                        // Check one block south whether there's a wall there. If so, a pillar will always be generated.
                        pillar = pillar || !region.getBlockState(new BlockPos(startX + x * 4 + 3, 2 + 6 * y, startZ + z * 4 + 4)).isAir();
                    }
                    pillar = pillar || (random.nextFloat() < 0.2F); //Sometimes generate a pillar anyways, even if none of the previous conditions were met.
                    if (pillar) {
                        //Create the pillar.
                        for (int i = 0; i < 4; i++) {
                            region.setBlockState(new BlockPos(startX + x * 4 + 3, 2 + 6 * y + i, startZ + z * 4 + 3), BackroomsBlocks.PATTERNED_WALLPAPER.getDefaultState(), Block.FORCE_STATE, 0);
                        }
                    }
                    // Generate the carpeting and the ceiling.
                    for(int i = 0; i < 4; i++) {
                        for(int j = 0; j < 4; j++){
                            region.setBlockState(new BlockPos(startX + x * 4 + i, 1 + 6 * y, startZ + z * 4 + j), BackroomsBlocks.WOOLEN_CARPET.getDefaultState(), Block.FORCE_STATE, 0);
                            region.setBlockState(new BlockPos(startX + x * 4 + i, 6 + 6 * y, startZ + z * 4 + j), BackroomsBlocks.CORK_TILE.getDefaultState(), Block.FORCE_STATE, 0);
                        }
                    }
                    //Place a ceiling light.
                    region.setBlockState(new BlockPos(startX + x * 4 + 1, 6 + 6 * y, startZ + z * 4 + 1), BackroomsBlocks.FLUORESCENT_LIGHT.getDefaultState(), Block.FORCE_STATE, 0);
                }
            }

            // Create an unique random Object for the current floor.
            final Random fullFloorRandom = new Random(region.getSeed() + MathHelper.hashCode(chunk.getPos().getStartX(), chunk.getPos().getStartZ(), y));
            // Check whether a random number between zero and one is less than the number with an F directly after it. Currently, for debugging reasons, a "|| true" has been placed, which means that the following code will be excecuted anyways.
            // Place a large (7x7 or bigger) room in the current chunk at the current floor. Both dimensions of the base of the room must be of the form 4x-1.
            if (fullFloorRandom.nextFloat() < 0.1F || true) {
                //Define the amounts of regular and nofill rooms.
                final int regularRooms = 14;
                final int nofillRooms = 4;
                //Choose the room that will be placed.
                int roomNumber = (fullFloorRandom.nextInt(regularRooms + nofillRooms) + 1);
                //The number with an F directly after it denotes the probability of an empty room being generated regardless.
                if(fullFloorRandom.nextFloat() < 0.6F) {
                    roomNumber = 0;
                }
                String roomName = "backrooms_large_" + roomNumber;
                if(roomNumber > regularRooms) {
                    roomName = "backrooms_large_nofill_" + (roomNumber - regularRooms);
                }
                //Choose the rotation for the room.
                Direction dir = Direction.fromHorizontal(fullFloorRandom.nextInt(4));
                BlockRotation rotation = switch(dir) {
                    case NORTH -> BlockRotation.COUNTERCLOCKWISE_90;
                    case EAST -> BlockRotation.NONE;
                    case SOUTH -> BlockRotation.CLOCKWISE_90;
                    default -> BlockRotation.CLOCKWISE_180;
                };

                // Calculate size of current room
                final var currentRoom = this.loadedStructures.get(roomName);

                int sizeY = currentRoom.sizeY, sizeX, sizeZ;
                final boolean isEastOrWestDirection = dir.equals(Direction.EAST) || dir.equals(Direction.WEST);
                if(isEastOrWestDirection) {
                    sizeX = currentRoom.sizeX;
                    sizeZ = currentRoom.sizeZ;
                } else {
                    sizeX = currentRoom.sizeZ;
                    sizeZ = currentRoom.sizeX;
                }
                // Place a structure only if it fits before the bedrock
                if(6 * y + sizeY < ROOF_BEGIN_Y) {
                    //Choose a spot in the chunk.
                    final int x = fullFloorRandom.nextInt(5 - (sizeX + 1) / 4);
                    final int z = fullFloorRandom.nextInt(5 - (sizeZ + 1) / 4);
                    //Fill the area the room will be placed in with air.
                    if(roomNumber <= regularRooms) {
                        for (int i = 0; i < sizeX; i++) {
                            for (int j = 0; j < sizeY; j++) {
                                for (int k = 0; k < sizeZ; k++) {
                                    region.setBlockState(
                                            new BlockPos(startX + x * 4 + i, 2 + 6 * y + j, startZ + z * 4 + k),
                                            Blocks.AIR.getDefaultState(),
                                            Block.FORCE_STATE,
                                            0);
                                }
                            }
                        }
                    }
                    generateNbt(region, new BlockPos(startX + x * 4, 2 + 6 * y, startZ + z * 4), roomName, rotation); //Actually generate the room.
                }
            }
        }

        //Mold placement code; will be subject to heavy revisions, so ignore for now.
        for (int y = getFloorCount(); y >= 0; y--) {
            final Random fullFloorRandom = new Random(region.getSeed()
                    + MathHelper.hashCode(chunk.getPos().getStartX(), chunk.getPos().getStartZ(), y));

            for(int i = 0 ; i < 300; i++){
                final int x = fullFloorRandom.nextInt(16);
                final int z = fullFloorRandom.nextInt(16);
                int x2 = x + fullFloorRandom.nextInt(3) - 1;
                int z2 = fullFloorRandom.nextInt(3) - 1;
                if(region.getBlockState(new BlockPos(startX + x, 1 + 6 * y, startZ + z))
                        == BackroomsBlocks.WOOLEN_CARPET.getDefaultState()){
                    if(x2 < 0) x2=0;
                    else if(x2 > 15) x2=15;
                    if(z2 < 0) z2=0;
                    else if(z2 > 15) z2=15;
                    if(fullFloorRandom.nextFloat() < 0.1F || region.getBlockState(new BlockPos(startX + x2, 1 + 6 * y, startZ + z2)) == BackroomsBlocks.CORK_TILE.getDefaultState()) {
                        region.setBlockState(
                                new BlockPos(startX + x, 1 + 6 * y, startZ + z),
                                BackroomsBlocks.MOLDY_WOOLEN_CARPET.getDefaultState(),
                                Block.FORCE_STATE,
                                0);
                    }
                }
            }
        }
        // Place bedrock bricks at the bottom.
        for (int x = startX; x < startX + 16; x++) {
            for (int z = startZ; z < startZ + 16; z++) {
                region.setBlockState(new BlockPos(x, 0, z), ROOF_BLOCK, Block.FORCE_STATE, 0);
            }
        }
        // Place bedrock bricks at the roof of chunk
        for (int x = startX; x < startX + 16; x++) {
            for (int z = startZ; z < startZ + 16; z++) {
                region.setBlockState(new BlockPos(x, ROOF_BEGIN_Y, z), ROOF_BLOCK, Block.FORCE_STATE, 0);
            }
        }
        return CompletableFuture.completedFuture(chunk);}
    }

    public static int getFloorCount() {
        return 5;
    }

    @Override
    public void storeStructures(ServerWorld world) {
        store("backrooms_large", world, 0, 14); //Makes it so the large regular rooms can be used while generating.
        store("backrooms_large_nofill", world, 1, 4); //Makes it so the large nofill rooms can be used while generating.
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

    private void replace(Block block, Chunk chunk, BlockPos pos) {
        chunk.setBlockState(pos, block.getDefaultState(), false);
    }

    private boolean isBiomeEquals(RegistryKey<Biome> biome, Chunk chunk, BlockPos biomePos) {
        return chunk.getBiomeForNoiseGen(biomePos.getX(), biomePos.getY(), biomePos.getZ()).matchesId(biome.getValue());
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structureAccessor, Chunk chunk) {
        final ChunkPos chunkPos = chunk.getPos();
        final BlockPos biomePos = chunkPos.getBlockPos(4, 4, 4);

        // controls every block up to the roof
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < ROOF_BEGIN_Y; y++) {
                    // does a swap from the various stones to the custom blocks
                    if(isBiomeEquals(BackroomsLevels.CRIMSON_WALLS_BIOME, chunk, biomePos)) {
                        final BlockPos pos = chunkPos.getBlockPos(x, y, z);
                        final BlockState block = chunk.getBlockState(pos);
                        if (block == BackroomsBlocks.PATTERNED_WALLPAPER.getDefaultState()) {
                            replace(BackroomsBlocks.RED_PATTERNED_WALLPAPER, chunk, pos);
                        }
                        else if (block.getBlock() == BackroomsBlocks.WOOLEN_CARPET
                                || block == BackroomsBlocks.MOLDY_WOOLEN_CARPET.getDefaultState()) {

                            replace(BackroomsBlocks.RED_CARPETING, chunk, pos);
                        }
                        else if (block == BackroomsBlocks.CORK_TILE.getDefaultState()
                                && moldPlacementRandom.nextDouble() < BackroomsConfig.getInstance().moldyCorkTileChance) {

                            replace(BackroomsBlocks.MOLDY_CORK_TILE, chunk, pos);
                        }
                    }
                    else if(isBiomeEquals(BackroomsLevels.DECREPIT_BIOME, chunk, biomePos)) {
                        final BlockPos pos = chunkPos.getBlockPos(x, y, z);
                        final BlockState block = chunk.getBlockState(pos);

                        if (block == BackroomsBlocks.WOOLEN_CARPET.getDefaultState()) {
                            replace(BackroomsBlocks.MOLDY_WOOLEN_CARPET, chunk, pos);
                        } else if (block == BackroomsBlocks.FLUORESCENT_LIGHT.getDefaultState()) {
                            replace(BackroomsBlocks.REPAIRED_FLUORESCENT_LIGHT, chunk, pos);
                        }
                    }
                }
            }
        }
    }
}
