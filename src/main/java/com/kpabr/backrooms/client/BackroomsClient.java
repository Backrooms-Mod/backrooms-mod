package com.kpabr.backrooms.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.SkyProperties;
import net.minecraft.util.Identifier;
import com.kpabr.backrooms.init.BackroomsBlocks;
import com.kpabr.backrooms.world.Level0;
import com.kpabr.backrooms.world.Level0Sky;

import java.util.Map;

public class BackroomsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {

		BlockRenderLayerMap.INSTANCE.putBlock(BackroomsBlocks.OFFICE_DOOR, RenderLayer.getTranslucent());
	}

	public void registerModSkies(Map<Identifier, SkyProperties> map) {
		SkyProperties Level0Sky = new Level0Sky();
		//map.put(Level0.LEVEL_0_ID, Level0Sky);
	}
}
