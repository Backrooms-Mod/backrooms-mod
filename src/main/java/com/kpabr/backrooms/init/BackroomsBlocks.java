package com.kpabr.backrooms.init;

import java.util.ArrayList;
import java.util.HashMap;

import com.kpabr.backrooms.block.entity.MaskBlockEntity;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
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
import net.minecraft.item.ItemGroups;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

@SuppressWarnings("unused")
public class BackroomsBlocks {

	private static HashMap<RegistryKey<ItemGroup>, ArrayList<Item>> GROUPED_ITEMS = new HashMap<RegistryKey<ItemGroup>, ArrayList<Item>>();

	public static ArrayList<ItemEntry> ITEMS = new ArrayList<>();
	private static final ArrayList<BlockEntityEntry> BLOCK_ENTITIES = new ArrayList<>();
	private static final ArrayList<BlockEntry> BLOCKS = new ArrayList<>();

	public static final Block CORK_TILE = add("cork_tile",
			new TileBlock(FabricBlockSettings.copyOf(Blocks.STONE).mapColor(DyeColor.WHITE)),
			ItemGroups.BUILDING_BLOCKS);

	public static final Block MOLDY_CORK_TILE = add("moldy_cork_tile", new MoldyTileBlock(
			FabricBlockSettings.copyOf(Blocks.BUDDING_AMETHYST).mapColor(DyeColor.WHITE).sounds(BlockSoundGroup.TUFF)),
			ItemGroups.BUILDING_BLOCKS);

	public static final Block TILEMOLD = add("tilemold",
			new TilemoldBlock(FabricBlockSettings.copyOf(Blocks.AMETHYST_CLUSTER).mapColor(DyeColor.BROWN).strength(0F)
					.noCollision().sounds(BlockSoundGroup.SMALL_DRIPLEAF)),
			ItemGroups.NATURAL);

	public static final Block FAKE_CEILING = add("fake_ceiling", new TileBlock(FabricBlockSettings.copyOf(Blocks.STONE)
			.mapColor(DyeColor.WHITE).noCollision().sounds(BlockSoundGroup.SMALL_DRIPLEAF)),
			ItemGroups.BUILDING_BLOCKS);

	public static final Block FLUORESCENT_LIGHT = add("fluorescent_light", new FluorescentLightBlock(
			FabricBlockSettings.copyOf(Blocks.REDSTONE_LAMP).ticksRandomly().requiresTool().mapColor(DyeColor.WHITE)),
			ItemGroups.FUNCTIONAL);

	public static final Block REPAIRED_FLUORESCENT_LIGHT = add("repaired_fluorescent_light",
			new RepairedFluorescentLightBlock(
					FabricBlockSettings.copyOf(Blocks.REDSTONE_LAMP).requiresTool().mapColor(DyeColor.WHITE)),
			ItemGroups.FUNCTIONAL);

	public static final Block PATTERNED_WALLPAPER = add("patterned_wallpaper",
			new WallpaperBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).mapColor(DyeColor.YELLOW)),
			ItemGroups.BUILDING_BLOCKS);

	public static final Block FAKE_WALL = add("fake_wall",
			new Block(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).mapColor(DyeColor.YELLOW).noCollision()),
			ItemGroups.BUILDING_BLOCKS);

	public static final Block STRIPED_WALLPAPER = add("striped_wallpaper",
			new WallpaperBlock(FabricBlockSettings.copyOf(PATTERNED_WALLPAPER)), ItemGroups.BUILDING_BLOCKS);

	public static final Block DOTTED_WALLPAPER = add("dotted_wallpaper",
			new WallpaperBlock(FabricBlockSettings.copyOf(PATTERNED_WALLPAPER)), ItemGroups.BUILDING_BLOCKS);

	public static final Block BLANK_WALLPAPER = add("blank_wallpaper",
			new WallpaperBlock(FabricBlockSettings.copyOf(PATTERNED_WALLPAPER)), ItemGroups.BUILDING_BLOCKS);

	public static final Block WOOLEN_CARPET = add("moist_carpet",
			new Block(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL).mapColor(DyeColor.YELLOW)),
			ItemGroups.BUILDING_BLOCKS);

	public static final Block FAKE_CARPET = add("fake_carpet",
			new Block(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL).mapColor(DyeColor.YELLOW).noCollision()),
			ItemGroups.BUILDING_BLOCKS);

	public static final Block MOLDY_WOOLEN_CARPET = add("moldy_carpet",
			new Block(FabricBlockSettings.copyOf(WOOLEN_CARPET)), ItemGroups.BUILDING_BLOCKS);

	// Cement
	public static final Block CEMENT = add("cement",
			new Block(FabricBlockSettings.copyOf(Blocks.STONE).mapColor(DyeColor.GRAY).sounds(BlockSoundGroup.TUFF)),
			ItemGroups.BUILDING_BLOCKS);

	public static final Block CEMENT_SLAB = add("cement_slab",
			new SlabBlock(FabricBlockSettings.copyOf(CEMENT)), ItemGroups.BUILDING_BLOCKS);

	public static final Block CEMENT_STAIRS = add("cement_stairs",
			new BackroomsStairsBlock(CEMENT.getDefaultState(), FabricBlockSettings.copyOf(CEMENT)),
			ItemGroups.BUILDING_BLOCKS);

	public static final Block CEMENT_WALL = add("cement_wall",
			new WallBlock(FabricBlockSettings.copyOf(CEMENT)), ItemGroups.BUILDING_BLOCKS);

	public static final Block CUT_CEMENT = add("cut_cement",
			new Block(FabricBlockSettings.copyOf(CEMENT)), ItemGroups.BUILDING_BLOCKS);

	public static final Block CUT_CEMENT_SLAB = add("cut_cement_slab",
			new SlabBlock(FabricBlockSettings.copyOf(CEMENT)), ItemGroups.BUILDING_BLOCKS);

	public static final Block CUT_CEMENT_STAIRS = add("cut_cement_stairs",
			new BackroomsStairsBlock(CEMENT.getDefaultState(), FabricBlockSettings.copyOf(CEMENT)),
			ItemGroups.BUILDING_BLOCKS);

	public static final Block CUT_CEMENT_WALL = add("cut_cement_wall",
			new WallBlock(FabricBlockSettings.copyOf(CEMENT)), ItemGroups.BUILDING_BLOCKS);

	public static final Block CEMENT_BRICKS = add("cement_bricks",
			new Block(FabricBlockSettings.copyOf(CEMENT)), ItemGroups.BUILDING_BLOCKS);

	public static final Block CEMENT_BRICK_SLAB = add("cement_brick_slab",
			new SlabBlock(FabricBlockSettings.copyOf(CEMENT)), ItemGroups.BUILDING_BLOCKS);

	public static final Block CEMENT_BRICK_STAIRS = add("cement_brick_stairs",
			new BackroomsStairsBlock(CEMENT.getDefaultState(), FabricBlockSettings.copyOf(CEMENT)),
			ItemGroups.BUILDING_BLOCKS);

	public static final Block CEMENT_BRICK_WALL = add("cement_brick_wall",
			new WallBlock(FabricBlockSettings.copyOf(CEMENT)), ItemGroups.BUILDING_BLOCKS);

	public static final Block CEMENT_TILES = add("cement_tiles",
			new Block(FabricBlockSettings.copyOf(CEMENT)), ItemGroups.BUILDING_BLOCKS);

	public static final Block CEMENT_TILE_SLAB = add("cement_tile_slab",
			new SlabBlock(FabricBlockSettings.copyOf(CEMENT)), ItemGroups.BUILDING_BLOCKS);

	public static final Block CEMENT_TILE_STAIRS = add("cement_tile_stairs",
			new BackroomsStairsBlock(CEMENT.getDefaultState(), FabricBlockSettings.copyOf(CEMENT)),
			ItemGroups.BUILDING_BLOCKS);

	public static final Block CEMENT_TILE_WALL = add("cement_tile_wall",
			new WallBlock(FabricBlockSettings.copyOf(CEMENT)), ItemGroups.BUILDING_BLOCKS);


	public static final Block HOTEL_CARPET = add("hotel_carpet",
			new Block(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL).mapColor(DyeColor.YELLOW)),
			ItemGroups.BUILDING_BLOCKS);


	public static final Block MACHINERY_BLOCK = add("machinery",
			new MachineryBlock(
					FabricBlockSettings.copyOf(Blocks.SHROOMLIGHT).mapColor(DyeColor.GRAY).sounds(BlockSoundGroup.TUFF)),
			ItemGroups.BUILDING_BLOCKS);

	public static final Block ROOF_WIRING = add(
			"roof_wiring", new RoofWiringBlock(FabricBlockSettings.copyOf(Blocks.STONE).mapColor(DyeColor.BROWN)
					.strength(0F).noCollision().sounds(BlockSoundGroup.SMALL_DRIPLEAF).nonOpaque()),
			ItemGroups.BUILDING_BLOCKS);

	public static final Block CRATE = add("crate", new CrateBlock(FabricBlockSettings.copyOf(Blocks.CHEST).nonOpaque()),
			ItemGroups.FUNCTIONAL);

	public static final BlockEntityType<CrateBlockEntity> CRATE_BLOCK_ENTITY = add("crate",
			FabricBlockEntityTypeBuilder.create(CrateBlockEntity::new, CRATE).build(null));

	public static final Block COMPUTER = add("computer",
			new ComputerBlock(FabricBlockSettings.copyOf(Blocks.REDSTONE_TORCH).collidable(true)
					.mapColor(DyeColor.YELLOW).nonOpaque()),
			ItemGroups.BUILDING_BLOCKS);

	public static final BlockEntityType<ComputerBlockEntity> COMPUTER_BLOCK_ENTITY = add("computer",
			FabricBlockEntityTypeBuilder.create(ComputerBlockEntity::new, COMPUTER).build(null));

	
	public static final Block HARLEQUIN_MASK = addWithoutItem("harlequin_mask",
			new MaskBlock(MaskType.HARLEQUIN, FabricBlockSettings.create().strength(1.0F).nonOpaque().noCollision()));

	public static final Block COLOMBINA_MASK = addWithoutItem("colombina_mask",
			new MaskBlock(MaskType.COLOMBINA, FabricBlockSettings.create().strength(1.0F).nonOpaque().noCollision()));

	public static final Block SOCK_BUSKIN_MASK = addWithoutItem("sock_buskin_mask",
			new MaskBlock(MaskType.SOCK_BUSKIN, FabricBlockSettings.create().strength(1.0F).nonOpaque().noCollision()));

	public static final BlockEntityType<MaskBlockEntity> MASK = add("mask", FabricBlockEntityTypeBuilder
			.create(MaskBlockEntity::new, HARLEQUIN_MASK, COLOMBINA_MASK, SOCK_BUSKIN_MASK)
			.build(null));


	public static final Block PYROIL = add("pyroil",
			new PyroilLineBlock(FabricBlockSettings.copyOf(Blocks.REDSTONE_WIRE).nonOpaque().mapColor(DyeColor.ORANGE)),
			ItemGroups.BUILDING_BLOCKS);


	public static final Block CRACKED_PIPE = add("cracked_pipe",
			new CrackedPipeBlock(FabricBlockSettings.copyOf(Blocks.STONE).nonOpaque()), ItemGroups.BUILDING_BLOCKS);

	public static final Block CRACKED_COPPER_PIPE = add("cracked_copper_pipe",
			new CrackedPipeBlock(FabricBlockSettings.copyOf(Blocks.STONE).nonOpaque()), ItemGroups.BUILDING_BLOCKS);

	public static final Block PIPE = add("pipe",
			new PipeBlock(FabricBlockSettings.copyOf(Blocks.STONE).nonOpaque().mapColor(DyeColor.GRAY), CRACKED_PIPE),
			ItemGroups.BUILDING_BLOCKS);

	public static final Block COPPER_PIPE = add("copper_pipe",
			new PipeBlock(FabricBlockSettings.copyOf(Blocks.STONE).nonOpaque().mapColor(DyeColor.ORANGE),
					CRACKED_COPPER_PIPE),
			ItemGroups.BUILDING_BLOCKS);

	public static final BlockEntityType<PyroilLineBlockEntity> PYROIL_LINE_BLOCK_ENTITY = add("pyroil",
			FabricBlockEntityTypeBuilder.create(PyroilLineBlockEntity::new, PYROIL).build(null));


	public static final Block RED_PATTERNED_WALLPAPER = add("red_patterned_wallpaper",
			new WallpaperBlock(FabricBlockSettings.copyOf(PATTERNED_WALLPAPER).mapColor(DyeColor.RED)
					.sounds(BlockSoundGroup.STEM)),
			ItemGroups.BUILDING_BLOCKS);

	public static final Block RED_STRIPED_WALLPAPER = add("red_striped_wallpaper",
			new WallpaperBlock(FabricBlockSettings.copyOf(RED_PATTERNED_WALLPAPER)),
			ItemGroups.BUILDING_BLOCKS);

	public static final Block RED_DOTTED_WALLPAPER = add("red_dotted_wallpaper",
			new WallpaperBlock(FabricBlockSettings.copyOf(RED_PATTERNED_WALLPAPER)), ItemGroups.BUILDING_BLOCKS);

	public static final Block RED_BLANK_WALLPAPER = add("red_blank_wallpaper",
			new WallpaperBlock(FabricBlockSettings.copyOf(RED_PATTERNED_WALLPAPER)), ItemGroups.BUILDING_BLOCKS);


	public static final Block FANCY_PILLAR = add("fancy_pillar", new PillarBlock(
			FabricBlockSettings.copyOf(Blocks.STONE).strength(2.0F, 8.0F).requiresTool().mapColor(DyeColor.YELLOW)),
			ItemGroups.BUILDING_BLOCKS);

	public static final Block CEMENT_PILLAR = add("cement_pillar",
			new PillarBlock(FabricBlockSettings.copyOf(Blocks.STONE).mapColor(DyeColor.GRAY)),
			ItemGroups.BUILDING_BLOCKS);

	public static final Block PARKING_CEMENT_PILLAR = add("parking_cement_pillar",
			new PillarBlock(FabricBlockSettings.copyOf(CEMENT_PILLAR).mapColor(DyeColor.GRAY)),
			ItemGroups.BUILDING_BLOCKS);

	public static final Block CEMENT_SANDWICH = add("cement_sandwich",
			new Block(FabricBlockSettings.copyOf(Blocks.STONE).mapColor(DyeColor.YELLOW)), ItemGroups.BUILDING_BLOCKS);

	public static final BlockEntityType<ComputerBlockEntity> TABLE_BLOCK_ENTITY = add("table",
			FabricBlockEntityTypeBuilder.create(ComputerBlockEntity::new, COMPUTER).build(null));


	public static final Block OFFICE_DOOR = add("office_door",
			new DoorBlock(FabricBlockSettings.copyOf(Blocks.DARK_OAK_DOOR), BlockSetType.DARK_OAK),
			ItemGroups.BUILDING_BLOCKS);

	public static final Block PLATE_DOOR = add("plate_door",
			new DoorBlock(FabricBlockSettings.copyOf(Blocks.IRON_DOOR), BlockSetType.IRON), ItemGroups.BUILDING_BLOCKS);


	public static final Block FIRESALT_BLOCK = add("firesalt_block",
			new Block(FabricBlockSettings.copyOf(Blocks.AMETHYST_BLOCK).mapColor(DyeColor.ORANGE)),
			ItemGroups.BUILDING_BLOCKS);

	public static final Block FIRESALT_CRYSTAL = add("firesalt_crystal",
			new FiresaltCrystalBlock(FabricBlockSettings.copyOf(Blocks.AMETHYST_CLUSTER).mapColor(DyeColor.ORANGE)),
			ItemGroups.BUILDING_BLOCKS);


	public static final Block BEDROCK_BRICKS = add("bedrock_bricks",
			new Block(FabricBlockSettings.copy(Blocks.BEDROCK)), ItemGroups.BUILDING_BLOCKS);

			
	public static final Block ALMOND_WATER = addWithoutItem("almond_water",
			new FluidBlock(BackroomsFluids.STILL_ALMOND_WATER, FabricBlockSettings.copyOf(Blocks.WATER)));

	// 16 types of various carpetings
	public static final Block BLACK_CARPETING = add("black_carpeting", new CarpetingBlock(DyeColor.BLACK),
			ItemGroups.BUILDING_BLOCKS);
	public static final Block BLUE_CARPETING = add("blue_carpeting", new CarpetingBlock(DyeColor.BLUE),
			ItemGroups.BUILDING_BLOCKS);
	public static final Block BROWN_CARPETING = add("brown_carpeting", new CarpetingBlock(DyeColor.BROWN),
			ItemGroups.BUILDING_BLOCKS);
	public static final Block CYAN_CARPETING = add("cyan_carpeting", new CarpetingBlock(DyeColor.CYAN),
			ItemGroups.BUILDING_BLOCKS);
	public static final Block GRAY_CARPETING = add("gray_carpeting", new CarpetingBlock(DyeColor.GRAY),
			ItemGroups.BUILDING_BLOCKS);
	public static final Block GREEN_CARPETING = add("green_carpeting", new CarpetingBlock(DyeColor.GREEN),
			ItemGroups.BUILDING_BLOCKS);
	public static final Block LIGHT_BLUE_CARPETING = add("light_blue_carpeting",
			new CarpetingBlock(DyeColor.LIGHT_BLUE), ItemGroups.BUILDING_BLOCKS);
	public static final Block LIGHT_GRAY_CARPETING = add("light_gray_carpeting",
			new CarpetingBlock(DyeColor.LIGHT_GRAY), ItemGroups.BUILDING_BLOCKS);
	public static final Block LIME_CARPETING = add("lime_carpeting", new CarpetingBlock(DyeColor.LIME),
			ItemGroups.BUILDING_BLOCKS);
	public static final Block MAGENTA_CARPETING = add("magenta_carpeting", new CarpetingBlock(DyeColor.MAGENTA),
			ItemGroups.BUILDING_BLOCKS);
	public static final Block ORANGE_CARPETING = add("orange_carpeting", new CarpetingBlock(DyeColor.ORANGE),
			ItemGroups.BUILDING_BLOCKS);
	public static final Block PINK_CARPETING = add("pink_carpeting", new CarpetingBlock(DyeColor.PINK),
			ItemGroups.BUILDING_BLOCKS);
	public static final Block PURPLE_CARPETING = add("purple_carpeting", new CarpetingBlock(DyeColor.PURPLE),
			ItemGroups.BUILDING_BLOCKS);
	public static final Block WHITE_CARPETING = add("white_carpeting", new CarpetingBlock(DyeColor.WHITE),
			ItemGroups.BUILDING_BLOCKS);
	public static final Block RED_CARPETING = add("red_carpeting", new CarpetingBlock(DyeColor.RED),
			ItemGroups.BUILDING_BLOCKS);
	public static final Block PORTAL_BLOCK = add("portal_block", new PortalBlock(), ItemGroups.BUILDING_BLOCKS);
	public static final Block NOCLIP_CARPETING = add("noclip_carpeting",
			new NoclipBlock(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL).noCollision().dropsNothing()),
			ItemGroups.FUNCTIONAL);
	public static final Block NOCLIP_WALL = add("noclip_wall",
			new NoclipBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).noCollision().dropsNothing()),
			ItemGroups.FUNCTIONAL);

	private static <T extends BlockEntity> BlockEntityType<T> add(String name, BlockEntityType<T> blockEntity) {
		Identifier id = BackroomsMod.id(name);
		BLOCK_ENTITIES.add(new BlockEntityEntry(id, blockEntity));
		return blockEntity;
	}

	private static <B extends Block> B add(String name, B block, RegistryKey<ItemGroup> tab) {
		BlockItem b = new BlockItem(block, new Item.Settings());

		if (!GROUPED_ITEMS.containsKey(tab)) {
			GROUPED_ITEMS.put(tab, new ArrayList<Item>());
		}

		GROUPED_ITEMS.get(tab).add(b);
		return add(name, block, b);
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
			Registry.register(Registries.ITEM, entry.identifier, entry.item);
		}
		for (BlockEntry entry : BLOCKS) {
			Registry.register(Registries.BLOCK, entry.identifier, entry.block);
		}
		for (BlockEntityEntry entry : BLOCK_ENTITIES) {
			Registry.register(Registries.BLOCK_ENTITY_TYPE, entry.identifier, entry.blockEntity);
		}

		registerCompostableBlocks();
		registerFlammableBlocks();
		registerFuels();

		for (RegistryKey<ItemGroup> groupKey : GROUPED_ITEMS.keySet()) {
			ItemGroupEvents.modifyEntriesEvent(groupKey)
					.register((itemGroup) -> addAllItemsToGroup(itemGroup, groupKey));
		}
	}

	private static void addAllItemsToGroup(FabricItemGroupEntries group, RegistryKey<ItemGroup> key) {
		if (GROUPED_ITEMS.containsKey(key)) {
			for (Item item : GROUPED_ITEMS.get(key)) {
				group.add(item);
			}
		}
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

	public record ItemEntry(Identifier identifier, BlockItem item) {
	}

	private record BlockEntry(Identifier identifier, Block block) {
	}

	private record BlockEntityEntry(Identifier identifier, BlockEntityType<?> blockEntity) {
	}
}