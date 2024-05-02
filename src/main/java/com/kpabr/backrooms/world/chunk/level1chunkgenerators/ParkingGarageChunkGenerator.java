package com.kpabr.backrooms.world.chunk.level1chunkgenerators;


import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.init.BackroomsBlocks;
import com.kpabr.backrooms.init.BackroomsLevels;
import com.kpabr.backrooms.util.NbtPlacerUtilNew;
import com.kpabr.backrooms.world.chunk.LevelOneChunkGenerator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil.MultiNoiseSampler;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep.Carver;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ParkingGarageChunkGenerator extends ChunkGenerator {
    public static final Codec<ParkingGarageChunkGenerator> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source")
                            .stable()
                            .forGetter((chunkGenerator) -> chunkGenerator.biomeSource),
                    Codec.LONG.fieldOf("seed")
                            .stable()
                            .forGetter((chunkGenerator) -> chunkGenerator.worldSeed)
            ).apply(instance, instance.stable(ParkingGarageChunkGenerator::new))
    );

    private final long worldSeed;
    private static final int ROOF_BEGIN_Y = 8 * (LevelOneChunkGenerator.getFloorCount() + 1) + 1;

    private final HashMap<String, NbtPlacerUtilNew> loadedStructures = new HashMap<String, NbtPlacerUtilNew>(30);
    private Identifier nbtId = BackroomsMod.id("level_1");
    
    
    public ParkingGarageChunkGenerator(BiomeSource biomeSource, long worldSeed) {
        super(new SimpleRegistry<>(Registry.STRUCTURE_SET_KEY, Lifecycle.stable(), null), Optional.empty(), biomeSource, biomeSource, worldSeed);
        this.worldSeed = worldSeed;
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }
    @Override
    public ChunkGenerator withSeed(long seed) {
        return new ParkingGarageChunkGenerator(this.biomeSource, seed);
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, StructureAccessor structureAccessor, Chunk chunk) {
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
        // Save the starting x and z position of the chunk. Note: positive x means east, positive z means south.
        final int startX = chunkPos.getStartX();
        final int startZ = chunkPos.getStartZ();

        if (this.loadedStructures.isEmpty()) {
            storeStructures(BackroomsLevels.LEVEL_1_WORLD);
        }

        // Create 5 floors, top to bottom.
        final int floorCount = LevelOneChunkGenerator.getFloorCount();
        for (int y = floorCount; y >= 0; y--) {
            //Create 16 smaller sections of the floor, layed out in a 4x4 pattern. Each section will consist of the carpeting, the ceiling, two walls (located on the eastern and southern side of the section) and a pillar, located in the southeasternmost space.
            for (int x = 3; x >= 0; x--) {
                for (int z = 3; z >= 0; z--) {
                    if((x & 1) == 1){
                        //Create a pillar.
                        chunk.setBlockState(new BlockPos(startX + x * 4 + 3, 2 + 8 * y, startZ + z * 4 + 3), BackroomsBlocks.PARKING_CEMENT_PILLAR.getDefaultState(), false);
                        chunk.setBlockState(new BlockPos(startX + x * 4 + 3, 3 + 8 * y, startZ + z * 4 + 3), BackroomsBlocks.PARKING_CEMENT_PILLAR.getDefaultState(), false);
                        chunk.setBlockState(new BlockPos(startX + x * 4 + 3, 4 + 8 * y, startZ + z * 4 + 3), BackroomsBlocks.CEMENT_PILLAR.getDefaultState(), false);
                        chunk.setBlockState(new BlockPos(startX + x * 4 + 3, 5 + 8 * y, startZ + z * 4 + 3), BackroomsBlocks.CEMENT_PILLAR.getDefaultState(), false);
                    }
                    // Generate the carpeting and the ceiling.
                    for(int i = 0; i < 4; i++){
                        for(int j = 0; j < 4; j++){
                            chunk.setBlockState(new BlockPos(startX + x * 4 + i, 1 + 8 * y, startZ + z * 4 + j), BackroomsBlocks.WOOLEN_CARPET.getDefaultState(), false);
                            for(int k = 0; k < 3; k++) {
                                chunk.setBlockState(new BlockPos(startX + x * 4 + i, 6 + 8 * y + k, startZ + z * 4 + j), BackroomsBlocks.CORK_TILE.getDefaultState(), false);
                            }
                        }
                    }
                    //Place a ceiling light at the correct height.
                    chunk.setBlockState(new BlockPos(startX + x * 4 + 1, 6 + 8 * y, startZ + z * 4 + 1), BackroomsBlocks.FLUORESCENT_LIGHT.getDefaultState(), false);
                }
            }
            //Create an unique Random object for the current floor.
            final Random fullFloorRandom = new Random(worldSeed + MathHelper.hashCode(startX, startZ, y));

            if(fullFloorRandom.nextFloat() < 0.1F){ //Check whether a random number between zero and one is less than the number with an F directly after it. Currently, for debugging reasons, a "|| true" has been placed, which means that the following code will be excecuted anyways.
                final int regularRooms = 1;
                final int noFillRooms = 0;
                //Choose the room that will be placed.
                int roomNumber = (fullFloorRandom.nextInt(regularRooms + noFillRooms));
                String roomName = "parking_garage_" + roomNumber;
                if(roomNumber >= regularRooms) {
                    roomName = "parking_garage_nofill_" + (roomNumber - regularRooms);
                }
                //Choose the rotation for the room.
                Direction dir = Direction.fromHorizontal(fullFloorRandom.nextInt(4));
                if(roomNumber==0){
                    dir = Direction.fromHorizontal(fullFloorRandom.nextInt(2)*2+1);
                }
                BlockRotation rotation = switch(dir) {
                    case NORTH -> BlockRotation.COUNTERCLOCKWISE_90;
                    case EAST -> BlockRotation.NONE;
                    case SOUTH -> BlockRotation.CLOCKWISE_90;
                    default -> BlockRotation.CLOCKWISE_180;
                };

                // Get size of current room.
                final var currentRoom = this.loadedStructures.get(roomName);
                final boolean isEastOrWestDirection = dir.equals(Direction.EAST) || dir.equals(Direction.WEST);
                int sizeY = currentRoom.sizeY, sizeX, sizeZ;

                if(isEastOrWestDirection) {
                    sizeX = currentRoom.sizeX;
                    sizeZ = currentRoom.sizeZ;
                } else {
                    sizeX = currentRoom.sizeZ;
                    sizeZ = currentRoom.sizeX;
                }

                // Only generate the structure if it has enough vertical space to generate.
                if (8 * y + sizeY < ROOF_BEGIN_Y) {
                    //Choose a spot in the chunk.
                    int x = fullFloorRandom.nextInt(5 - (sizeX + 1) / 4);
                    int z = fullFloorRandom.nextInt(5 - (sizeZ + 1) / 4);
                    if(roomNumber==0){
                        x = fullFloorRandom.nextInt(2)*2;
                    }
                    //Fill the area the room will be placed in with air.
                    if(roomNumber <= regularRooms) {
                        for (int i = 0; i < sizeX; i++) {
                            for (int j = 0; j < sizeY; j++) {
                                for (int k = 0; k < sizeZ; k++) {
                                    chunk.setBlockState(new BlockPos(startX + x * 4 + i, 2 + 8 * y + j, startZ + z * 4 + k), Blocks.AIR.getDefaultState(), false);
                                }
                            }
                        }
                    }
                    // Actually generate the room.
                    generateNbt(chunk, new BlockPos(startX + x * 4, 2 + 8 * y, startZ + z * 4), roomName, rotation);
                }
            }
        }
        return CompletableFuture.completedFuture(chunk);
    }

    public void storeStructures(ServerWorld world) {
        store("parking_garage", world, 0, 0); //Makes it so the large regular rooms can be used while generating.
        //store("parking_garage_nofill", world, 0, 0); //Makes it so the large nofill rooms can be used while generating.*/
    }

    private void store(String id, ServerWorld world) {
		loadedStructures.put(id, NbtPlacerUtilNew.load(world.getServer().getResourceManager(), new Identifier(this.nbtId.getNamespace(), "nbt/" + this.nbtId.getPath() + "/" + id + ".nbt")).get());
	}

	private void store(String id, ServerWorld world, int from, int to) {
		for (int i = from; i <= to; i++) {
			store(id + "_" + i, world);
		}
	}

	private void generateNbt(Chunk region, BlockPos at, String id, BlockRotation rotation) {
		loadedStructures.get(id).rotate(rotation).generateNbt(region, at, (pos, state, nbt) -> this.modifyStructure(region, pos, state, nbt));
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
    public int getHeight(int x, int y, Heightmap.Type type, HeightLimitView world) {
        return world.getTopY();
    }

    private void replace(Block block, Chunk chunk, BlockPos pos) {
        chunk.setBlockState(pos, block.getDefaultState(), false);
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

                    if (block == BackroomsBlocks.PATTERNED_WALLPAPER.getDefaultState()) {
                        replace(BackroomsBlocks.CEMENT_BRICKS, chunk, pos);
                    } else if (block == BackroomsBlocks.WOOLEN_CARPET.getDefaultState()
                            || block == BackroomsBlocks.MOLDY_WOOLEN_CARPET.getDefaultState()) {
                        replace(BackroomsBlocks.CEMENT, chunk, pos);
                    } else if (block == BackroomsBlocks.CORK_TILE.getDefaultState()
                            || block == BackroomsBlocks.MOLDY_CORK_TILE.getDefaultState()) {
                        replace(BackroomsBlocks.CEMENT_TILES, chunk, pos);
                    }
                }
            }
        }
    }

     @Override
    public void carve(ChunkRegion chunkRegion, long seed, BiomeAccess biomeAccess, StructureAccessor structureAccessor,
            Chunk chunk, Carver generationStep) {}

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world) {
        return new VerticalBlockSample(0, new BlockState[0]);
    }

    @Override
    public void getDebugHudText(List<String> text, BlockPos pos) {}

    @Override
    public int getMinimumY() {
        return 0;
    }

    @Override
    public MultiNoiseSampler getMultiNoiseSampler() {
        return null;
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }

    @Override
    public void populateEntities(ChunkRegion region) {}
}
