package com.bwp.utils.queue;

import com.quiptmc.core.heartbeat.Flutter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Lightweight cooperative task queue executed by the Quipt Heartbeat.
 * <p>
 * Tasks are Suppliers that return true when completed; returning false re-enqueues
 * the task for a future tick. Additions/removals are staged to avoid concurrent
 * modification during iteration.
 */
public class Queue implements Flutter {

    private final LinkedList<Supplier<Boolean>> queue = new LinkedList<>();
    private final List<Supplier<Boolean>> queue_add = new ArrayList<>();
    private final List<Supplier<Boolean>> queue_remove = new ArrayList<>();

    /**
     * Adds a task to be executed on subsequent heartbeat ticks.
     *
     * @param function supplier returning true when the task is finished; false to retry later
     */
    public void add(Supplier<Boolean> function) {
        queue_add.add(function);
    }

    /**
     * Requests removal of a task from the queue.
     *
     * @param function the task supplier previously added
     */
    public void remove(Supplier<Boolean> function) {
        queue_remove.add(function);
    }

    /**
     * Heartbeat callback: processes staged additions/removals and runs each task once.
     * Tasks that return false are re-queued for future execution.
     *
     * @return always true to keep the heartbeat alive
     */
    @Override
    public boolean run() {
        queue.addAll(queue_add);
        queue_add.clear();
        queue.removeAll(queue_remove);
        queue_remove.clear();

        List<Supplier<Boolean>> toProcess = new ArrayList<>(queue);
        queue.clear();
        for (Supplier<Boolean> consumer : toProcess) {
            if(consumer.get()) continue;
            queue.add(consumer);
        }
        return true;
    }
}
