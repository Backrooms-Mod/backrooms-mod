package com.kpabr.backrooms.world.chunk;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.world.BlockView;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.chunk.VerticalBlockSample;

public class MazeChunkGenerator extends ChunkGenerator {

	private BiomeSource biomeSource;
	private long seed;
	private int height;
	private int width;
	private int seaLevel;
	private BlockState walls;
	private BlockState base;
	private BlockState[] states;
	private ImmutableList<Pair<Boolean, OctavePerlinNoiseSampler>> noisemapOne = ImmutableList.of();
	private ImmutableList<Pair<Boolean, OctavePerlinNoiseSampler>> noisemapTwo = ImmutableList.of();
	private ImmutableList<Pair<Boolean, OctavePerlinNoiseSampler>> noisemapThree = ImmutableList.of();
	private ImmutableList<Pair<Boolean, OctavePerlinNoiseSampler>> noisemapFour = ImmutableList.of();
	private List<Boolean> northNoisemap;
	private List<Boolean> eastNoisemap;
	private List<Boolean> southNoisemap;
	private List<Boolean> westNoisemap;

	public static final Codec<MazeChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(Codec.INT.fieldOf("height").forGetter((chunkGenerator) -> {
			return chunkGenerator.height;
		}), Codec.INT.fieldOf("width").forGetter((chunkGenerator) -> {
			return chunkGenerator.width;
		}), Codec.INT.fieldOf("sea_level").forGetter((chunkGenerator) -> {
			return chunkGenerator.seaLevel;
		}), BlockState.CODEC.fieldOf("wall_block").forGetter((chunkGenerator) -> {
			return chunkGenerator.walls;
		}), BlockState.CODEC.fieldOf("base_block").forGetter((chunkGenerator) -> {
			return chunkGenerator.base;
		}), Codec.list(Codec.BOOL).fieldOf("north_noisemap").forGetter((chunkGenerator) -> {
			return chunkGenerator.northNoisemap;
		}), Codec.list(Codec.BOOL).fieldOf("east_noisemap").forGetter((chunkGenerator) -> {
			return chunkGenerator.eastNoisemap;
		}), Codec.list(Codec.BOOL).fieldOf("south_noisemap").forGetter((chunkGenerator) -> {
			return chunkGenerator.southNoisemap;
		}), Codec.list(Codec.BOOL).fieldOf("west_noisemap").forGetter((chunkGenerator) -> {
			return chunkGenerator.westNoisemap;
		}), BiomeSource.CODEC.fieldOf("biome_source").forGetter((chunkGenerator) -> {
			return chunkGenerator.biomeSource;
		}), Codec.LONG.fieldOf("seed").forGetter((chunkGenerator) -> {
			return chunkGenerator.seed;
		})).apply(instance, instance.stable(MazeChunkGenerator::new));
	});

	public MazeChunkGenerator(int height, int width, int seaLevel, BlockState walls, BlockState base, List<Boolean> northNoisemap, List<Boolean> eastNoisemap, List<Boolean> southNoisemap, List<Boolean> westNoisemap, BiomeSource biomeSource, long seed) {
		super(biomeSource, biomeSource, new StructuresConfig(false), seed);
		this.height = height;
		this.width = width;
		this.walls = walls;
		this.base = base;
		this.states = new BlockState[] { base, walls, Blocks.AIR.getDefaultState() };
		this.northNoisemap = northNoisemap;
		this.eastNoisemap = eastNoisemap;
		this.southNoisemap = southNoisemap;
		this.westNoisemap = westNoisemap;
		this.seaLevel = seaLevel;
		this.biomeSource = biomeSource;
		this.seed = seed;
		this.noisemapOne = createNoise(northNoisemap, seed ^ 3);
		this.noisemapTwo = createNoise(eastNoisemap, seed ^ 4);
		this.noisemapThree = createNoise(southNoisemap, seed ^ 5);
		this.noisemapFour = createNoise(westNoisemap, seed ^ 6);
	}

	@Override
	protected Codec<? extends ChunkGenerator> getCodec() {
		return CODEC;
	}

	@Override
	public ChunkGenerator withSeed(long seed) {
		return new MazeChunkGenerator(this.height, this.width, this.seaLevel, this.walls, this.base, this.northNoisemap, this.eastNoisemap, this.southNoisemap, this.westNoisemap, this.biomeSource, seed);
	}

	@Override
	public void buildSurface(ChunkRegion world, Chunk chunk) {

	}

	@Override
	public CompletableFuture<Chunk> populateNoise(Executor exec, StructureAccessor accessor, Chunk chunk) {
		for (int y = 0; y < 255; y++) {
			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					BlockPos pos = chunk.getPos().getStartPos().add(x, y, z);
					if (pos.getY() < this.getSeaLevel() || pos.getY() >= this.getSeaLevel() + this.height) {
						chunk.setBlockState(pos, states[0], true);
					} else {
						Random random = new Random(seed ^ pos.getX() * pos.getZ() ^ chunk.getPos().toLong());
						// Rooms
						boolean north = this.noisemapOne.stream().max(Comparator.comparing((entry) -> {
							return ((Pair<Boolean, OctavePerlinNoiseSampler>) entry).getRight().sample(pos.getX() / 5, 0, pos.getZ() / 5);
						})).get().getLeft();
						boolean east = this.noisemapTwo.stream().max(Comparator.comparing((entry) -> {
							return ((Pair<Boolean, OctavePerlinNoiseSampler>) entry).getRight().sample(pos.getX() / 5, 0, pos.getZ() / 5);
						})).get().getLeft();
						boolean south = this.noisemapThree.stream().max(Comparator.comparing((entry) -> {
							return ((Pair<Boolean, OctavePerlinNoiseSampler>) entry).getRight().sample(pos.getX() / 5, 0, pos.getZ() / 5);
						})).get().getLeft();
						boolean west = this.noisemapFour.stream().max(Comparator.comparing((entry) -> {
							return ((Pair<Boolean, OctavePerlinNoiseSampler>) entry).getRight().sample(pos.getX() / 5, 0, pos.getZ() / 5);
						})).get().getLeft();

						int size = width;
						if (pos.getX() % size == 0 && pos.getZ() % size == 0) {
							buildRoom(chunk, pos, size, random.nextBoolean() && random.nextBoolean() && random.nextBoolean() ? north : !north, random.nextBoolean() && random.nextBoolean() && random.nextBoolean() ? east : !east, random.nextBoolean() && random.nextBoolean() && random.nextBoolean() ? south : !south, random.nextBoolean() && random.nextBoolean() && random.nextBoolean() ? west : !west, states[1]);
							// Cleanup
							if (chunk.getBlockState(pos).isOf(states[1].getBlock())) {
								if (chunk.getBlockState(pos.north()).isOf(states[1].getBlock()) && chunk.getBlockState(pos.east()).isOf(states[1].getBlock()) && chunk.getBlockState(pos.south()).isOf(states[1].getBlock()) && chunk.getBlockState(pos.west()).isOf(states[1].getBlock())) {
									buildWalls(chunk, pos, size, random.nextBoolean() && random.nextBoolean(), random.nextBoolean() && random.nextBoolean(), random.nextBoolean() && random.nextBoolean(), random.nextBoolean() && random.nextBoolean(), states[2]);
									chunk.setBlockState(pos, states[1], true);
								}
							}
						}
					}
				}
			}
		}
		return CompletableFuture.completedFuture(chunk); //useless, this is to stop the error
	}

	private void buildRoom(Chunk world, BlockPos pos, int size, boolean north, boolean east, boolean south, boolean west, BlockState state) {
		world.setBlockState(pos.add(0, 0, 0), state, true);
		world.setBlockState(pos.add(size, 0, 0), state, true);
		world.setBlockState(pos.add(0, 0, size), state, true);
		world.setBlockState(pos.add(size, 0, size), state, true);

		buildWalls(world, pos, size, north, east, south, west, state);
	}

	private void buildWalls(Chunk world, BlockPos pos, int size, boolean north, boolean east, boolean south, boolean west, BlockState state) {
		if (north) {
			BlockPos.iterate(pos, pos.add(size, 0, 0)).forEach((blockPos) -> {
				world.setBlockState(blockPos, state, true);
			});
		}
		if (east) {
			BlockPos.iterate(pos.add(size, 0, 0), pos.add(size, 0, size)).forEach((blockPos) -> {
				world.setBlockState(blockPos, state, true);
			});
		}
		if (south) {
			BlockPos.iterate(pos.add(0, 0, size), pos.add(size, 0, size)).forEach((blockPos) -> {
				world.setBlockState(blockPos, state, true);
			});
		}
		if (west) {
			BlockPos.iterate(pos, pos.add(0, 0, size)).forEach((blockPos) -> {
				world.setBlockState(blockPos, state, true);
			});
		}
	}

	@Override
	public int getSeaLevel() {
		return this.seaLevel;
	}

	@Override
	public int getHeight(int x, int z, Type heightmap, HeightLimitView world) {
		return this.getSeaLevel() + this.height;
	}

	@Override
	public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world) {
		return new VerticalBlockSample(world.getHeight(), states);
	}

	public static <P> ImmutableList<Pair<P, OctavePerlinNoiseSampler>> createNoise(List<P> aspects, long seed) {
		Builder<Pair<P, OctavePerlinNoiseSampler>> builder = new Builder<Pair<P, OctavePerlinNoiseSampler>>();

		for (Iterator<P> var4 = aspects.iterator(); var4.hasNext(); ++seed) {
			P layer = var4.next();
			builder.add(new Pair<P, OctavePerlinNoiseSampler>(layer, new OctavePerlinNoiseSampler(new ChunkRandom(seed), ImmutableList.of(-4))));
		}

		return builder.build();
	}

}
