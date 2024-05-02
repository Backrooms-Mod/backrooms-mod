package com.kpabr.backrooms;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.kpabr.backrooms.init.BackroomsBlocks;
import com.kpabr.backrooms.init.BackroomsLevels;
import com.kpabr.backrooms.world.biome.sources.LevelOneBiomeSource;
import com.kpabr.backrooms.world.chunk.level1chunkgenerators.CementHallsChunkGenerator;
import com.kpabr.backrooms.world.chunk.level1chunkgenerators.ParkingGarageChunkGenerator;
import com.kpabr.backrooms.world.chunk.level1chunkgenerators.WarehouseChunkGenerator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;

public class LevelOneChunkGenerator extends ChunkGenerator {
	public static final Codec<LevelOneChunkGenerator> CODEC = RecordCodecBuilder.create((instance) ->
			method_41042(instance).and(
				RegistryOps.createRegistryCodec(Registry.BIOME_KEY).forGetter((generator) -> generator.biomeRegistry)
			)
			.apply(instance, instance.stable(LevelOneChunkGenerator::new))
	);

	
    private CementHallsChunkGenerator cementHallsChunkGenerator;
    private ParkingGarageChunkGenerator parkingGarageChunkGenerator;
    private WarehouseChunkGenerator warehouseChunkGenerator;
    private static final BlockState PATTERNED_WALLPAPER = BackroomsBlocks.PATTERNED_WALLPAPER.getDefaultState();
    private static final BlockState WOOLEN_CARPET = BackroomsBlocks.WOOLEN_CARPET.getDefaultState();
    private static final BlockState MOLDY_WOOLEN_CARPET = BackroomsBlocks.MOLDY_WOOLEN_CARPET.getDefaultState();
    private static final BlockState CORK_TILE = BackroomsBlocks.CORK_TILE.getDefaultState();
    private static final BlockState MOLDY_CORK_TILE = BackroomsBlocks.MOLDY_CORK_TILE.getDefaultState();
    private static final BlockState ROOF_BLOCK = BackroomsBlocks.BEDROCK_BRICKS.getDefaultState();

    private static final int ROOF_BEGIN_Y = 8 * (getFloorCount() + 1) + 1;
    
	private final Registry<Biome> biomeRegistry;

	public LevelOneChunkGenerator(Registry<StructureSet> registry, Registry<Biome> biomeRegistry) {
		super(registry, Optional.empty(), new LevelOneBiomeSource(biomeRegistry));
		this.biomeRegistry = biomeRegistry;
	}

	@Override
	protected Codec<? extends ChunkGenerator> getCodec() {
		return CODEC;
	}

	@Override
	public ChunkGenerator withSeed(long seed) {
		return this;
	}

	@Override
	public MultiNoiseUtil.MultiNoiseSampler getMultiNoiseSampler() {
        return null;
	}

	@Override
	public void carve(ChunkRegion chunkRegion, long l, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver carver) {
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

                    if (block == PATTERNED_WALLPAPER) {
                        replace(BackroomsBlocks.CEMENT_BRICKS, chunk, pos);
                    } else if (block == WOOLEN_CARPET || block == MOLDY_WOOLEN_CARPET) {
                        replace(BackroomsBlocks.CEMENT, chunk, pos);
                    } else if (block == CORK_TILE || block == MOLDY_CORK_TILE) {
                        replace(BackroomsBlocks.CEMENT_TILES, chunk, pos);
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
		return 128;
	}

	@Override
	public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, StructureAccessor structureAccessor, Chunk chunk) {

        if (this.cementHallsChunkGenerator == null) {
            this.cementHallsChunkGenerator = new CementHallsChunkGenerator(biomeSource, BackroomsLevels.LEVEL_1_WORLD.getSeed());
            this.parkingGarageChunkGenerator= new ParkingGarageChunkGenerator(biomeSource, BackroomsLevels.LEVEL_1_WORLD.getSeed());
            this.warehouseChunkGenerator = new WarehouseChunkGenerator(biomeSource, BackroomsLevels.LEVEL_1_WORLD.getSeed());
        }
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
        //Define a position for checking biomes
        final BlockPos biomePos = chunkPos.getBlockPos(0, 4, 4);

        //Save the first and last x and z position of the chunk. Note: positive x means east, positive z means south.
        final int startX = chunkPos.getStartX();
        final int endX = startX + 16;
        final int startZ = chunkPos.getStartZ();
        final int endZ = startZ  + 16;

        if(isBiomeEquals(BackroomsLevels.CEMENT_WALLS_BIOME, chunk, biomePos)) {
            this.cementHallsChunkGenerator.populateNoise(executor, blender, structureAccessor, chunk);
        }
        else if(isBiomeEquals(BackroomsLevels.PARKING_GARAGE_BIOME, chunk, biomePos)) {
            this.parkingGarageChunkGenerator.populateNoise(executor, blender, structureAccessor, chunk);
        }
        else if(isBiomeEquals(BackroomsLevels.WAREHOUSE_BIOME, chunk, biomePos)) {
            this.warehouseChunkGenerator.populateNoise(executor, blender, structureAccessor, chunk);
        }

        // Place bedrock bricks at the bottom.
        for (int x = startX; x < endX; x++) {
            for (int z = startZ; z < endZ; z++) {
                chunk.setBlockState(new BlockPos(x, 0, z), ROOF_BLOCK, false);
            }
        }

        // Place bedrock bricks at the roof of chunk
        for (int x = startX; x < endX; x++) {
            // 3 layers to be placed
            for (int z = startZ; z < endZ; z++) {
                chunk.setBlockState(new BlockPos(x, ROOF_BEGIN_Y, z), ROOF_BLOCK, false);
                chunk.setBlockState(new BlockPos(x, 1 + ROOF_BEGIN_Y, z), ROOF_BLOCK, false);
                chunk.setBlockState(new BlockPos(x, 2 + ROOF_BEGIN_Y, z), ROOF_BLOCK, false);
            }
        }

        return CompletableFuture.completedFuture(chunk);

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
	public int getHeight(int x, int z, Heightmap.Type heightmapType, HeightLimitView heightLimitView) {
		return 128;
	}

	@Override
	public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView heightLimitView) {
		return new VerticalBlockSample(0, new BlockState[0]);
	}

	@Override
	public void getDebugHudText(List<String> list, BlockPos blockPos) {
	}

    public static int getFloorCount() {
        return 5;
    }


    public void storeStructures(ServerWorld world) {
        this.parkingGarageChunkGenerator.storeStructures(world);
        this.cementHallsChunkGenerator.storeStructures(world);
        this.warehouseChunkGenerator.storeStructures(world);
    }

    private boolean isBiomeEquals(RegistryKey<Biome> biome, Chunk chunk, BlockPos biomePos) {
        return chunk.getBiomeForNoiseGen(biomePos.getX(), biomePos.getY(), biomePos.getZ()).matchesId(biome.getValue());
    }

    private void replace(Block block, Chunk chunk, BlockPos pos) {
        chunk.setBlockState(pos, block.getDefaultState(), false);
    }
}