package name.trimsky.lib_ai;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

/**
 * Realization of task controller manager, used to efficiently update entity AI
 */
public final class SimpleTaskControllerManager {
    public final Long2ObjectOpenHashMap<TaskController> controllers = new Long2ObjectOpenHashMap<>();
    /**
     * Next free id for entity
     */
    public long uniqueId = 0;

    public void tick() {
        for(final var controller : controllers.values()) {
            controller.tick();
        }
    }
}
