package com.kpabr.backrooms.block;

import java.util.Random;
import java.util.function.Consumer;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.World;
import com.kpabr.backrooms.block.AbstractPipeBlock;

import java.util.Map;

public class PipeBlock extends AbstractPipeBlock{
	public final Block crackedPipe;
	public PipeBlock(Settings settings, Block crackedPipe) {
		super(settings);
		this.crackedPipe = crackedPipe;
		this.setDefaultState(this.stateManager.getDefaultState().with(NORTH, true).with(EAST, true).with(SOUTH, true).with(WEST, true).with(UP, true).with(DOWN, true).with(WATERLOGGED, false));
	}
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		Random rand = world.random;
		ItemStack itemStack = player.getStackInHand(hand);

		if (itemStack.getItem() instanceof PickaxeItem) {
			if (!world.isClient) {
				itemStack.damage(1, player, (Consumer<LivingEntity>) ((e) ->
						e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND)
				));
			}
			world.setBlockState(pos, this.crackedPipe.getStateWithProperties(state), Block.FORCE_STATE, 0);
			//player.playSound(BackroomsSounds.ITEM_AXE_STRIP, 0.5f, rand.nextFloat());
			return ActionResult.SUCCESS;
		}
		return ActionResult.FAIL;
	}
	@Override
	protected boolean shouldConnect(BlockState blockState){
		return blockState.isOf(this)||blockState.isOf(this.crackedPipe);
	}
}