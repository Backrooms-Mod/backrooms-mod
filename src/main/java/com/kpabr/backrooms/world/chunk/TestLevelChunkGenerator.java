package com.kpabr.backrooms.world.chunk;


import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import com.kpabr.backrooms.BackroomsMod;
import net.ludocrypt.limlib.api.LiminalUtil;
import net.ludocrypt.limlib.api.world.AbstractNbtChunkGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import com.kpabr.backrooms.init.BackroomsBlocks;
import net.minecraft.loot.LootTables;
import net.minecraft.server.world.ChunkHolder.Unloaded;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.BlockRotation;
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
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class TestLevelChunkGenerator extends AbstractNbtChunkGenerator {
    public static final Codec<TestLevelChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(BiomeSource.CODEC.fieldOf("biome_source").stable().forGetter((chunkGenerator) -> {
            return chunkGenerator.biomeSource;
        }), Codec.LONG.fieldOf("seed").stable().forGetter((chunkGenerator) -> {
            return chunkGenerator.worldSeed;
        })).apply(instance, instance.stable(TestLevelChunkGenerator::new));
    });


    private long worldSeed;

    public TestLevelChunkGenerator(BiomeSource biomeSource, long worldSeed) {
        super(new SimpleRegistry<StructureSet>(Registry.STRUCTURE_SET_KEY, Lifecycle.stable(), null), Optional.empty(), biomeSource, biomeSource, worldSeed, BackroomsMod.id("test_level"), LiminalUtil.createMultiNoiseSampler());
        this.worldSeed = worldSeed;
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public ChunkGenerator withSeed(long seed) {
        return new TestLevelChunkGenerator(this.biomeSource, seed);
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(ChunkRegion region, ChunkStatus targetStatus, Executor executor, ServerWorld world, ChunkGenerator generator, StructureManager structureManager, ServerLightingProvider lightingProvider, Function<Chunk, CompletableFuture<Either<Chunk, Unloaded>>> function, List<Chunk> chunks, Chunk chunk, boolean bl) {
        ChunkPos chunkPos = chunk.getPos();
        //Save the starting x and z position of the chunk. Note: positive x means east, positive z means south.
        int startX = chunk.getPos().getStartX();
        int startZ = chunk.getPos().getStartZ();
        //Create 5 floors, top to bottom.
        for (int y = 5; y >= 0; y--) {
            //Create 16 smaller sections of the floor, layed out in a 4x4 pattern. Each section will consist of the carpeting, the ceiling, two walls (located on the eastern and southern side of the section) and a pillar, located in the southeasternmost space.
            for (int x = 3; x >= 0; x--) {
                for (int z = 3; z >= 0; z--) {
                    Random random = new Random(region.getSeed() + MathHelper.hashCode(chunk.getPos().getStartX(), chunk.getPos().getStartZ(), x + 4 * z + 20 * y)); //Make a Random object controlling the generation of the section.
                    int wallType = (random.nextFloat() < 0.4F ? 1 : 0) + (random.nextFloat() < 0.4F ? 2 : 0); //Decide the arrangement of the walls of the section. The two numbers with an F directly after them denote the probability of an eastern wall and a southern wall generating, respectively.
                    if((wallType & 1) == 1){ //Check if the arrangement includes the eastern wall.
                        //Create the eastern wall.
                        for(int i = 0; i < 3; i++){
                            for(int j = 0; j < 4; j++){
                                region.setBlockState(new BlockPos(startX + x * 4 + 3 , 2 + 6 * y + j, startZ + z * 4 + i), BackroomsBlocks.PATTERNED_WALLPAPER.getDefaultState(), Block.FORCE_STATE, 0);
                            }
                        }
                    }
                    if((wallType & 2) == 2){ //Check if the arrangement includes the southern wall.
                        //Create the southern wall.
                        for(int i = 0; i < 3; i++){
                            for(int j = 0; j < 4; j++){
                                region.setBlockState(new BlockPos(startX + x * 4 + i, 2 + 6 * y + j, startZ + z * 4 + 3), BackroomsBlocks.PATTERNED_WALLPAPER.getDefaultState(), Block.FORCE_STATE, 0);
                            }
                        }
                    }
                    boolean pillar = false; //New variable controlling whether a pillar in generated. Initially false.
                    if(wallType != 0){ //If there's a wall in the current section, always create a pillar.
                        pillar = true;
                    }
                    if(x != 3){ //Check if you're not on the eastern edge of the chunk. If you aren't, proceed.
                        if(region.getBlockState(new BlockPos(startX + x * 4 + 4, 2 + 6 * y, startZ + z * 4 + 3))!=Blocks.AIR.getDefaultState()){ //Check one block east whether there's a wall there. If so, a pillar will always be generated.
                            pillar = true;
                        }
                    }
                    if(z != 3){ //Check if you're not on the southern edge of the chunk. If you aren't, proceed.
                        if(region.getBlockState(new BlockPos(startX + x * 4 + 3, 2 + 6 * y, startZ + z * 4 + 4))!=Blocks.AIR.getDefaultState()){ //Check one block south whether there's a wall there. If so, a pillar will always be generated.
                            pillar = true;
                        }
                    }
                    if(x == 3 && z == 3){ //If you're on the southeasternmost spot on the chunk, always make a pillar.
                        pillar = true;
                    }
                    pillar = pillar||(random.nextFloat() < 0.2F); //Sometimes generate a pillar anyways, even if none of the previous conditions were met.
                    if(pillar){
                        //Create the pillar.
                        for (int j = 0; j < 4; j++){
                            region.setBlockState(new BlockPos(startX + x * 4 + 3, 2 + 6 * y + j, startZ + z * 4 + 3), BackroomsBlocks.PATTERNED_WALLPAPER.getDefaultState(), Block.FORCE_STATE, 0);
                        }
                    }
                    // Generate the carpeting and the ceiling.
                    for(int i = 0; i < 4; i++){
                        for(int j = 0; j < 4; j++){
                            region.setBlockState(new BlockPos(startX + x * 4 + i, 1 + 6 * y, startZ + z * 4 + j), BackroomsBlocks.WOOLEN_CARPET.getDefaultState(), Block.FORCE_STATE, 0);
                            region.setBlockState(new BlockPos(startX + x * 4 + i, 6 + 6 * y, startZ + z * 4 + j), BackroomsBlocks.CORK_TILE.getDefaultState(), Block.FORCE_STATE, 0);
                        }
                    }
                    region.setBlockState(new BlockPos(startX + x * 4 + 1, 6 + 6 * y, startZ + z * 4 + 1), BackroomsBlocks.FLUORESCENT_LIGHT.getDefaultState(), Block.FORCE_STATE, 0); //Place a ceiling light.
                    //Commented former code: generateNbt(region, chunkPos.getStartPos().add(x * 4, 1+6*y, z * 4), "backrooms_" + ((random.nextFloat() < 0.4F ? 1 : 0) + (random.nextFloat() < 0.4F ? 1 : 0) * 2));
                }
            }
            Random fullFloorRandom = new Random(region.getSeed() + MathHelper.hashCode(chunk.getPos().getStartX(), chunk.getPos().getStartZ(), y)); //Create an unique random Object for the current floor.
            if(fullFloorRandom.nextFloat() < 0.1F || true){ //Check whether a random number between zero and one is less than the number with an F directly after it. Currently, for debugging reasons, a "|| true" has been placed, which means that the following code will be excecuted anyways.
                //Place a large (7x7 block) room in the current chunk at the current floor.

                //Choose a spot in the chunk.
                int x=fullFloorRandom.nextInt(3);
                int z=fullFloorRandom.nextInt(3);
                //Choose the room that will be placed.
                int roomNumber = (fullFloorRandom.nextInt(12) + 1);
                if(fullFloorRandom.nextFloat() < 0.6F){ //The number with an F directly after it denotes the probability of an empty room being generated regardless.
                    roomNumber=0;
                }
                //Choose the rotation for the room.
                Direction dir = Direction.fromHorizontal(fullFloorRandom.nextInt(4));
                BlockRotation rotation = dir.equals(Direction.NORTH) ? BlockRotation.COUNTERCLOCKWISE_90 : dir.equals(Direction.EAST) ? BlockRotation.NONE : dir.equals(Direction.SOUTH) ? BlockRotation.CLOCKWISE_90 : BlockRotation.CLOCKWISE_180;
                //Fill the area the room will be placed in with air.
                for(int i = 0; i < 7; i++){
                    for(int j = 0; j < this.loadedStructures.get("backrooms_large_" + roomNumber).sizeY; j++){
                        for(int k = 0; k < 7; k++){
                            region.setBlockState(new BlockPos(startX + x * 4 + i, 2 + 6 * y + j, startZ + z * 4 + k), Blocks.AIR.getDefaultState(), Block.FORCE_STATE, 0);
                        }
                    }
                }
                generateNbt(region, new BlockPos(startX + x * 4, 2 + 6 * y, startZ + z * 4), "backrooms_large_" + roomNumber, rotation); //Actually generate the room.
            }
        }
        //Mold placement code; will be subject to heavy revisions, so ignore for now.
        for (int y = 5; y >= 0; y--) {
            Random fullFloorRandom = new Random(region.getSeed() + MathHelper.hashCode(chunk.getPos().getStartX(), chunk.getPos().getStartZ(), y));
            for(int i=0;i<300;i++){
                int x=fullFloorRandom.nextInt(16);
                int z=fullFloorRandom.nextInt(16);
                int x2=x+fullFloorRandom.nextInt(3)-1;
                int z2=fullFloorRandom.nextInt(3)-1;
                if(region.getBlockState(new BlockPos(startX + x, 1 + 6 * y, startZ + z))==BackroomsBlocks.WOOLEN_CARPET.getDefaultState()){
                    if(x2<0){x2=0;}
                    if(x2>15){x2=15;}
                    if(z2<0){z2=0;}
                    if(z2>15){z2=15;}
                    if(fullFloorRandom.nextFloat()<0.1F||region.getBlockState(new BlockPos(startX + x2, 1 + 6 * y, startZ + z2))==BackroomsBlocks.MOLDY_WOOLEN_CARPET.getDefaultState()){
                        region.setBlockState(new BlockPos(startX + x, 1 + 6 * y, startZ + z), BackroomsBlocks.MOLDY_WOOLEN_CARPET.getDefaultState(), Block.FORCE_STATE, 0);
                    }
                }
            }
        }
        // Place bedrock bricks at the bottom.
        for (int x = startX; x < startX + 16; x++) {
            for (int z = startZ; z < startZ + 16; z++) {
                region.setBlockState(new BlockPos(x, 0, z), BackroomsBlocks.BEDROCK_BRICKS.getDefaultState(), Block.FORCE_STATE, 0);
            }
        }
        // Place bedrock bricks at the roof of chunk
        for (int x = startX; x < startX + 16; x++) {
            for (int z = startZ; z < startZ + 16; z++) {
                region.setBlockState(new BlockPos(x, 1+4+2 + 6 * 5, z), BackroomsBlocks.BEDROCK_BRICKS.getDefaultState(), Block.FORCE_STATE, 0);
            }
        }


        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public void storeStructures(ServerWorld world) {
        store("backrooms_large", world, 0, 12); //Makes it so the large rooms can be used while generating.
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
}
