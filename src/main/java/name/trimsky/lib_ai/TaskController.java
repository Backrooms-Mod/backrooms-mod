package name.trimsky.lib_ai;

import name.trimsky.lib_ai.tasks.Task;

import java.util.Stack;

/**
 * Simple stack based FSM
 */
public class TaskController {
    /**
     * I highly recommend to do not use this variable in your programs.
     * But for extraordinary cases I leaved it as public
     */
    public final Stack<Task> tasksStack;
    private Task idleTask;

    public TaskController(final Task idleTask) {
        if(idleTask == null) {
            throw new IllegalArgumentException("firstTask argument must be not null");
        }
        tasksStack = new Stack<>();
        this.idleTask = idleTask;
        this.idleTask.setTaskController(this);
        pushState(idleTask);
    }

    public Task getIdleTask() {
        return this.idleTask;
    }
    public void setIdleTask(Task newIdleTask) {
        this.idleTask = newIdleTask;
    }

    public void popState() {
        tasksStack.pop();
        if(tasksStack.empty()) {
            tasksStack.push(idleTask);
        }
    }

    public void pushState(Task task) {
        if(tasksStack.empty() || !tasksStack.peek().equals(task)) {
            task.setTaskController(this);
            tasksStack.push(task);
        }
    }

    public void tick() {
        tasksStack.peek().tick();
    }
}
