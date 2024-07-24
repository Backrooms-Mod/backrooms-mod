package name.trimsky.lib_ai.tasks;

import name.trimsky.lib_ai.LibAI;
import name.trimsky.lib_ai.TaskController;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.World;

@SuppressWarnings("unused")
public abstract class GroupTask implements Task {
    private TaskController controller;
    /**
     * To return normal execution for entity when leaved group
     */
    private final Long2ObjectOpenHashMap<Task> previousTasks;
    private final Object2ObjectOpenHashMap<Class<?>, Task> entityBehaviour;
    private final long groupId;

    public GroupTask(World world) {
        groupId = LibAI.generateNewUniqueId(world, this);
        previousTasks = new Long2ObjectOpenHashMap<>();
        entityBehaviour = new Object2ObjectOpenHashMap<>();
    }

    public <T> GroupTask WithCustomBehaviourFor(Class<T> entityClass, Task task) {
        this.entityBehaviour.put(entityClass, task);
        return this;
    }

    public GroupTask SetDefaultBehaviour(Task task) {
        entityBehaviour.defaultReturnValue(task);
        return this;
    }

    public synchronized void onAttach(long entityId, Class<?> entityClass) {
        /*
         * final var entityTaskController = LibAI.getTaskControllerByEntityId(entityId);
         * 
         * previousTasks.put(entityId, entityTaskController.getIdleTask());
         * entityTaskController.setIdleTask(entityBehaviour.get(entityClass));
         */
    }

    public void onDetach(long entityId) {
        /*
         * LibAI.getTaskControllerByEntityId(entityId)
         * .setIdleTask(previousTasks.get(entityId));
         * synchronized (previousTasks) {
         * previousTasks.remove(entityId);
         * }
         */
    }

    @Override
    public void setTaskController(TaskController controller) {
        this.controller = controller;
    }
}
