package name.trimsky.lib_ai.tasks;

import name.trimsky.lib_ai.TaskController;

/**
 * Task is default interface for entities AI
 */
public interface Task {
    /**
     * a function that is called every tick
     */
    void tick();
    void setTaskController(TaskController controller);
}