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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
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
        int startX = chunk.getPos().getStartX();
        int startZ = chunk.getPos().getStartZ();
        Random fullChunkRandom = new Random(region.getSeed() + MathHelper.hashCode(chunk.getPos().getStartX(), chunk.getPos().getStartZ(), -69420));
        for (int y = 5; y >= 0; y--) {
            for (int x = 3; x >= 0; x--) {
                for (int z = 3; z >= 0; z--) {
                    Random random = new Random(region.getSeed() + MathHelper.hashCode(chunk.getPos().getStartX(), chunk.getPos().getStartZ(), x + 4 * z + 20 * y));
                    int wallType = (random.nextFloat() < 0.4F ? 1 : 0) + (random.nextFloat() < 0.4F ? 1 : 0) * 2;
                    if((wallType & 1) == 1){
                        for(int i = 0; i < 3; i++){
                            for(int j = 0; j < 4; j++){
                                region.setBlockState(new BlockPos(startX + x * 4 + 3 , 2 + 6 * y + j, startZ + z * 4 + i), BackroomsBlocks.PATTERNED_WALLPAPER.getDefaultState(), Block.FORCE_STATE, 0);
                            }
                        }
                    }
                    if((wallType & 2) == 2){
                        for(int i = 0; i < 3; i++){
                            for(int j = 0; j < 4; j++){
                                region.setBlockState(new BlockPos(startX + x * 4 + i, 2 + 6 * y + j, startZ + z * 4 + 3), BackroomsBlocks.PATTERNED_WALLPAPER.getDefaultState(), Block.FORCE_STATE, 0);
                            }
                        }
                    }
                    boolean pillar = false;
                    if(x != 3){
                        if(region.getBlockState(new BlockPos(startX + x * 4 + 4, 2 + 6 * y, startZ + z * 4 + 3))!=Blocks.AIR.getDefaultState()){
                            pillar = true;
                        }
                    }
                    if(z != 3){
                        if(region.getBlockState(new BlockPos(startX + x * 4 + 3, 2 + 6 * y, startZ + z * 4 + 4))!=Blocks.AIR.getDefaultState()){
                            pillar = true;
                        }
                    }
                    if(x == 3 && z == 3){
                        pillar = true;
                    }
                    pillar = pillar||(random.nextFloat() < 0.2F);
                    if(pillar || wallType != 0){
                        for (int j = 0; j < 4; j++){
                            region.setBlockState(new BlockPos(startX + x * 4 + 3, 2 + 6 * y + j, startZ + z * 4 + 3), BackroomsBlocks.PATTERNED_WALLPAPER.getDefaultState(), Block.FORCE_STATE, 0);
                        }
                    }
                    for(int i = 0; i < 4; i++){
                        for(int j = 0; j < 4; j++){
                            region.setBlockState(new BlockPos(startX + x * 4 + i, 1 + 6 * y, startZ + z * 4 + j), BackroomsBlocks.WOOLEN_CARPET.getDefaultState(), Block.FORCE_STATE, 0);
                            region.setBlockState(new BlockPos(startX + x * 4 + i, 6 + 6 * y, startZ + z * 4 + j), BackroomsBlocks.CORK_TILE.getDefaultState(), Block.FORCE_STATE, 0);
                        }
                    }
                    region.setBlockState(new BlockPos(startX + x * 4 + 1, 6 + 6 * y, startZ + z * 4 + 1), BackroomsBlocks.FLUORESCENT_LIGHT.getDefaultState(), Block.FORCE_STATE, 0);
                    //generateNbt(region, chunkPos.getStartPos().add(x * 4, 1+6*y, z * 4), "backrooms_" + ((random.nextFloat() < 0.4F ? 1 : 0) + (random.nextFloat() < 0.4F ? 1 : 0) * 2));
                }
            }
        }
        for (int x = startX; x < startX + 16; x++) {
            for (int z = startZ; z < startZ + 16; z++) {
                region.setBlockState(new BlockPos(x, 0, z), Blocks.BEDROCK.getDefaultState(), Block.FORCE_STATE, 0);
            }
        }

        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public void storeStructures(ServerWorld world) {
        store("backrooms", world, 0, 3);
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
