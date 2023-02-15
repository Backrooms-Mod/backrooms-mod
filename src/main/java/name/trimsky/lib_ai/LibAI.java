package name.trimsky.lib_ai;

import name.trimsky.lib_ai.tasks.Task;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.world.World;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LibAI {
    public static final String modID = "lib_ai";
    public static boolean initialized = false;
    private static SimpleTaskControllerManager tasksManager;

    public static void initialize() {
        if(!initialized) {
            ServerTickEvents.START_SERVER_TICK.register(server -> tasksManager.tick());
            ServerWorldEvents.LOAD.register((server, world) -> tasksManager = new SimpleTaskControllerManager());

            initialized = true;
        }
    }

    public static synchronized void removeEntity(World world, long uniqueId) {
        if(!world.isClient) {
            tasksManager.controllers.remove(uniqueId);
        }
    }
    public static synchronized long generateNewUniqueId(World world, Task idleTask) {
        if(!world.isClient) {
            tasksManager.controllers.put(tasksManager.uniqueId, new TaskController(idleTask));
            return tasksManager.uniqueId++;
        } else {
            // Cannot be used on client
            return -1;
        }
    }
    public static TaskController getTaskControllerByEntityId(World world, long uniqueId) {
        if(world.isClient) throw new IllegalStateException("Called on client");
        return tasksManager.controllers.get(uniqueId);
    }

    /**
     * @return Old task controller
     */
    public static synchronized TaskController replaceTaskController(long uniqueId, TaskController newController) {
        return tasksManager.controllers.replace(uniqueId, newController);
    }
}
