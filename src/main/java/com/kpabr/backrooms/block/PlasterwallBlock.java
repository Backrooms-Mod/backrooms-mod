package com.kpabr.backrooms.block;

import java.util.Map;

import com.google.common.collect.Maps;

import com.kpabr.backrooms.util.WallpaperType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PlasterwallBlock extends Block {

	public static final EnumProperty<WallpaperType> UP_PAPER = EnumProperty.of("up", WallpaperType.class);
	public static final EnumProperty<WallpaperType> DOWN_PAPER = EnumProperty.of("down", WallpaperType.class);
	public static final EnumProperty<WallpaperType> NORTH_PAPER = EnumProperty.of("north", WallpaperType.class);
	public static final EnumProperty<WallpaperType> SOUTH_PAPER = EnumProperty.of("south", WallpaperType.class);
	public static final EnumProperty<WallpaperType> EAST_PAPER = EnumProperty.of("east", WallpaperType.class);
	public static final EnumProperty<WallpaperType> WEST_PAPER = EnumProperty.of("west", WallpaperType.class);

	public static final Map<Direction, EnumProperty<WallpaperType>> DIRECTION_MAP = Maps.newHashMap();
	public static final Map<EnumProperty<WallpaperType>, Direction> ENUM_MAP = Maps.newHashMap();

	public PlasterwallBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(UP_PAPER, WallpaperType.EMPTY).with(DOWN_PAPER, WallpaperType.EMPTY).with(NORTH_PAPER, WallpaperType.EMPTY).with(SOUTH_PAPER, WallpaperType.EMPTY).with(EAST_PAPER, WallpaperType.EMPTY).with(WEST_PAPER, WallpaperType.EMPTY));
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(UP_PAPER, DOWN_PAPER, NORTH_PAPER, SOUTH_PAPER, EAST_PAPER, WEST_PAPER);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		EnumProperty<WallpaperType> property = DIRECTION_MAP.get(hit.getSide());
		if (player.getStackInHand(hand).getItem().equals(Items.SHEARS) && !state.get(property).equals(WallpaperType.EMPTY)) {
			world.setBlockState(pos, state.with(property, WallpaperType.EMPTY));
			world.playSound(null, hit.getBlockPos(), SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.BLOCKS, 1.0F, 1.0F);
			return ActionResult.SUCCESS;
		}

		return ActionResult.PASS;
	}

	static {
		DIRECTION_MAP.put(Direction.UP, UP_PAPER);
		DIRECTION_MAP.put(Direction.DOWN, DOWN_PAPER);
		DIRECTION_MAP.put(Direction.NORTH, NORTH_PAPER);
		DIRECTION_MAP.put(Direction.SOUTH, SOUTH_PAPER);
		DIRECTION_MAP.put(Direction.EAST, EAST_PAPER);
		DIRECTION_MAP.put(Direction.WEST, WEST_PAPER);
		ENUM_MAP.put(UP_PAPER, Direction.UP);
		ENUM_MAP.put(DOWN_PAPER, Direction.DOWN);
		ENUM_MAP.put(NORTH_PAPER, Direction.NORTH);
		ENUM_MAP.put(SOUTH_PAPER, Direction.SOUTH);
		ENUM_MAP.put(EAST_PAPER, Direction.EAST);
		ENUM_MAP.put(WEST_PAPER, Direction.WEST);
	}

}
