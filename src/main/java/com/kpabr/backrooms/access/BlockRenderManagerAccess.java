package com.kpabr.backrooms.access;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;

public interface BlockRenderManagerAccess {

	public BakedModel getModelPure(BlockState state);

}
