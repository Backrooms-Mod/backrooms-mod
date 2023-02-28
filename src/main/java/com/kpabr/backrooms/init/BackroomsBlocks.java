package com.kpabr.backrooms.init;

import java.util.ArrayList;

import com.kpabr.backrooms.block.entity.MaskBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;

import com.kpabr.backrooms.BackroomsMod;
import com.kpabr.backrooms.block.*;
import com.kpabr.backrooms.block.MaskBlock.MaskType;
import com.kpabr.backrooms.block.entity.ComputerBlockEntity;
import com.kpabr.backrooms.block.entity.PyroilLineBlockEntity;
import com.kpabr.backrooms.block.entity.CrateBlockEntity;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@SuppressWarnings("unused")
public class BackroomsBlocks {

	public static ArrayList<ItemEntry> ITEMS = new ArrayList<>();
	private static final ArrayList<BlockEntityEntry> BLOCK_ENTITIES = new ArrayList<>();
	private static final ArrayList<BlockEntry> BLOCKS = new ArrayList<>();

	public static final Block CORK_TILE = add("cork_tile", new TileBlock(FabricBlockSettings.copyOf(Blocks.STONE).mapColor(DyeColor.WHITE)), ItemGroup.BUILDING_BLOCKS);
	public static final Block MOLDY_CORK_TILE = add("moldy_cork_tile", new MoldyTileBlock(FabricBlockSettings.copyOf(Blocks.BUDDING_AMETHYST).mapColor(DyeColor.WHITE).sounds(BlockSoundGroup.TUFF)), ItemGroup.BUILDING_BLOCKS);
	public static final Block TILEMOLD = add("tilemold", new TilemoldBlock(FabricBlockSettings.copyOf(Blocks.AMETHYST_CLUSTER).mapColor(DyeColor.BROWN).strength(0F).noCollision().sounds(BlockSoundGroup.SMALL_DRIPLEAF)), ItemGroup.BUILDING_BLOCKS);
	public static final Block FAKE_CEILING = add("fake_ceiling", new TileBlock(FabricBlockSettings.copyOf(Blocks.STONE).mapColor(DyeColor.WHITE).noCollision().sounds(BlockSoundGroup.SMALL_DRIPLEAF)), ItemGroup.BUILDING_BLOCKS);
	public static final Block FLUORESCENT_LIGHT = add("fluorescent_light", new FluorescentLightBlock(FabricBlockSettings.copyOf(Blocks.REDSTONE_LAMP).ticksRandomly().requiresTool().mapColor(DyeColor.WHITE)), ItemGroup.BUILDING_BLOCKS);
	public static final Block REPAIRED_FLUORESCENT_LIGHT = add("repaired_fluorescent_light", new RepairedFluorescentLightBlock(FabricBlockSettings.copyOf(Blocks.REDSTONE_LAMP).requiresTool().mapColor(DyeColor.WHITE)), ItemGroup.BUILDING_BLOCKS);
	public static final Block PATTERNED_WALLPAPER = add("patterned_wallpaper", new WallpaperBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).mapColor(DyeColor.YELLOW)), ItemGroup.BUILDING_BLOCKS);
	public static final Block FAKE_WALL = add("fake_wall", new Block(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).mapColor(DyeColor.YELLOW).noCollision()), ItemGroup.BUILDING_BLOCKS);
	public static final Block STRIPED_WALLPAPER = add("striped_wallpaper", new WallpaperBlock(FabricBlockSettings.copyOf(PATTERNED_WALLPAPER)), ItemGroup.BUILDING_BLOCKS);
	public static final Block DOTTED_WALLPAPER = add("dotted_wallpaper", new WallpaperBlock(FabricBlockSettings.copyOf(PATTERNED_WALLPAPER)), ItemGroup.BUILDING_BLOCKS);
	public static final Block BLANK_WALLPAPER = add("blank_wallpaper", new WallpaperBlock(FabricBlockSettings.copyOf(PATTERNED_WALLPAPER)), ItemGroup.BUILDING_BLOCKS);
	public static final Block WOOLEN_CARPET = add("moist_carpet", new Block(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL).mapColor(DyeColor.YELLOW)), ItemGroup.BUILDING_BLOCKS);
	public static final Block FAKE_CARPET = add("fake_carpet", new Block(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL).mapColor(DyeColor.YELLOW).noCollision()), ItemGroup.BUILDING_BLOCKS);
	public static final Block MOLDY_WOOLEN_CARPET = add("moldy_carpet", new Block(FabricBlockSettings.copyOf(WOOLEN_CARPET)), ItemGroup.BUILDING_BLOCKS);

	public static final Block CEMENT = add("cement",
			new Block(FabricBlockSettings.copyOf(Blocks.STONE).mapColor(DyeColor.GRAY).sounds(BlockSoundGroup.TUFF)), ItemGroup.BUILDING_BLOCKS);
	public static final Block CEMENT_SLAB = add("cement_slab",
			new SlabBlock(FabricBlockSettings.copyOf(CEMENT)), ItemGroup.BUILDING_BLOCKS);
	public static final Block CEMENT_STAIRS = add("cement_stairs",
			new BackroomsStairsBlock(CEMENT.getDefaultState(),FabricBlockSettings.copyOf(CEMENT)), ItemGroup.BUILDING_BLOCKS);
	public static final Block CEMENT_WALL = add("cement_wall",
			new WallBlock(FabricBlockSettings.copyOf(CEMENT)), ItemGroup.BUILDING_BLOCKS);
	public static final Block CUT_CEMENT = add("cut_cement",
			new Block(FabricBlockSettings.copyOf(CEMENT)), ItemGroup.BUILDING_BLOCKS);
	public static final Block CUT_CEMENT_SLAB = add("cut_cement_slab",
			new SlabBlock(FabricBlockSettings.copyOf(CEMENT)), ItemGroup.BUILDING_BLOCKS);
	public static final Block CUT_CEMENT_STAIRS = add("cut_cement_stairs",
			new BackroomsStairsBlock(CEMENT.getDefaultState(),FabricBlockSettings.copyOf(CEMENT)), ItemGroup.BUILDING_BLOCKS);
	public static final Block CUT_CEMENT_WALL = add("cut_cement_wall",
			new WallBlock(FabricBlockSettings.copyOf(CEMENT)), ItemGroup.BUILDING_BLOCKS);
	public static final Block CEMENT_BRICKS = add("cement_bricks",
			new Block(FabricBlockSettings.copyOf(CEMENT)), ItemGroup.BUILDING_BLOCKS);
	public static final Block CEMENT_BRICK_SLAB = add("cement_brick_slab",
			new SlabBlock(FabricBlockSettings.copyOf(CEMENT)), ItemGroup.BUILDING_BLOCKS);
	public static final Block CEMENT_BRICK_STAIRS = add("cement_brick_stairs",
			new BackroomsStairsBlock(CEMENT.getDefaultState(),FabricBlockSettings.copyOf(CEMENT)), ItemGroup.BUILDING_BLOCKS);
	public static final Block CEMENT_BRICK_WALL = add("cement_brick_wall",
			new WallBlock(FabricBlockSettings.copyOf(CEMENT)), ItemGroup.BUILDING_BLOCKS);
	public static final Block CEMENT_TILES = add("cement_tiles",
			new Block(FabricBlockSettings.copyOf(CEMENT)), ItemGroup.BUILDING_BLOCKS);
	public static final Block CEMENT_TILE_SLAB = add("cement_tile_slab",
			new SlabBlock(FabricBlockSettings.copyOf(CEMENT)), ItemGroup.BUILDING_BLOCKS);
	public static final Block CEMENT_TILE_STAIRS = add("cement_tile_stairs",
			new BackroomsStairsBlock(CEMENT.getDefaultState(),FabricBlockSettings.copyOf(CEMENT)), ItemGroup.BUILDING_BLOCKS);
	public static final Block CEMENT_TILE_WALL = add("cement_tile_wall",
			new WallBlock(FabricBlockSettings.copyOf(CEMENT)), ItemGroup.BUILDING_BLOCKS);

	public static final Block HOTEL_CARPET = add("hotel_carpet",
			new Block(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL).mapColor(DyeColor.YELLOW)), ItemGroup.BUILDING_BLOCKS);

	public static final Block MACHINERY_BLOCK = add("machinery_block",
			new MachineryBlock(FabricBlockSettings.copyOf(Blocks.STONE).mapColor(DyeColor.GRAY).sounds(BlockSoundGroup.TUFF)), ItemGroup.BUILDING_BLOCKS);
	public static final Block ROOF_WIRING = add("roof_wiring", new RoofWiringBlock(FabricBlockSettings.copyOf(Blocks.STONE).mapColor(DyeColor.BROWN).strength(0F).noCollision().sounds(BlockSoundGroup.SMALL_DRIPLEAF).nonOpaque()), ItemGroup.BUILDING_BLOCKS);

	public static final Block CRATE = add("crate", new CrateBlock(FabricBlockSettings.copyOf(CEMENT).nonOpaque()), ItemGroup.BUILDING_BLOCKS);
	public static final BlockEntityType<CrateBlockEntity> CRATE_BLOCK_ENTITY = add("crate", FabricBlockEntityTypeBuilder.create(CrateBlockEntity::new, CRATE).build(null));

	public static final Block COMPUTER = add("computer",
			new ComputerBlock(FabricBlockSettings.copyOf(Blocks.REDSTONE_TORCH).collidable(true).mapColor(DyeColor.YELLOW).nonOpaque()), ItemGroup.BUILDING_BLOCKS);
	public static final BlockEntityType<ComputerBlockEntity> COMPUTER_BLOCK_ENTITY = add("computer",
			FabricBlockEntityTypeBuilder.create(ComputerBlockEntity::new, COMPUTER).build(null));

	public static final Block HARLEQUIN_MASK = addWithoutItem("harlequin_mask",
			new MaskBlock(MaskType.HARLEQUIN, FabricBlockSettings.of(Material.DECORATION).strength(1.0F).nonOpaque().noCollision()));
	public static final Block COLOMBINA_MASK = addWithoutItem("colombina_mask",
			new MaskBlock(MaskType.COLOMBINA, FabricBlockSettings.of(Material.DECORATION).strength(1.0F).nonOpaque().noCollision()));
	public static final Block SOCK_BUSKIN_MASK = addWithoutItem("sock_buskin_mask",
			new MaskBlock(MaskType.SOCK_BUSKIN, FabricBlockSettings.of(Material.DECORATION).strength(1.0F).nonOpaque().noCollision()));
	public static final BlockEntityType<MaskBlockEntity> MASK = add("mask", FabricBlockEntityTypeBuilder
					.create(MaskBlockEntity::new, HARLEQUIN_MASK, COLOMBINA_MASK, SOCK_BUSKIN_MASK)
					.build(null));
	public static final Block PYROIL = add("pyroil",
			new Pyroil(FabricBlockSettings.copyOf(Blocks.REDSTONE_WIRE).nonOpaque().mapColor(DyeColor.ORANGE)), ItemGroup.BUILDING_BLOCKS);
	public static final Block PIPE = add("pipe",
			new PipeBlock(FabricBlockSettings.copyOf(Blocks.STONE).nonOpaque().mapColor(DyeColor.GRAY)), ItemGroup.BUILDING_BLOCKS);
	public static final Block COPPER_PIPE = add("copper_pipe",
			new PipeBlock(FabricBlockSettings.copyOf(Blocks.STONE).nonOpaque().mapColor(DyeColor.ORANGE)), ItemGroup.BUILDING_BLOCKS);

	public static final BlockEntityType<PyroilLineBlockEntity> PYROIL_LINE_BLOCK_ENTITY = add("pyroil",
			FabricBlockEntityTypeBuilder.create(PyroilLineBlockEntity::new, PYROIL).build(null));

	public static final Block RED_PATTERNED_WALLPAPER = add("red_patterned_wallpaper",
			new WallpaperBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).mapColor(DyeColor.YELLOW).sounds(BlockSoundGroup.STEM)), ItemGroup.BUILDING_BLOCKS);
	public static final Block RED_STRIPED_WALLPAPER = add("red_striped_wallpaper",
			new WallpaperBlock(FabricBlockSettings.copyOf(PATTERNED_WALLPAPER).sounds(BlockSoundGroup.STEM)), ItemGroup.BUILDING_BLOCKS);

	public static final Block RED_DOTTED_WALLPAPER = add("red_dotted_wallpaper", new WallpaperBlock(FabricBlockSettings.copyOf(PATTERNED_WALLPAPER)), ItemGroup.BUILDING_BLOCKS);
	public static final Block RED_BLANK_WALLPAPER = add("red_blank_wallpaper", new WallpaperBlock(FabricBlockSettings.copyOf(PATTERNED_WALLPAPER)), ItemGroup.BUILDING_BLOCKS);

	public static final Block FANCY_PILLAR = add("fancy_pillar", new PillarBlock(FabricBlockSettings.copyOf(Blocks.STONE).strength(2.0F, 8.0F).requiresTool().mapColor(DyeColor.YELLOW)), ItemGroup.BUILDING_BLOCKS);
	public static final Block CEMENT_PILLAR = add("cement_pillar", new PillarBlock(FabricBlockSettings.copyOf(Blocks.STONE).mapColor(DyeColor.GRAY)), ItemGroup.BUILDING_BLOCKS);
	public static final Block PARKING_CEMENT_PILLAR = add("parking_cement_pillar", new PillarBlock(FabricBlockSettings.copyOf(CEMENT_PILLAR).mapColor(DyeColor.GRAY)), ItemGroup.BUILDING_BLOCKS);
	public static final Block CEMENT_SANDWICH = add("cement_sandwich", new Block(FabricBlockSettings.copyOf(Blocks.STONE).mapColor(DyeColor.YELLOW)), ItemGroup.BUILDING_BLOCKS);
	public static final BlockEntityType<ComputerBlockEntity> TABLE_BLOCK_ENTITY = add("table", FabricBlockEntityTypeBuilder.create(ComputerBlockEntity::new, COMPUTER).build(null));
	public static final Block OFFICE_DOOR = add("office_door", new BackroomsDoorBlock(FabricBlockSettings.copyOf(PATTERNED_WALLPAPER).nonOpaque()), ItemGroup.BUILDING_BLOCKS);

	public static final Block FIRESALT_BLOCK = add("firesalt_block", new Block(FabricBlockSettings.copyOf(Blocks.COBBLESTONE).mapColor(DyeColor.ORANGE).collidable(true).sounds(BlockSoundGroup.DRIPSTONE_BLOCK)), ItemGroup.BUILDING_BLOCKS);
	public static final Block FIRESALT_CRYSTAL = add("firesalt_crystal", new FiresaltCrystalBlock(FabricBlockSettings.copyOf(Blocks.AMETHYST_CLUSTER).mapColor(DyeColor.ORANGE)), ItemGroup.BUILDING_BLOCKS);
	public static final Block BEDROCK_BRICKS = add("bedrock_bricks", new Block(FabricBlockSettings.copy(Blocks.BEDROCK)), ItemGroup.BUILDING_BLOCKS);
	public static final Block ALMOND_WATER = addWithoutItem("almond_water",
			new FluidBlock(BackroomsFluids.STILL_ALMOND_WATER, FabricBlockSettings.copyOf(Blocks.WATER)));

	// 16 types of various carpetings
	public static final Block BLACK_CARPETING = add("black_carpeting", new CarpetingBlock(DyeColor.BLACK), ItemGroup.BUILDING_BLOCKS);
	public static final Block BLUE_CARPETING = add("blue_carpeting", new CarpetingBlock(DyeColor.BLUE), ItemGroup.BUILDING_BLOCKS);
	public static final Block BROWN_CARPETING = add("brown_carpeting", new CarpetingBlock(DyeColor.BROWN), ItemGroup.BUILDING_BLOCKS);
	public static final Block CYAN_CARPETING = add("cyan_carpeting", new CarpetingBlock(DyeColor.CYAN), ItemGroup.BUILDING_BLOCKS);
	public static final Block GRAY_CARPETING = add("gray_carpeting", new CarpetingBlock(DyeColor.GRAY), ItemGroup.BUILDING_BLOCKS);
	public static final Block GREEN_CARPETING = add("green_carpeting", new CarpetingBlock(DyeColor.GREEN), ItemGroup.BUILDING_BLOCKS);
	public static final Block LIGHT_BLUE_CARPETING = add("light_blue_carpeting", new CarpetingBlock(DyeColor.LIGHT_BLUE), ItemGroup.BUILDING_BLOCKS);
	public static final Block LIGHT_GRAY_CARPETING = add("light_gray_carpeting", new CarpetingBlock(DyeColor.LIGHT_GRAY), ItemGroup.BUILDING_BLOCKS);
	public static final Block LIME_CARPETING = add("lime_carpeting", new CarpetingBlock(DyeColor.LIME), ItemGroup.BUILDING_BLOCKS);
	public static final Block MAGENTA_CARPETING = add("magenta_carpeting", new CarpetingBlock(DyeColor.MAGENTA), ItemGroup.BUILDING_BLOCKS);
	public static final Block ORANGE_CARPETING = add("orange_carpeting", new CarpetingBlock(DyeColor.ORANGE), ItemGroup.BUILDING_BLOCKS);
	public static final Block PINK_CARPETING = add("pink_carpeting", new CarpetingBlock(DyeColor.PINK), ItemGroup.BUILDING_BLOCKS);
	public static final Block PURPLE_CARPETING = add("purple_carpeting", new CarpetingBlock(DyeColor.PURPLE), ItemGroup.BUILDING_BLOCKS);
	public static final Block WHITE_CARPETING = add("white_carpeting", new CarpetingBlock(DyeColor.WHITE), ItemGroup.BUILDING_BLOCKS);
	public static final Block RED_CARPETING = add("red_carpeting", new CarpetingBlock(DyeColor.RED), ItemGroup.BUILDING_BLOCKS);
	private static <T extends BlockEntity> BlockEntityType<T> add(String name, BlockEntityType<T> blockEntity) {
		Identifier id = BackroomsMod.id(name);
		BLOCK_ENTITIES.add(new BlockEntityEntry(id, blockEntity));
		return blockEntity;
	}

	private static <B extends Block> B add(String name, B block, ItemGroup tab) {
		return add(name, block, new BlockItem(block, new Item.Settings().group(tab)));
	}

	private static <B extends Block> B add(String name, B block, BlockItem item) {
		add(name, block);
		if (item != null) {
			item.appendBlocks(Item.BLOCK_ITEMS, item);
			ITEMS.add(new ItemEntry(BackroomsMod.id(name), item));
		}
		return block;
	}

	private static <B extends Block> B addWithoutItem(String name, B block) {
		BLOCKS.add(new BlockEntry(BackroomsMod.id(name), block));
		return block;
	}

	private static <B extends Block> void add(String name, B block) {
		BLOCKS.add(new BlockEntry(BackroomsMod.id(name), block));
	}

	public static void init() {
		for (ItemEntry entry : ITEMS) {
			Registry.register(Registry.ITEM, entry.identifier, entry.item);
		}
		for (BlockEntry entry : BLOCKS) {
			Registry.register(Registry.BLOCK, entry.identifier, entry.block);
		}
		for (BlockEntityEntry entry : BLOCK_ENTITIES) {
			Registry.register(Registry.BLOCK_ENTITY_TYPE, entry.identifier, entry.blockEntity);
		}

		registerCompostableBlocks();
		registerFlammableBlocks();
		registerFuels();
	}

	private static void registerCompostableBlocks() {
	}

	private static void registerFlammableBlocks() {
		FlammableBlockRegistry registry = FlammableBlockRegistry.getDefaultInstance();
		registry.add(BackroomsBlocks.PYROIL, 1, 100);
	}

	private static void registerFuels() {
		FuelRegistry registry = FuelRegistry.INSTANCE;
		registry.add(PYROIL, 800);
	}

	private record ItemEntry(Identifier identifier, BlockItem item) { }
	private record BlockEntry(Identifier identifier, Block block) { }
	private record BlockEntityEntry(Identifier identifier, BlockEntityType<?> blockEntity) { }
}