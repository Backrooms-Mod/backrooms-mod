package com.kpabr.backrooms.items;

import io.github.fablabsmc.fablabs.api.bannerpattern.v1.LoomPattern;
import io.github.fablabsmc.fablabs.api.bannerpattern.v1.LoomPatternItem;
import com.kpabr.backrooms.block.PlasterwallBlock;
import com.kpabr.backrooms.init.BackroomsBlocks;
import com.kpabr.backrooms.util.WallpaperType;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class WallpaperPatternItem extends LoomPatternItem {

	public final WallpaperType type;

	public WallpaperPatternItem(LoomPattern pattern, WallpaperType type, Settings settings) {
		super(pattern, settings);
		this.type = type;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos pos = context.getBlockPos();
		BlockState state = world.getBlockState(pos);
		Direction side = context.getSide();
		EnumProperty<WallpaperType> property = PlasterwallBlock.DIRECTION_MAP.get(side);

		if (state.isOf(BackroomsBlocks.PLASTERWALL) && !state.get(property).equals(this.type)) {
			world.setBlockState(pos, state.with(property, this.type));
			world.playSound(null, pos, SoundEvents.BLOCK_HONEY_BLOCK_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
			return ActionResult.SUCCESS;
		}

		return ActionResult.PASS;
	}

}
