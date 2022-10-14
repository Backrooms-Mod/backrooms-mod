package com.kpabr.backrooms.block;

import java.util.Random;
import java.util.function.Consumer;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import com.kpabr.backrooms.block.entity.ComputerBlockEntity;
import com.kpabr.backrooms.init.BackroomsBlocks;
import org.jetbrains.annotations.Nullable;

public class WallpaperBlock extends Block {

	public WallpaperBlock(FabricBlockSettings fabricBlockSettings) { super(fabricBlockSettings);}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		Random rand = world.random;
		ItemStack itemStack = player.getStackInHand(hand);

		if (itemStack.getItem() instanceof AxeItem) {

			if (!world.isClient) {
				itemStack.damage(1, (LivingEntity) player, (Consumer<LivingEntity>) ((e) -> {
					((LivingEntity) e).sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
				}));
			}
			world.setBlockState(pos, Blocks.OAK_PLANKS.getDefaultState());
			player.playSound(SoundEvents.ITEM_AXE_STRIP, 0.5f, rand.nextFloat());
			return ActionResult.SUCCESS;
		} else {
			return ActionResult.FAIL;
		}
	}
}