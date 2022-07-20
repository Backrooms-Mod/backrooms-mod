package com.kpabr.backrooms;

<<<<<<< Updated upstream
import java.util.Map;

=======
import com.kpabr.backrooms.config.BackroomsConfig;
import com.kpabr.backrooms.init.*;
>>>>>>> Stashed changes
import net.fabricmc.api.ModInitializer;
import com.kpabr.backrooms.init.BackroomsBiomes;
import com.kpabr.backrooms.init.BackroomsBlocks;
import com.kpabr.backrooms.init.BackroomsFeatures;
import com.kpabr.backrooms.init.BackroomsGroups;
import com.kpabr.backrooms.init.BackroomsItems;
import com.kpabr.backrooms.init.BackroomsLoomPatterns;
import com.kpabr.backrooms.world.Level0;
import com.kpabr.backrooms.world.chunk.MazeChunkGenerator;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BackroomsMod implements ModInitializer {

	static public String ModId = "backrooms";

	@Override
	public void onInitialize() {
<<<<<<< Updated upstream
		Registry.register(Registry.CHUNK_GENERATOR, id("maze_chunk"), MazeChunkGenerator.CODEC);
		BackroomsLoomPatterns.init();
		BackroomsFeatures.init();
		BackroomsItems.init();
		BackroomsBiomes.init();
=======
		BackroomsConfig.init();
		LOGGER.info("Loaded config");
>>>>>>> Stashed changes
		BackroomsBlocks.init();
		BackroomsGroups.init();
<<<<<<< Updated upstream
=======
		LOGGER.info("Loaded groups");
		BackroomsItems.init();
		LOGGER.info("Loaded items");
		BackroomsLevels.init();



>>>>>>> Stashed changes
	}

	/*@Override
	public void registerModDimensions(Map<Identifier, ExtraDimension> registry) {
		registry.put(Level0.LEVEL_0_ID, new Level0());
	}*/

	public static Identifier id(String id) {
		return new Identifier("backrooms", id);
	}

}
