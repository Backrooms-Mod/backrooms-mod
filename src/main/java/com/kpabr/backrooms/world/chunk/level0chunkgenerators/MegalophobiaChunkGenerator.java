package com.kpabr.backrooms.world.chunk.level0chunkgenerators;


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
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil.MultiNoiseSampler;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.GenerationStep.Carver;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;

public class MegalophobiaChunkGenerator extends AbstractNbtChunkGenerator {
    public static final Codec<MegalophobiaChunkGenerator> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source")
                            .stable()
                            .forGetter((chunkGenerator) -> chunkGenerator.biomeSource),
                    Codec.LONG.fieldOf("seed")
                            .stable()
                            .forGetter((chunkGenerator) -> chunkGenerator.worldSeed)
            ).apply(instance, instance.stable(MegalophobiaChunkGenerator::new)));


    private final Random moldPlacementRandom;
    private final long worldSeed;
    private static final int ROOF_BEGIN_Y = 6 * (getFloorCount() + 1) + 1;
    private static final BlockState ROOF_BLOCK = BackroomsBlocks.BEDROCK_BRICKS.getDefaultState();

    public MegalophobiaChunkGenerator(BiomeSource biomeSource, long worldSeed) {
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
        return new MegalophobiaChunkGenerator(this.biomeSource, seed);
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
        //Save the starting x and z position of the chunk. Note: positive x means east, positive z means south.
        final int startX = chunkPos.getStartX();
        final int startZ = chunkPos.getStartZ();

        //Create 5 floors, top to bottom.
        for (int y = getFloorCount(); y >= 0; y--) {
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



    @Override
    public void carve(ChunkRegion chunkRegion, long seed, BiomeAccess biomeAccess, StructureAccessor structureAccessor,
            Chunk chunk, Carver generationStep) {
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world) {
        return null;
    }

    @Override
    public void getDebugHudText(List<String> text, BlockPos pos) {
    }

    @Override
    public int getMinimumY() {
        return 128;
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
    public void populateEntities(ChunkRegion region) {
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender,
            StructureAccessor structureAccessor, Chunk chunk) {
        return null;
    }
}
