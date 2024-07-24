package com.kpabr.backrooms.block;

import java.util.function.Consumer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.world.World;

public class PipeBlock extends AbstractPipeBlock {
	public final Block crackedPipe;

	public PipeBlock(Settings settings, Block crackedPipe) {
		super(settings);
		this.crackedPipe = crackedPipe;
		this.setDefaultState(this.stateManager.getDefaultState().with(NORTH, true).with(EAST, true).with(SOUTH, true)
				.with(WEST, true).with(UP, true).with(DOWN, true).with(WATERLOGGED, false));
	}

	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
			BlockHitResult hit) {
		ItemStack itemStack = player.getStackInHand(hand);

		if (itemStack.getItem() instanceof PickaxeItem) {
			if (!world.isClient) {
				itemStack.damage(1, player,
						(Consumer<LivingEntity>) ((e) -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND)));
			}
			world.setBlockState(pos, this.crackedPipe.getStateWithProperties(state), Block.FORCE_STATE, 0);
			return ActionResult.SUCCESS;
		}
		return ActionResult.FAIL;
	}

	@Override
	protected boolean shouldConnect(BlockState blockState) {
		return blockState.isOf(this) || blockState.isOf(this.crackedPipe);
	}
}