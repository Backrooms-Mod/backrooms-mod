package name.trimsky.lib_ai.tasks;

import name.trimsky.lib_ai.TaskController;

/**
 * wrapper for undefined behaviour
 */
public class EmptyTask implements Task {
    @Override
    public void tick() {}

    @Override
    public void setTaskController(TaskController controller) {}
}
