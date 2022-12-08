package com.kpabr.backrooms.world.chunk.level1chunkgenerators;


import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.init.BackroomsBlocks;
import com.kpabr.backrooms.world.chunk.LevelOneChunkGenerator;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ludocrypt.limlib.api.LiminalUtil;
import net.ludocrypt.limlib.api.world.AbstractNbtChunkGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.loot.LootTables;
import net.minecraft.server.world.ChunkHolder.Unloaded;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
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

public class WarehouseChunkGenerator extends AbstractNbtChunkGenerator {
    public static final Codec<WarehouseChunkGenerator> CODEC = RecordCodecBuilder.create((instance) ->
         instance.group(
                 BiomeSource.CODEC.fieldOf("biome_source")
                         .stable()
                         .forGetter((chunkGenerator) -> chunkGenerator.biomeSource),
                 Codec.LONG.fieldOf("seed")
                         .stable()
                         .forGetter((chunkGenerator) -> chunkGenerator.worldSeed)
         ).apply(instance, instance.stable(WarehouseChunkGenerator::new))
    );


    private final long worldSeed;
    private static final int ROOF_BEGIN_Y = 6 * (LevelOneChunkGenerator.getFloorCount() + 1) + 1;
    public WarehouseChunkGenerator(BiomeSource biomeSource, long worldSeed) {
        super(new SimpleRegistry<>(Registry.STRUCTURE_SET_KEY, Lifecycle.stable(), null), Optional.empty(), biomeSource, biomeSource, worldSeed, BackroomsMod.id("level_1"), LiminalUtil.createMultiNoiseSampler());
        this.worldSeed = worldSeed;
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }
    @Override
    public ChunkGenerator withSeed(long seed) {
        return new WarehouseChunkGenerator(this.biomeSource, seed);
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
        // Save the starting x and z position of the chunk. Note: positive x means east, positive z means south.
        final int startX = chunkPos.getStartX();
        final int startZ = chunkPos.getStartZ();
        // Create 5 floors, top to bottom.
        final int floorCount = LevelOneChunkGenerator.getFloorCount();

        // Create 4 smaller sections of the floor, layed out in a 2x2 pattern.
        // Each section will consist of the carpeting, the ceiling, two walls
        // (located on the eastern and southern side of the section) and a pillar,
        // located in the southeasternmost space.
        for (int y = floorCount; y >= 0; y--) {
            for (int x = 1; x >= 0; x--) {
                for (int z = 1; z >= 0; z--) {
                    //Make a Random object controlling the generation of the section.
                    final Random random = new Random(region.getSeed() + MathHelper.hashCode(startX, startZ, x + 4 * z + 20 * y));
                    // Decide the arrangement of the walls of the section.
                    // The two numbers with an F directly after them denote the probability of
                    // an eastern wall and a southern wall generating, respectively.
                    final int wallType = (random.nextFloat() < 0.4F ? 1 : 0) + (random.nextFloat() < 0.4F ? 2 : 0);
                    final int shelfType = random.nextFloat() < 0.6F ? random.nextInt(4)+1 : 0;

                    // Check if the arrangement includes the eastern wall
                    // and create eastern wall if includes.
                    if ((wallType & 1) == 1) {
                        for(int i = 0; i < 7; i++){
                            for(int j = 0; j < 6; j++){
                                region.setBlockState(new BlockPos(startX + x * 8 + 7 , 2 + 8 * y + j, startZ + z * 8 + i), BackroomsBlocks.CEMENT_BRICKS.getDefaultState(), Block.FORCE_STATE, 0);
                            }
                        }
                    }
                    // Check if the arrangement includes the southern wall
                    // and create southern wall if includes
                    if ((wallType & 2) == 2) {
                        for(int i = 0; i < 7; i++){
                            for(int j = 0; j < 6; j++){
                                region.setBlockState(new BlockPos(startX + x * 8 + i, 2 + 8 * y + j, startZ + z * 8 + 7), BackroomsBlocks.CEMENT_BRICKS.getDefaultState(), Block.FORCE_STATE, 0);
                            }
                        }
                    }
                    // Boolean variable controlling whether a pillar is generated. Initially false.
                    boolean pillar;

                    // If there's a wall in the current section, always create a pillar.
                    pillar = wallType != 0;

                    // Check if you're not on the eastern edge of the chunk. If you aren't, proceed.
                    if (x != 3) {
                        // Check one block east whether there's a wall there. If so, a pillar will always be generated.
                        pillar = pillar || !region.getBlockState(new BlockPos(startX + x * 8 + 8, 2 + 8 * y, startZ + z * 8 + 7)).isAir();
                    }
                    // Check if you're not on the southern edge of the chunk. If you aren't, proceed.
                    if (z != 3) {
                        // Check one block south whether there's a wall there. If so, a pillar will always be generated.
                        pillar = pillar || !region.getBlockState(new BlockPos(startX + x * 8 + 7, 2 + 8 * y, startZ + z * 8 + 8)).isAir();
                    }
                    // If you're on the southeasternmost spot on the chunk, always make a pillar.
                    pillar = pillar || (x == 3 && z == 3);

                    //Sometimes generate a pillar anyways, even if none of the previous conditions were met.
                    pillar = pillar || (random.nextFloat() < 0.2F);

                    // Create the pillar.
                    if (pillar) {
                        for (int j = 0; j < 6; j++){
                            region.setBlockState(
                                    new BlockPos(startX + x * 8 + 7, 2 + 8 * y + j, startZ + z * 8 + 7),
                                    BackroomsBlocks.CEMENT_BRICKS.getDefaultState(),
                                    Block.FORCE_STATE, 0);
                        }
                    }

                    // Generate the carpeting and the ceiling.
                    for(int i = 0; i < 8; i++){
                        for(int j = 0; j < 8; j++){
                            region.setBlockState(new BlockPos(startX + x * 8 + i, 1 + 8 * y, startZ + z * 8 + j), BackroomsBlocks.WOOLEN_CARPET.getDefaultState(), Block.FORCE_STATE, 0);
                            region.setBlockState(new BlockPos(startX + x * 8 + i, 8 + 8 * y, startZ + z * 8 + j), BackroomsBlocks.CORK_TILE.getDefaultState(), Block.FORCE_STATE, 0);
                        }
                    }

                    //Place a ceiling light at the correct height.
                    region.setBlockState(
                            new BlockPos(startX + x * 8 + 3, 8 + 8 * y, startZ + z * 8 + 3),
                            BackroomsBlocks.FLUORESCENT_LIGHT.getDefaultState(),
                            Block.FORCE_STATE, 0); //Place a ceiling light.
                    //Commented former code: generateNbt(region, chunkPos.getStartPos().add(x * 4, 1+6*y, z * 4), "backrooms_" + ((random.nextFloat() < 0.4F ? 1 : 0) + (random.nextFloat() < 0.4F ? 1 : 0) * 2));
                }
            }
        }
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public void storeStructures(ServerWorld world) {
        /*store("warehouse", world, 0, 5); //Makes it so the large regular rooms can be used while generating.
        store("cement_halls", world, 1, 3); //Makes it so the large nofill rooms can be used while generating.*/
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
                    } else if (block == BackroomsBlocks.WOOLEN_CARPET.getDefaultState()) {
                        replace(BackroomsBlocks.CEMENT, chunk, pos);
                    } else if (block == BackroomsBlocks.MOLDY_WOOLEN_CARPET.getDefaultState()) {
                        replace(BackroomsBlocks.CEMENT, chunk, pos);
                    } else if (block == BackroomsBlocks.CORK_TILE.getDefaultState()) {
                        replace(BackroomsBlocks.CEMENT_TILES, chunk, pos);
                    } else if (block == BackroomsBlocks.MOLDY_CORK_TILE.getDefaultState()) {
                        replace(BackroomsBlocks.CEMENT_TILES, chunk, pos);
                    }
                }
            }
        }
    }
}
