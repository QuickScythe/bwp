package com.bwp.utils.queue;

import com.quiptmc.core.heartbeat.Flutter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class Queue implements Flutter {

    private final LinkedList<Supplier<Boolean>> queue = new LinkedList<>();
    private final List<Supplier<Boolean>> queue_add = new ArrayList<>();
    private final List<Supplier<Boolean>> queue_remove = new ArrayList<>();

    public void add(Supplier<Boolean> function) {
        queue_add.add(function);
    }

    public void remove(Supplier<Boolean> function) {
        queue_remove.add(function);
    }

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
