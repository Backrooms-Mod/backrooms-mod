package name.trimsky.lib_ai.tasks;

import name.trimsky.lib_ai.TaskController;

public abstract class SingleTask<T> implements Task {
    /**
     * This variable is actually sets in TaskControllerManager, not available in
     * constructor!
     */
    protected TaskController controller;
    public T owner;

    public SingleTask(T owner) {
        this.owner = owner;
    }

    /**
     * Private implementation, but I leaved it public. It actually sets
     * task controller for task
     * 
     * @param controllerRef reference to already existing TaskController from
     *                      TaskControllerManager
     */
    public void setTaskController(TaskController controllerRef) {
        this.controller = controllerRef;
    }
}
